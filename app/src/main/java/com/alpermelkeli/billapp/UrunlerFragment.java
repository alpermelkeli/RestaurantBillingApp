package com.alpermelkeli.billapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class UrunlerFragment extends Fragment {

    ImageView addProduct;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_urunler, container, false);

        addProduct = view.findViewById(R.id.addProduct);


        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),UrunEkle.class);
                startActivity(intent);
            }
        });




        return view;
    }
}