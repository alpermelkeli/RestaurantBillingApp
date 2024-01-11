package com.alpermelkeli.billapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MasaAyir extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masa_ayir);

        Button ayirButton = findViewById(R.id.ayirButton);
        ayirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ayirMasalar();
            }
        });
    }

    private void ayirMasalar() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference masalarCollectionRef = firestore.collection("Masalar");

        // Ayırmak istediğiniz masaların ID'lerini buraya ekleyin
        String birlesikMasaID = "masa2masa3";

        // Ayırma işlemi için ilk olarak birleşik masanın verilerini alın
        DocumentReference birlesikMasaRef = masalarCollectionRef.document(birlesikMasaID);
        birlesikMasaRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Masa birlesikMasa = documentSnapshot.toObject(Masa.class);
                    if (birlesikMasa != null) {
                        // İki ayrı masa oluşturun ve verileri kaydedin
                        String masa1ID = "masa2";
                        Masa masa1 = new Masa(masa1ID, birlesikMasa.getToplamFiyat());
                        DocumentReference masa1Ref = masalarCollectionRef.document(masa1ID);
                        masa1Ref.set(masa1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MasaAyir.this, "Masa 1 oluşturuldu.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MasaAyir.this, "Masa 1 oluşturma hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                        String masa2ID = "masa3";
                        Masa masa2 = new Masa(masa2ID, birlesikMasa.getToplamFiyat());
                        DocumentReference masa2Ref = masalarCollectionRef.document(masa2ID);
                        masa2Ref.set(masa2)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MasaAyir.this, "Masa 2 oluşturuldu.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MasaAyir.this, "Masa 2 oluşturma hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                        // Birleşik masayı sil
                        birlesikMasaRef.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MasaAyir.this, "Birleşik masa ayırıldı.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MasaAyir.this, "Birleşik masa ayırma hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(MasaAyir.this, "Birleşik masa bulunamadı.", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MasaAyir.this, "Birleşik masa alınırken bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
