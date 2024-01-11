package com.alpermelkeli.billapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class SiparisMenu extends AppCompatActivity {
    AppCompatButton helvakat;
    AppCompatButton soguk;
    AppCompatButton sıcak;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siparis_menu);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        helvakat = findViewById(R.id.helvakat);
        soguk = findViewById(R.id.soguk);
        sıcak = findViewById(R.id.sıcak);
        String masaId = getIntent().getStringExtra("masaId");
        helvakat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SiparisMenu.this, HelvaSiparis.class);
                intent.putExtra("masaId", masaId);
                startActivity(intent);
            }
        });
        soguk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SiparisMenu.this, SogukSiparis.class);
                intent.putExtra("masaId", masaId);
                startActivity(intent);

            }
        });
        sıcak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SiparisMenu.this, SicakSiparis.class);
                intent.putExtra("masaId", masaId);
                startActivity(intent);

            }
        });
    }

}