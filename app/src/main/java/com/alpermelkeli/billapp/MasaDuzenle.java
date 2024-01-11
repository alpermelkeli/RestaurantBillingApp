package com.alpermelkeli.billapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class MasaDuzenle extends AppCompatActivity {

    ImageView masaEkle;
    ImageView masaBirlestir;
    ImageView masaAyir;
    ImageView masaSil;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masa_duzenle);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        masaEkle = findViewById(R.id.masaEkle);
        masaBirlestir = findViewById(R.id.masaBirlestir);
        masaAyir = findViewById(R.id.masaAyir);
        masaSil = findViewById(R.id.masaSil);
        masaEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                CollectionReference masalarCollectionRef = firestore.collection("Masalar");

                // Son masa dokümanının ID'sini alarak yeni masa ID'sini belirleyin
                Query query = masalarCollectionRef.orderBy("masaNo", Query.Direction.DESCENDING).limit(1);
                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        int sonMasaNo = 0;

                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot sonMasa = querySnapshot.getDocuments().get(0);
                            sonMasaNo = Integer.parseInt(sonMasa.getId().substring(4));
                        }

                        int yeniMasaNo = sonMasaNo + 1;
                        String yeniMasaID = "masa" + yeniMasaNo;

                        // Yeni masa dokümanını oluşturun
                        DocumentReference masaDocumentRef = masalarCollectionRef.document(yeniMasaID);
                        Masa masa = new Masa(yeniMasaID, 0); // toplamFiyat başlangıç değeri olarak 0 ayarlandı
                        masaDocumentRef.set(masa)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Başarıyla masa oluşturuldu
                                        Toast.makeText(getApplicationContext(), "Masa oluşturuldu: " + yeniMasaID, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Masa oluşturma başarısız oldu
                                        Toast.makeText(getApplicationContext(), "Masa oluşturma hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
        });
        masaSil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                CollectionReference masalarCollectionRef = firestore.collection("Masalar");
                Query query = masalarCollectionRef.orderBy("masaNo", Query.Direction.DESCENDING).limit(1);

                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments().get(0);
                            String documentId = lastDocument.getId();

                            masalarCollectionRef.document(documentId)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Delete operation successful, you can perform additional actions here
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Delete operation failed, handle the error here
                                        }
                                    });
                        } else {
                            // The collection is empty, nothing to delete
                        }
                    }
                });
            }
        });

        masaBirlestir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MasaDuzenle.this,MasaBirlestir.class);
                startActivity(intent);

            }
        });
        masaAyir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                CollectionReference masalarCollectionRef = firestore.collection("Masalar");
                masalarCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot document: queryDocumentSnapshots.getDocuments()){

                            if(document.getId().contains("-")){
                                String[] masalar =  document.getId().split("-");
                                String masa1 = masalar[0];
                                String masa2 = masalar[1];
                                DocumentReference all = masalarCollectionRef.document(document.getId());
                                all.delete();
                                DocumentReference masa1doc = masalarCollectionRef.document(masa1);
                                Masa masa1c = new Masa(masa1, 0);
                                masa1doc.set(masa1c);
                                DocumentReference masa2doc = masalarCollectionRef.document(masa2);
                                Masa masa2c = new Masa(masa2, 0);
                                masa2doc.set(masa2c);
                                Toast.makeText(MasaDuzenle.this,"Masaların tamamı ayrıldı", Toast.LENGTH_LONG).show();

                            }

                        }
                    }
                });

            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MasaDuzenle.this,GarsonSettings.class);
        startActivity(intent);
        finish();
    }
    public class Masa {
        private String masaNo;
        private double toplamFiyat;

        public Masa() {
            // Boş yapıcı yöntem gereklidir (Firestore'da dokümanlara dönüştürülürken kullanılır)
        }

        public Masa( String masaNo, double toplamFiyat) {
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