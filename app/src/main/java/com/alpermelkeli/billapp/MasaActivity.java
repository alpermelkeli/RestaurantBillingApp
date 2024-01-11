package com.alpermelkeli.billapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class MasaActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    String masaID;

    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masa);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        recyclerView = findViewById(R.id.recyclerView);
        backButton = findViewById(R.id.backButton);
        masaID = getIntent().getStringExtra("masaId");
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MasaActivity.this,Kasa.class);
                startActivity(intent);

            }
        });
        retrieveOrdersForMasa();
    }

    private void retrieveOrdersForMasa() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference ordersCollectionRef = firestore.collection("Siparisler");

        Query query = ordersCollectionRef.whereEqualTo("masaNo", masaID)
                .whereEqualTo("odemeDurumu", "tamamlanmadi");

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Hata durumunu işleyin
                    return;
                }

                List<Order> orderList = new ArrayList<>();

                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String orderId = document.getId();
                        String tableId = document.getString("masaNo");
                        long orderDate = document.getLong("siparisTarihi");
                        String paymentStatus = document.getString("odemeDurumu");

                        CollectionReference siparisDetaylariCollectionRef = document.getReference().collection("siparisDetaylari");
                        siparisDetaylariCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot querySnapshot) {
                                List<SiparisDetayi> siparisDetaylari = new ArrayList<>();

                                for (DocumentSnapshot siparisDetayiDocument : querySnapshot.getDocuments()) {
                                    String adet = siparisDetayiDocument.getString("adet");
                                    String notlar = siparisDetayiDocument.getString("notlar");
                                    String urunAdi = siparisDetayiDocument.getString("urunAdi");
                                    SiparisDetayi siparisDetayi = new SiparisDetayi(adet, notlar, urunAdi);
                                    siparisDetaylari.add(siparisDetayi);
                                }

                                Order order = new Order(orderId, tableId, orderDate, paymentStatus, siparisDetaylari);
                                orderList.add(order);

                                OrderAdapterKasa adapter = new OrderAdapterKasa(orderList);
                                recyclerView.setLayoutManager(new LinearLayoutManager(MasaActivity.this));
                                recyclerView.setAdapter(adapter);

                                // Sipariş listesi boşsa Kasa aktivitesine dön
                                if (orderList.isEmpty()) {
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Hata durumunu işleyin
                            }
                        });
                    }
                } else {
                    // Sipariş listesi boşsa Kasa aktivitesine dön
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveOrdersForMasa();
    }
}
