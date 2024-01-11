package com.alpermelkeli.billapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SiparisAlFragment extends Fragment {

    private RecyclerView recyclerView;
    private ButtonAdapter buttonAdapter;
    private List<String> masaList;
    private ImageView masaDuzenle;
    private ImageView backImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_siparis_al, container, false);
        backImage = view.findViewById(R.id.backımage);
        recyclerView = view.findViewById(R.id.recyclerView);
        masaDuzenle = view.findViewById(R.id.masaDuzenle);
        masaList = new ArrayList<>();
        buttonAdapter = new ButtonAdapter(masaList, new ButtonAdapter.OnButtonClickListener() {
            @Override
            public void onButtonClicked(String masaId) {
                startNextActivity(masaId);

            }
        });
        masaDuzenle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),GarsonSettings.class);
                startActivity(intent);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(buttonAdapter);

        retrieveMasalar();

        return view;
    }

    private void retrieveMasalar() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference masalarCollectionRef = firestore.collection("Masalar");

        masalarCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String masaId = document.getId();
                        masaList.add(masaId);
                    }

                    buttonAdapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Hata durumunu işleyin
            }
        });
    backImage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    });

    }

    private void startNextActivity(String masaId) {
        Intent intent = new Intent(getActivity(), SiparisMenu.class);
        intent.putExtra("masaId", masaId);
        startActivity(intent);
    }


}


