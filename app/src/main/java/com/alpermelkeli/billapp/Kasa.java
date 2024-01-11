package com.alpermelkeli.billapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Kasa extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ButtonAdapterKasa buttonAdapter;
    private List<String> masaList;

    private ImageView backImage;

    private Button selfServis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kasa);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        backImage = findViewById(R.id.backımage);
        recyclerView = findViewById(R.id.recyclerView);
        selfServis = findViewById(R.id.selfServis);
        masaList = new ArrayList<>();
        buttonAdapter = new ButtonAdapterKasa(masaList, new ButtonAdapterKasa.OnButtonClickListener() {
            @Override
            public void onButtonClicked(String masaId) {
                startNextActivity(masaId);

            }
        });
        selfServis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Kasa.this,SiparisMenu.class);
                intent.putExtra("masaId","kasa");
                startActivity(intent);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(Kasa.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(buttonAdapter);
        listenForMasalar();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Kasa.this, MainActivity.class);
        startActivity(intent);
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
                Intent intent = new Intent(Kasa.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void startNextActivity(String masaId) {
        Intent intent = new Intent(Kasa.this, MasaActivity.class);
        intent.putExtra("masaId", masaId);
        System.out.println(masaId);
        startActivity(intent);
    }
    private void listenForMasalar() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference masalarCollectionRef = firestore.collection("Masalar");

        masalarCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Hata durumunu işleyin
                    return;
                }

                if (querySnapshot != null) {
                    masaList.clear();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String masaId = document.getId();
                        masaList.add(masaId);
                    }

                    buttonAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}


