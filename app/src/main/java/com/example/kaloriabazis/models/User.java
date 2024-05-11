package com.example.kaloriabazis.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private Map<String, List<Food>> consumedFoods;

    public User() {
        this.consumedFoods = new HashMap<>();
    }

    public User(Map<String, List<Food>> consumedFoods) {
        this.consumedFoods = consumedFoods;
    }

    public Map<String, List<Food>> getConsumedFoods() {
        return consumedFoods;
    }

    public void setConsumedFoods(Map<String, List<Food>> consumedFoods) {
        this.consumedFoods = consumedFoods;
    }
}