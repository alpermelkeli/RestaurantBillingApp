package com.alpermelkeli.billapp;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MevcutSiparislerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MevcutSiparislerFragment extends Fragment {
    RecyclerView recyclerView;

    public MevcutSiparislerFragment() {
        // Required empty public constructor
    }

    public static MevcutSiparislerFragment newInstance() {
        return new MevcutSiparislerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mevcut_siparisler, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        retrieveOrdersByOrderDate();
        return view;
    }

    // MevcutSiparislerFragment sınıfında retrieveOrdersByOrderDate() metodunun içine ekleyin

    private void retrieveOrdersByOrderDate() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference ordersCollectionRef = firestore.collection("Siparisler");

        Query query = ordersCollectionRef.orderBy("siparisTarihi", Query.Direction.ASCENDING);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Order> orderList = new ArrayList<>();

                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
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
                            // Tüm sipariş detayları alındıktan sonra adapter güncellenir
                            if (orderList.size() == queryDocumentSnapshots.getDocuments().size()) {
                                // Siparişleri tarihlerine göre sıralama
                                Collections.sort(orderList, new Comparator<Order>() {
                                    @Override
                                    public int compare(Order o1, Order o2) {
                                        long orderDate1 = o1.getOrderDate();
                                        long orderDate2 = o2.getOrderDate();

                                        // İki siparişin tarihlerini karşılaştırma
                                        if (orderDate1 < orderDate2) {
                                            return 1;
                                        } else if (orderDate1 > orderDate2) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                });

                                OrderAdapter adapter = new OrderAdapter(orderList);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Hata durumunu işleyin
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Hata durumunu işleyin
            }
        });
    }





}
