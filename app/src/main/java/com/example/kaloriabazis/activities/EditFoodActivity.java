package com.example.kaloriabazis.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kaloriabazis.R;
import com.example.kaloriabazis.models.Food;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditFoodActivity extends AppCompatActivity {

    private EditText editTextFoodName, editTextCalories, editTextProtein, editTextFat, editTextCarbohydrate;
    private Button buttonUpdateFood;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);

        editTextFoodName = findViewById(R.id.editTextFoodName);
        editTextCalories = findViewById(R.id.editTextCalories);
        editTextProtein = findViewById(R.id.editTextProtein);
        editTextFat = findViewById(R.id.editTextFat);
        editTextCarbohydrate = findViewById(R.id.editTextCarbohydrate);
        buttonUpdateFood = findViewById(R.id.buttonUpdateFood);

        firestore = FirebaseFirestore.getInstance();

        String foodId = getIntent().getStringExtra("foodId");

        DocumentReference foodRef = firestore.collection("foods").document(foodId);
        foodRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Food food = documentSnapshot.toObject(Food.class);
                        if (food != null) {
                            editTextFoodName.setText(food.getName());
                            editTextCalories.setText(String.valueOf(food.getCalories()));
                            editTextProtein.setText(String.valueOf(food.getProtein()));
                            editTextFat.setText(String.valueOf(food.getFat()));
                            editTextCarbohydrate.setText(String.valueOf(food.getCarbohydrate()));
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Hiba: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        buttonUpdateFood.setOnClickListener(v -> updateFood(foodRef));
    }

    private void updateFood(DocumentReference foodRef) {
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

        foodRef.set(food)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditFoodActivity.this, "Étel frissítve", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditFoodActivity.this, "Sikertelen frissítés", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
