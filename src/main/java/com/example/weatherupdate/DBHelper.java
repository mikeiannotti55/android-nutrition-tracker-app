package com.example.weatherupdate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.LocalDate;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context,"calendar.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table Foodlog(name TEXT primary key, brand TEXT, calories INT, carbs INT, fats INT, proteins INT, servings NUM, date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists Foodlog");
    }

    public Boolean insertuserdata(String name, String brand, int calories, int carbs, int fats, int proteins, double servings, String date){
       SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",name);
        contentValues.put("brand",brand);
        contentValues.put("calories",calories);
        contentValues.put("carbs",carbs);
        contentValues.put("fats",fats);
        contentValues.put("proteins",proteins);
        contentValues.put("servings",servings);
        contentValues.put("date", String.valueOf(date));
        long result = DB.insert("Foodlog", null,contentValues);
        if (result == -1){
            return false;
        }else{
            return true;
        }

    }

    public Boolean updateuserdata(String name, String brand, int calories, int carbs, int fats, int proteins, double servings, LocalDate date){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("brand",brand);
        contentValues.put("calories",calories);
        contentValues.put("carbs",carbs);
        contentValues.put("fats",fats);
        contentValues.put("proteins",proteins);
        contentValues.put("servings",servings);
        contentValues.put("date", String.valueOf(date));
        Cursor cursor = DB.rawQuery("Select * from Foodlog where name = ?",new String[]{name});
        if (cursor.getCount()>0){
            long result = DB.update("Foodlog",contentValues,"name=?",new String[] {name});
            if (result == -1){
                return false;
            }else{
                return true;
            }
        }else{
            return false;
        }

    }

    public Boolean deletedata(String name){

        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("Select * from Foodlog where name = ?",new String[]{name});
        if (cursor.getCount()>0){

            long result = DB.delete("Foodlog","name=?",new String[] {name});
            if (result == -1){
                return false;
            }else{
                return true;
            }
        }else{
            return false;
        }

    }

    public Cursor getdata (){

        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("Select * from Foodlog",null);
        return cursor;

    }

}
