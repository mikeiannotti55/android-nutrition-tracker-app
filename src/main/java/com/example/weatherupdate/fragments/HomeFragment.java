package com.example.weatherupdate.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.time.LocalDate;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.lang.Object;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherupdate.DBHelper;
import com.example.weatherupdate.R;
import com.example.weatherupdate.food;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment {
    DBHelper DB;
    ArrayList<food> foodsToday = new ArrayList<>();
    EditText etCountry, servingCountInp;
    TextView tvResult;
    private final String url = "https://api.nutritionix.com/v1_1/item";
    private final String appId = "46c094cc";
    private final String appKey = "3a8813a0666a139b9ef238c625144c5d";
    private PieChart pieChart;
    Button btnGet;
    View v;
    Button selectCard;
    String[] selectedCourses;
    ArrayList<Integer> courseList = new ArrayList<>();
    String[] courseArray = {"Biology", "English", "Physics", "Chemistry", "Computer"};

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);
        etCountry = v.findViewById(R.id.etCountry);
        tvResult = v.findViewById(R.id.tvResult);
        pieChart = v.findViewById(R.id.activity_main_piechart);
        btnGet = v.findViewById(R.id.btnGet);
        selectCard = v.findViewById(R.id.selectCard);
        DB = new DBHelper(getActivity());

        selectedCourses = new String[courseArray.length];
        servingCountInp = v.findViewById(R.id.servingCountInp);
        btnGet.setOnClickListener(this::getNutritionFacts);
        setupPieChart();
        selectCard.setOnClickListener(this::showCoursesDialog);

        return v;
    }

    public void showCoursesDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.requireActivity());
        builder.setTitle("Add Nutrition Facts");
        Context context = this.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nameBox = new EditText(context);
        nameBox.setHint("Name Of Product");
        layout.addView(nameBox);

        final EditText brandBox = new EditText(context);
        brandBox.setHint("Brand Name");
        layout.addView(brandBox);

        final EditText calBox = new EditText(context);
        calBox.setHint("Total Calories (1 serving)");
        layout.addView(calBox);

        final EditText carbBox = new EditText(context);
        carbBox.setHint("Total Carbohydrates");
        layout.addView(carbBox);

        final EditText fatBox = new EditText(context);
        fatBox.setHint("Total Fat");
        layout.addView(fatBox);

        final EditText proteinBox = new EditText(context);
        proteinBox.setHint("Total Protein");
        layout.addView(proteinBox);

        final EditText countBox = new EditText(context);
        countBox.setHint("Serving Count");
        layout.addView(countBox);

        builder.setView(layout);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //adds custom food
                try {
                    String name = nameBox.getText().toString();
                    String brand = brandBox.getText().toString();

                    int cals = Integer.parseInt(calBox.getText().toString());
                    int carbs = Integer.parseInt(carbBox.getText().toString());
                    int fats = Integer.parseInt(fatBox.getText().toString());
                    int protein = Integer.parseInt(proteinBox.getText().toString());

                    double count = Double.parseDouble(countBox.getText().toString());
                    foodsToday.add(new food(name, brand, cals, carbs, fats, protein, count, LocalDate.now()));
                    //add to database
                    food thisFood = foodsToday.get(foodsToday.size() - 1);
                    System.out.println(thisFood.getCalories());
                    Boolean checkinsertdata = DB.insertuserdata(thisFood.getName(), thisFood.getBrand(), thisFood.getCalories(), thisFood.getCarbohydrates(), thisFood.getFats(), thisFood.getProtein(), thisFood.getServingCount(), thisFood.getDate());
                    if (checkinsertdata == true) {
                        double percentCarbs = ((Double.parseDouble(String.valueOf(carbs)) * 4) / cals);
                        double percentFats = ((Double.parseDouble(String.valueOf(fats)) * 9) / cals);
                        double percentProtein = ((Double.parseDouble(String.valueOf(protein)) * 4) / cals);
                        loadPieChartData(percentCarbs, percentFats, percentProtein);
                        Toast.makeText(getActivity(), "New Entry Inserted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "New Entry Not Inserted", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception ignored) {
                    Toast.makeText(getActivity(), "All fields must be filled out correctly!", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    public void getNutritionFacts(View view) {
        String tempUrl = "";
        //ex. coke cherry --> 038000265013
        if (!String.valueOf(etCountry.getText()).equals("")) {
            tempUrl = url + "?upc=" + etCountry.getText() + "&appId=" + appId + "&appKey=" + appKey;
            System.out.println(etCountry.getText());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl, new Response.Listener<String>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(String response) {
                    Log.d("response", response);
                    String output = "";
                    try {
                        //displaying nutrition information
                        JSONObject jsonResponse = new JSONObject(response);
                        String itemName = jsonResponse.getString("item_name");
                        String brandName = jsonResponse.getString("brand_name");
                        String calories = jsonResponse.getString("nf_calories");
                        String servings = jsonResponse.getString("nf_servings_per_container");
                        String servingSizeQty = jsonResponse.getString("nf_serving_size_qty");
                        String servingSizeUnit = jsonResponse.getString("nf_serving_size_unit");
                        String totalFat = jsonResponse.getString("nf_total_fat");
                        String cholesterol = jsonResponse.getString("nf_cholesterol");
                        String sodium = jsonResponse.getString("nf_sodium");
                        String totalCarbohydrate = jsonResponse.getString("nf_total_carbohydrate");
                        String protein = jsonResponse.getString("nf_protein");

                        output += "Nutrition facts of " + itemName + " (" + brandName + "):"
                                + "\n Calories: " + calories
                                + "\n Servings per container: " + servings
                                + "\n Serving size: " + servingSizeQty + " " + servingSizeUnit
                                + "\n Total Fat: " + totalFat
                                + "\n Cholesterol: " + cholesterol
                                + "\n Sodium: " + sodium
                                + "\n Total carb: " + totalCarbohydrate
                                + "\n Protein: " + protein;
                        int calNum = Integer.parseInt(calories);
                        double percentCarbs = ((Double.parseDouble(totalCarbohydrate) * 4) / calNum);
                        double percentFats = ((Double.parseDouble(totalFat) * 9) / calNum);
                        double percentProtein = ((Double.parseDouble(protein) * 4) / calNum);
                        loadPieChartData(percentCarbs, percentFats, percentProtein);
                        tvResult.setText(output);
                        //add food to array
                        foodsToday.add(new food(itemName, brandName, Integer.parseInt(calories), Integer.parseInt(totalCarbohydrate), Integer.parseInt(totalFat), Integer.parseInt(protein), Double.parseDouble(servingCountInp.getText().toString()), LocalDate.now()));
                        //add food object to database
                        food thisFood = foodsToday.get(foodsToday.size() - 1);
                        System.out.println(thisFood.getName());
                        System.out.println(thisFood.getDate());
                        Boolean checkinsertdata = DB.insertuserdata(thisFood.getName(), thisFood.getBrand(), thisFood.getCalories(), thisFood.getCarbohydrates(), thisFood.getFats(), thisFood.getProtein(), thisFood.getServingCount(), thisFood.getDate());
                        if (checkinsertdata == true) {
                            Toast.makeText(getActivity(), "New Entry Inserted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "New Entry Not Inserted", Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity().getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            requestQueue.add(stringRequest);
        } else {
            Toast.makeText(getActivity(), "No barcode UPC specified!", Toast.LENGTH_SHORT).show();
        }
        servingCountInp.setVisibility(View.VISIBLE);
    }


    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(20f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Caloric Percentages");
        pieChart.setCenterTextSize(20f);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadPieChartData(double carbPercent, double fatPercent, double proteinPercent) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) carbPercent, "Carbohydrates"));
        entries.add(new PieEntry((float) fatPercent, "Fats"));
        entries.add(new PieEntry((float) proteinPercent, "Protein"));


        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "Macronutrients");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueTextSize(20f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(1400, Easing.EaseInOutQuad);
    }

    //also maybe add some nutrition goals so you can see how much you intake compared to goaals.
}