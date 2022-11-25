package com.example.weatherupdate.fragments;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.example.weatherupdate.DBHelper;
import com.example.weatherupdate.R;
import com.example.weatherupdate.food;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class CalendarFragment extends HomeFragment {
    Button btnFoodHistory, btnTodaysStats;
    DBHelper DB;
    TextView statsText;
    private PieChart todaysStatsChart;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_calendar, container, false);
        btnFoodHistory = v.findViewById(R.id.btnViewFoodHistory);
        btnTodaysStats = v.findViewById(R.id.btnViewTodaysStats);
        todaysStatsChart = v.findViewById(R.id.piechart2);
        setupPieChart2();
        btnTodaysStats.setOnClickListener(this::loadPieChartData2);
        DB = new DBHelper(getActivity());
        statsText = v.findViewById(R.id.todaysStatsText);

        btnFoodHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor res = DB.getdata();
                if (res.getCount() == 0) {
                    Toast.makeText(getActivity(), "No Entry Exists", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()) {
                    buffer.append("Name :" + res.getString(0) + "\n");
                    buffer.append("Brand :" + res.getString(1) + "\n");
                    buffer.append("Calories :" + res.getString(2) + "\n");
                    buffer.append("Total Carbohydrate :" + res.getString(3) + "\n");
                    buffer.append("Total Fat :" + res.getString(4) + "\n");
                    buffer.append("Protein :" + res.getString(5) + "\n");
                    buffer.append("Serving Count :" + res.getString(6) + "\n");
                    buffer.append("Date Entered :" + res.getString(7) + "\n\n");
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("User Entries");
                builder.setMessage(buffer.toString());
                builder.show();
            }
        });
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadPieChartData2(View view) {
        statsText.setText(null);
        ArrayList<PieEntry> entries = new ArrayList<>();
        int totalCalories = 0;
        int totalCarbs = 0;
        int totalFats = 0;
        int totalProtein = 0;
        //calc total cals out of all foods entered in today.
        Cursor res = DB.getdata();
        ArrayList<String> foodNamesToday = new ArrayList<>();
        while (res.moveToNext()){
            if (res.getString(7).toString().equals(LocalDate.now().toString())){
                totalCalories+=Integer.parseInt(res.getString(2));
                totalCarbs+=Integer.parseInt(res.getString(3));
                totalFats+=Integer.parseInt(res.getString(4));
                totalProtein+=Integer.parseInt(res.getString(5));
                //add name to string arr of names
                foodNamesToday.add(res.getString(0));
            }
        }
        statsText.append("\n Total Calories: "+ totalCalories);
        statsText.append("\n Total Carbohydrates : "+ totalCarbs);
        statsText.append("\n Total Fats: "+ totalFats);
        statsText.append("\n Total Protein: "+ totalProtein);
        statsText.append("\n Foods Consumed "+foodNamesToday);
        double carbPercent = Double.parseDouble(String.valueOf(totalCarbs))/Double.parseDouble(String.valueOf(totalCalories));
        double fatPercent = Double.parseDouble(String.valueOf(totalFats))/Double.parseDouble(String.valueOf(totalCalories));
        double proteinPercent = Double.parseDouble(String.valueOf(totalProtein))/Double.parseDouble(String.valueOf(totalCalories));
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
        todaysStatsChart.setData(data);
        todaysStatsChart.invalidate();

        todaysStatsChart.animateY(1400, Easing.EaseInOutQuad);
    }

    public void setupPieChart2() {
        todaysStatsChart.setDrawHoleEnabled(true);
        todaysStatsChart.setUsePercentValues(true);
        todaysStatsChart.setEntryLabelTextSize(20f);
        todaysStatsChart.setEntryLabelColor(Color.BLACK);
        todaysStatsChart.setCenterText("Caloric Percentages");
        todaysStatsChart.setCenterTextSize(20f);
        todaysStatsChart.getDescription().setEnabled(false);

        Legend l = todaysStatsChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }


}