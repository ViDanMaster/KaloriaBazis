package com.example.kaloriabazis.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kaloriabazis.R;
import com.example.kaloriabazis.models.Food;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddFoodActivity extends AppCompatActivity {

    private EditText editTextFoodName, editTextCalories, editTextProtein, editTextFat, editTextCarbohydrate;
    private Button buttonAddFood;

    private CollectionReference foodsCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        foodsCollection = FirebaseFirestore.getInstance().collection("foods");

        editTextFoodName = findViewById(R.id.editTextFoodName);
        editTextCalories = findViewById(R.id.editTextCalories);
        editTextProtein = findViewById(R.id.editTextProtein);
        editTextFat = findViewById(R.id.editTextFat);
        editTextCarbohydrate = findViewById(R.id.editTextCarbohydrate);
        buttonAddFood = findViewById(R.id.buttonAddFood);

        buttonAddFood.setOnClickListener(v -> addFood());
    }

    private void addFood() {
        String foodName = editTextFoodName.getText().toString().trim();
        String caloriesStr = editTextCalories.getText().toString().trim();
        String proteinStr = editTextProtein.getText().toString().trim();
        String fatStr = editTextFat.getText().toString().trim();
        String carbohydrateStr = editTextCarbohydrate.getText().toString().trim();

        if (TextUtils.isEmpty(foodName)) {
            editTextFoodName.setError("Add meg az étel nevét!");
            return;
        }

        if (TextUtils.isEmpty(caloriesStr)) {
            editTextCalories.setError("Add meg az étel kalóriatartalmát!");
            return;
        }

        int calories = Integer.parseInt(caloriesStr);
        int protein = TextUtils.isEmpty(proteinStr) ? 0 : Integer.parseInt(proteinStr);
        int fat = TextUtils.isEmpty(fatStr) ? 0 : Integer.parseInt(fatStr);
        int carbohydrate = TextUtils.isEmpty(carbohydrateStr) ? 0 : Integer.parseInt(carbohydrateStr);

        Food food = new Food(foodName, calories, protein, fat, carbohydrate);

        foodsCollection.add(food)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddFoodActivity.this, "Étel hozzáadva", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddFoodActivity.this, "Sikertelen hozzáadás", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
