package com.example.weatherupdate.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import com.example.weatherupdate.DBHelper;
import com.example.weatherupdate.food;
import com.google.android.material.card.MaterialCardView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.weatherupdate.R;

public class SettingsFragment extends CalendarFragment {
    Button resetBtn, removeBtn, getGoalsBtn;
    TextView goalsTxt, statsText;
    DBHelper DB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_settings, container, false);
        resetBtn = v.findViewById(R.id.btnResetData);
        resetBtn.setOnClickListener(this::resetData);
        removeBtn = v.findViewById(R.id.btnRemoveFood);
        getGoalsBtn = v.findViewById(R.id.btnGetGoals);
        goalsTxt = v.findViewById(R.id.goalsText);
        statsText = v.findViewById(R.id.statsText);
        getGoalsBtn.setOnClickListener(this::getGoals);
        removeBtn.setOnClickListener(this::removeFood);
        DB = new DBHelper(getActivity());

        return v;
    }

    private void removeFood(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.requireActivity());
        builder.setTitle("Enter Info:");
        Context context = this.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText foodName = new EditText(context);
        foodName.setHint("Name of food: ");
        layout.addView(foodName);
        builder.setView(layout);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    DB.deletedata(foodName.getText().toString());
                    Toast.makeText(getActivity(), "Food removed!", Toast.LENGTH_SHORT).show();
                } catch (Exception ignored) {
                    Toast.makeText(getActivity(), "Cannot remove food!", Toast.LENGTH_SHORT).show();
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

    private void getGoals(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.requireActivity());
        builder.setTitle("Enter Info:");
        Context context = this.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText weightBox = new EditText(context);
        weightBox.setHint("Weight in lbs: ");
        layout.addView(weightBox);

        final EditText activityBox = new EditText(context);
        activityBox.setHint("Average minutes of activity per day :");
        layout.addView(activityBox);

        final EditText goalBox = new EditText(context);
        goalBox.setHint("Maintain, gain or lose weight?");
        layout.addView(goalBox);

        builder.setView(layout);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //adds custom food
                try {
                    double weight = Double.parseDouble(weightBox.getText().toString());
                    int minsActivity = Integer.parseInt(activityBox.getText().toString());
                    String goal = goalBox.getText().toString();
                    statsText.append("Weight: " + weight + " Lbs");
                    statsText.append("\nAvg Activity : " + minsActivity + " mins");
                    statsText.append("\nGoal: " + goal + " weight");
                    int num = 16;
                    switch (goal.toLowerCase(Locale.ROOT)) {
                        case "gain":
                            num = 22;
                            break;
                        case "lose":
                            num = 11;
                            break;
                    }
                    if (minsActivity > 60) {
                        num += 3;
                    } else if (minsActivity < 30) {
                        num -= 1;
                    } else {
                        num += 1;
                    }
                    DecimalFormat df = new DecimalFormat("0.00");
                    int calorieGoal = (int) (weight * num);
                    goalsTxt.append("Nutritional Goals");
                    goalsTxt.append("\n");
                    goalsTxt.append("\nCalorie goal: " + calorieGoal + " kcals");
                    goalsTxt.append("\nCarbs " + df.format((calorieGoal * .5) / 4) + "g  (50%)");
                    goalsTxt.append("\nFat " + df.format((calorieGoal * .3) / 9) + "g  (20%)");
                    goalsTxt.append("\nProtein " + df.format((calorieGoal * .2) / 4) + "g  (30%)");

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

    private void resetData(View view) {
        Cursor res = DB.getdata();
        ArrayList<String> names = new ArrayList<>();
        while (res.moveToNext()) {
            names.add(res.getString(0));
        }
        for (String x : names) {
            DB.deletedata(x);
        }
        Toast.makeText(getActivity(), "Data reset!", Toast.LENGTH_SHORT).show();

    }


}