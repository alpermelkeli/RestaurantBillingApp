package com.alpermelkeli.billapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MasaBirlestir extends AppCompatActivity {

    private ButtonAdapter buttonAdapter;
    private List<String> masaList;
    private String birleştirmeMasaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masa_birlestir);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        RecyclerView masalarRecyclerView = findViewById(R.id.masalarRecyclerView);
        masalarRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        masaList = new ArrayList<>();
        buttonAdapter = new ButtonAdapter(masaList, new ButtonAdapter.OnButtonClickListener() {
            @Override
            public void onButtonClicked(String masaId) {
                // Birleştirme işlemi için tıklandığında yapılacak işlemler burada gerçekleştirilir
                if (birleştirmeMasaId == null) {
                    birleştirmeMasaId = masaId;
                    Toast.makeText(MasaBirlestir.this, "Birleştirilecek masa: " + masaId, Toast.LENGTH_SHORT).show();
                } else {
                    birlestirMasa(birleştirmeMasaId, masaId);
                    birleştirmeMasaId = null;
                }
            }
        });
        masalarRecyclerView.setAdapter(buttonAdapter);

        getMasalar();
    }

    private void getMasalar() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference masalarCollectionRef = firestore.collection("Masalar");

        masalarCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    String masaId = document.getId();
                    masaList.add(masaId);
                }
                buttonAdapter.notifyDataSetChanged();
            }
        });
    }

    private void birlestirMasa(String birlestirmeMasaId, String hedefMasaId) {
        Toast.makeText(MasaBirlestir.this, birlestirmeMasaId + " ile " + hedefMasaId + " birleştirildi.", Toast.LENGTH_SHORT).show();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference masalarCollectionRef = firestore.collection("Masalar");

        // İlk seçilen masayı sil
        DocumentReference birlestirmeMasaRef = masalarCollectionRef.document(birlestirmeMasaId);
        birlestirmeMasaRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // İlk masa başarıyla silindi

                // İkinci seçilen masayı sil
                DocumentReference hedefMasaRef = masalarCollectionRef.document(hedefMasaId);
                hedefMasaRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // İkinci masa başarıyla silindi

                        // Yeni masa dokümanını oluştur
                        String yeniMasaID = birlestirmeMasaId + "-" + hedefMasaId;
                        DocumentReference yeniMasaRef = masalarCollectionRef.document(yeniMasaID);
                        Masa yeniMasa = new Masa(yeniMasaID, 0);
                        yeniMasaRef.set(yeniMasa).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Yeni masa başarıyla oluşturuldu
                                getMasalar(); // Masaları yeniden yükle
                                Intent intent = new Intent(MasaBirlestir.this,MasaDuzenle.class);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Yeni masa oluşturma hatası
                                Toast.makeText(MasaBirlestir.this, "Yeni masa oluşturma hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // İkinci masa silme hatası
                        Toast.makeText(MasaBirlestir.this, "İkinci masa silme hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // İlk masa silme hatası
                Toast.makeText(MasaBirlestir.this, "İlk masa silme hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public class Masa {
        private String masaNo;
        private double toplamFiyat;

        public Masa() {
            // Boş yapıcı yöntem gereklidir (Firestore'da dokümanlara dönüştürülürken kullanılır)
        }

        public Masa(String masaNo, double toplamFiyat) {
            this.masaNo = masaNo;
            this.toplamFiyat = toplamFiyat;
        }

        public String getMasaNo() {
            return masaNo;
        }

        public void setMasaNo(String masaNo) {
            this.masaNo = masaNo;
        }

        public double getToplamFiyat() {
            return toplamFiyat;
        }

        public void setToplamFiyat(double toplamFiyat) {
            this.toplamFiyat = toplamFiyat;
        }
    }


}
