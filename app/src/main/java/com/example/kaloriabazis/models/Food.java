package com.example.kaloriabazis.models;

public class Food {
    private String name;
    private int calories;
    private int protein;
    private int fat;
    private int carbohydrate;

    public Food() {
        this.name = "test";
        this.calories = 0;
        this.protein = 0;
        this.fat = 0;
        this.carbohydrate = 0;
    }

    public Food(String name, int calories, int protein, int fat, int carbohydrate) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrate = carbohydrate;
    }

    public String getName() {
        return name;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProtein() {
        return protein;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public int getFat() {
        return fat;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public int getCarbohydrate() {
        return carbohydrate;
    }

    public void setCarbohydrate(int carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    @Override
    public String toString() {
        return "Name: " + name +
                "\nCalories: " + calories +
                "\nProtein: " + protein + "g" +
                "\nFat: " + fat + "g" +
                "\nCarbohydrate: " + carbohydrate + "g";
    }
}
