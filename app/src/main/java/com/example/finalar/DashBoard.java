package com.example.finalar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DashBoard extends AppCompatActivity {

    CardView furCard;
    CardView animalsCard;
    CardView carsCard;
    CardView watchCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        furCard = findViewById(R.id.furnitureCard);
        animalsCard = findViewById(R.id.animalCard);
        carsCard = findViewById(R.id.carCard);
        watchCard = findViewById(R.id.virtualTryOn);
        furCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoard.this, MainActivity.class);
                intent.putExtra("FURNITURE",1);
                openActivity(intent);
            }
        });

        animalsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoard.this, MainActivity.class);
                intent.putExtra("ANIMALS",2);
                openActivity(intent);
            }
        });

        carsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoard.this, MainActivity.class);
                intent.putExtra("CARS",3);
                openActivity(intent);
            }
        });

        watchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoard.this, EyeGlasses.class);
                intent.putExtra("WATCH",4);
                openActivity(intent);
            }
        });

    }
    public void openActivity(Intent intent){
        startActivity(intent);
    }
}