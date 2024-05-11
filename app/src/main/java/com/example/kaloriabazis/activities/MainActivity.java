package com.example.kaloriabazis.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.kaloriabazis.R;
import com.example.kaloriabazis.models.Food;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button buttonAddFood, buttonAddFoodToUser;
    private FirebaseAuth firebaseAuth;
    private TextView textViewCalories, textViewProtein, textViewFat, textViewCarbohydrate;

    private String userId;
    private ListView listViewTodayFoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAddFood = findViewById(R.id.buttonAddFood);
        buttonAddFood.setOnClickListener(v -> openAddFood());

        buttonAddFoodToUser = findViewById(R.id.buttonAddFoodToUser);
        buttonAddFoodToUser.setOnClickListener(v -> openAddFoodToUserActivity());

        textViewCalories = findViewById(R.id.textViewCalories);
        textViewProtein = findViewById(R.id.textViewProtein);
        textViewFat = findViewById(R.id.textViewFat);
        textViewCarbohydrate = findViewById(R.id.textViewCarbohydrate);
        listViewTodayFoods = findViewById(R.id.listViewTodayFoods);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userId = user.getUid();

            updateTodayFoods();

            listViewTodayFoods.setOnItemLongClickListener((parent, view, position, id) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Étel műveletek");
                builder.setItems(new CharSequence[]{"Törlés"}, (dialog, which) -> {
                    deleteFood(position);
                });
                builder.create().show();
                return true;
            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTodayFoods();
    }

    private void openAddFoodToUserActivity() {
        Intent intent = new Intent(this, AddFoodToUserActivity.class);
        startActivity(intent);
    }

    private void openAddFood(){
        Intent intent = new Intent(this, ViewFoodsActivity.class);
        startActivity(intent);
    }

    private void updateTodayFoods() {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("consumedFoods")
                .document(todayDate)
                .collection("foods")
                .orderBy("name")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> todayFoods = new ArrayList<>();
                    int[] totals = new int[4];

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Food food = snapshot.toObject(Food.class);
                        if (food != null) {
                            todayFoods.add(food.getName());
                            totals[0] += food.getCalories();
                            totals[1] += food.getProtein();
                            totals[2] += food.getFat();
                            totals[3] += food.getCarbohydrate();
                        }
                    }

                    textViewCalories.setText(getString(R.string.mai_bevitt_kal_ria_d, totals[0]));
                    textViewProtein.setText(getString(R.string.mai_bevitt_protein_d, totals[1]));
                    textViewFat.setText(getString(R.string.mai_bevitt_zs_r_d, totals[2]));
                    textViewCarbohydrate.setText(getString(R.string.mai_bevitt_sz_nhidr_t_d, totals[3]));

                    listViewTodayFoods.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, todayFoods));
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Hiba történt az adatok lekérése során", Toast.LENGTH_SHORT).show());
    }

    private void deleteFood(int position) {
        Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out);
        anim.setDuration(500);
        listViewTodayFoods.getChildAt(position).startAnimation(anim);

        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("consumedFoods")
                .document(todayDate)
                .collection("foods")
                .orderBy("name")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    if (!documents.isEmpty() && position < documents.size()) {
                        documents.get(position).getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(MainActivity.this, "Étel sikeresen törölve", Toast.LENGTH_SHORT).show();
                                    updateTodayFoods();
                                })
                                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Hiba történt az étel törlése során", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Hiba történt az adatok lekérése során", Toast.LENGTH_SHORT).show());
    }
}
