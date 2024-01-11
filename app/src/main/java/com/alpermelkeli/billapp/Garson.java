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

public class Garson extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_garson);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        SiparisAlFragment siparisAlFragment = new SiparisAlFragment();
        MevcutSiparislerFragment mevcutSiparislerFragment = new MevcutSiparislerFragment();

        replaceFragment(siparisAlFragment);
        //items color changes
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


        bottomNavigationView.setItemIconTintList(iconTint);
        bottomNavigationView.setItemTextColor(getResources().getColorStateList(R.color.black));


        bottomNavigationView.setOnItemSelectedListener(item -> {


        switch (item.getItemId()){

            case R.id.siparisal:
                replaceFragment(siparisAlFragment);
            break;

            case R.id.mevcutsiparisler:
                replaceFragment(mevcutSiparislerFragment);
            break;



        }
        return true;
        });




    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Garson.this, MainActivity.class);
        startActivity(intent);
    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayoutgarson,fragment);
        fragmentTransaction.commit();

    }

}