package com.alpermelkeli.billapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Yonetici extends AppCompatActivity {
    BottomNavigationView bottomNavigationView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yonetici);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        UrunlerFragment urunlerFragment = new UrunlerFragment();
        CiroFragment ciroFragment = new CiroFragment();
        replaceFragment(urunlerFragment);
        bottomNavigationView1  =findViewById(R.id.bottomNavigationView1);
        ColorStateList iconTint = new ColorStateList(
                new int[][] {
                        new int[] { android.R.attr.state_selected },
                        new int[] { -android.R.attr.state_selected }
                },
                new int[] {
                        ContextCompat.getColor(this, R.color.black),
                        ContextCompat.getColor(this, R.color.grey)
                }
        );
        bottomNavigationView1.setItemIconTintList(iconTint);
        bottomNavigationView1.setItemTextColor(getResources().getColorStateList(R.color.black));
        bottomNavigationView1.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){

                case R.id.urunler:
                    replaceFragment(urunlerFragment);
                    break;
                case R.id.ciro:
                    replaceFragment(ciroFragment);
                    break;
            }

            return true;

        });




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Yonetici.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayoutyonetici,fragment);
        fragmentTransaction.commit();

    }
}