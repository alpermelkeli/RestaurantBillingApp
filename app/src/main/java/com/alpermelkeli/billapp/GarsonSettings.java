package com.alpermelkeli.billapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class GarsonSettings extends AppCompatActivity {
    ImageView masaEdit;
    ImageView stockEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garson_settings);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        masaEdit = findViewById(R.id.masaEdit);
        stockEdit = findViewById(R.id.stockEdit);
        masaEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GarsonSettings.this,MasaDuzenle.class);
                startActivity(intent);
            }
        });
        stockEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GarsonSettings.this,StokDuzenle.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(GarsonSettings.this,Garson.class);
        startActivity(intent);
        finish();
    }
}