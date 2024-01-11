package com.alpermelkeli.billapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    private AppCompatButton garsonGiris;
    private AppCompatButton kasaGiris;

    private  AppCompatButton yoneticiGiris;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        garsonGiris = findViewById(R.id.garson);
        kasaGiris = findViewById(R.id.kasa);
        yoneticiGiris = findViewById(R.id.yonetici);

        garsonGiris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Garson.class);
                startActivity(intent);
                finish();
            }
        });

        kasaGiris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Kasa.class);
                startActivity(intent);
                finish();


            }
        });
        yoneticiGiris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, YoneticiGiris.class);
                startActivity(intent);
                finish();
            }
        });


    }




}