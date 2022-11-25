package com.example.weatherupdate;

import java.time.LocalDate;

public class food {
    String name;
    String brand;
    int calories;
    int carbohydrates;
    int fats;
    int protein;
    double servingCount;
    LocalDate date;

    public food(String n, String b, int cals, int carbs, int fats, int p, double count, LocalDate d) {
        this.name = n;
        this.brand = b;
        this.calories = (int) (Double.parseDouble(String.valueOf(cals)) * count);
        this.carbohydrates = (int) (Double.parseDouble(String.valueOf(carbs)) * count);
        this.fats = (int) (Double.parseDouble(String.valueOf(fats)) * count);
        this.protein = (int) (Double.parseDouble(String.valueOf(p)) * count);
        this.servingCount = count;
        this.date = d;
    }

    public String getDate() {
        return String.valueOf(date);
    }

    public String getBrand() {
        return brand;
    }

    public double getServingCount() {
        return servingCount;
    }

    public int getCalories() {
        return calories;
    }

    public int getCarbohydrates() {
        return carbohydrates;
    }

    public int getFats() {
        return fats;
    }

    public int getProtein() {
        return protein;
    }

    public String getName() {
        return name;
    }

}
