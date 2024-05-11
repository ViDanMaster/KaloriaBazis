package com.example.kaloriabazis.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kaloriabazis.R;
import com.example.kaloriabazis.models.Food;
import com.example.kaloriabazis.notifications.NotificationHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddFoodToUserActivity extends AppCompatActivity {

    private ListView listViewFoods;
    private Button buttonAddFoods;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    private List<Food> foodList;
    private DocumentReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_to_user);

        listViewFoods = findViewById(R.id.listViewFoods);
        buttonAddFoods = findViewById(R.id.buttonAddFoods);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userId = user.getUid();

        firestore = FirebaseFirestore.getInstance();

        userReference = firestore.collection("users").document(userId);

        foodList = new ArrayList<>();

        firestore.collection("foods").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    foodList.addAll(queryDocumentSnapshots.toObjects(Food.class));

                    List<String> foodNames = new ArrayList<>();
                    for (Food food : foodList) {
                        foodNames.add(food.getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddFoodToUserActivity.this, android.R.layout.simple_list_item_single_choice, foodNames);
                    listViewFoods.setAdapter(adapter);
                })
                .addOnFailureListener(System.out::println);

        buttonAddFoods.setOnClickListener(v -> addSelectedFoodsToUser());
    }


    private void addSelectedFoodsToUser() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = dateFormat.format(calendar.getTime());

        boolean foodAdded = false;

        for (int i = 0; i < listViewFoods.getCount(); i++) {
            if (listViewFoods.isItemChecked(i)) {
                Food selectedFood = foodList.get(i);
                    if (selectedFood != null) {
                        userReference.collection("consumedFoods").document(todayDate).collection("foods").add(selectedFood);
                    foodAdded = true;
                }
            }
        }

        if (foodAdded) {
            Toast.makeText(AddFoodToUserActivity.this, "Étel sikeresen hozzáadva", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(AddFoodToUserActivity.this, "Nem sikerült hozzáadni az ételt, jelölj ki egy ételt", Toast.LENGTH_SHORT).show();
        }


        SharedPreferences prefs = getSharedPreferences("food_prefs", MODE_PRIVATE);
        boolean isFirstFoodToday = prefs.getBoolean("isFirstFoodToday", true);
        if (foodAdded && isFirstFoodToday) {
            NotificationHelper.showNotification(this);
            prefs.edit().putBoolean("isFirstFoodToday", false).apply();
        }

        finish();
    }
}
