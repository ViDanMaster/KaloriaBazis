package com.example.kaloriabazis.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kaloriabazis.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ViewFoodsActivity extends AppCompatActivity {

    private ListView listViewFoods;
    private Button buttonAddFood;
    private SearchView searchView;
    private CollectionReference foodsCollection;
    private ArrayList<String> foodList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_foods);

        listViewFoods = findViewById(R.id.listViewFoods);
        buttonAddFood = findViewById(R.id.buttonAddFood);
        searchView = findViewById(R.id.searchView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, foodList);

        foodsCollection = FirebaseFirestore.getInstance().collection("foods");

        adapter.setNotifyOnChange(true);

        Query query = foodsCollection.orderBy("name");

        query.addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(ViewFoodsActivity.this, "Hiba a lekérdezés során", Toast.LENGTH_SHORT).show();
                return;
            }

            foodList.clear();
            for (DocumentSnapshot doc : value) {
                String addedFoodName = doc.getString("name");
                foodList.add(addedFoodName);
            }
            adapter.notifyDataSetChanged();
        });

        listViewFoods.setOnItemLongClickListener((parent, view, position, id) -> {
            String selectedFoodName = foodList.get(position);

            AlertDialog.Builder builder = new AlertDialog.Builder(ViewFoodsActivity.this);
            builder.setTitle("Étel műveletek");
            builder.setItems(new CharSequence[]{"Szerkesztés", "Törlés"}, (dialog, which) -> {
                switch (which) {
                    case 0:
                        editFood(selectedFoodName);
                        break;
                    case 1:
                        deleteFood(selectedFoodName);
                        break;
                }
            });
            builder.create().show();
            return true;
        });

        listViewFoods.setAdapter(adapter);
        buttonAddFood.setOnClickListener(v -> startActivity(new Intent(ViewFoodsActivity.this, AddFoodActivity.class)));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        listViewFoods.startAnimation(anim);
    }

    private void refreshData() {
        foodList.clear();

        Query query = foodsCollection.orderBy("name");

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    String addedFoodName = document.getString("name");
                    if (!foodList.contains(addedFoodName)) {
                        foodList.add(addedFoodName);
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(ViewFoodsActivity.this, "Hiba a lekérdezés során", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editFood(String foodName) {
        foodsCollection.whereEqualTo("name", foodName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    String foodId = document.getId();
                    Intent intent = new Intent(ViewFoodsActivity.this, EditFoodActivity.class);
                    intent.putExtra("foodId", foodId);
                    startActivity(intent);
                    break;
                }
            } else {
                Toast.makeText(ViewFoodsActivity.this, "Hiba a lekérdezés során", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteFood(String foodName) {
        foodsCollection.whereEqualTo("name", foodName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    String foodId = document.getId();
                    foodsCollection.document(foodId).delete().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(ViewFoodsActivity.this, "Étel sikeresen törölve", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ViewFoodsActivity.this, "Hiba történt a törlés során", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                }
            } else {
                Toast.makeText(ViewFoodsActivity.this, "Hiba a lekérdezés során", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
