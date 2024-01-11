package com.alpermelkeli.billapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SiparisOnay extends AppCompatActivity {
    ShapeableImageView siparisFoto;
    ImageView acceptButton;
    TextView urunaditext;
    TextView adet;
    int adetSayisi = 1;

    double urunFiyatı;

    ImageView minusButton;
    ImageView plusButton;
    String urunAdi;
    String resimDosyaAdi;
    String masaID;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siparis_onay);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        siparisFoto = findViewById(R.id.siparisFoto);
        acceptButton = findViewById(R.id.urunEkleOnay);
        urunaditext = findViewById(R.id.urunaditext);
        adet = findViewById(R.id.adet);
        minusButton = findViewById(R.id.minusButton);
        plusButton = findViewById(R.id.plusButton);
        urunAdi = getIntent().getStringExtra("urunAdi");
        resimDosyaAdi = getIntent().getStringExtra("resimDosyaAdi");
        urunFiyatı = Double.parseDouble(getIntent().getStringExtra("fiyat"));
        masaID = getIntent().getStringExtra("masaID");
        adet.setText(Integer.toString(adetSayisi));
        // Set the product name in the TextView
        urunaditext.setText(urunAdi);

        // Load the image into siparisFoto using Glide library
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL); // Cache the image for better performance

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child(resimDosyaAdi);

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(SiparisOnay.this)
                        .load(uri)
                        .apply(requestOptions)
                        .into(siparisFoto);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors that occur during image loading
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adetSayisi++;
                adet.setText(Integer.toString(adetSayisi));

            }
        });
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adetSayisi>0){
                    adetSayisi--;
                    adet.setText(Integer.toString(adetSayisi));
                }
            }
        });
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                siparisiOnayla();
                finish();
            }

        });


    }
    public class Order {
        private String adet;
        private String notlar;
        private String urunAdi;

        public Order(String adet, String notlar, String urunAdi) {
            this.adet = adet;
            this.notlar = notlar;
            this.urunAdi = urunAdi;
        }

        public String getAdet() {
            return adet;
        }

        public String getNotlar() {
            return notlar;
        }

        public String getUrunAdi() {
            return urunAdi;
        }
    }



    public void siparisiOnayla() {
        double toplamFiyat = adetSayisi * urunFiyatı;

        // Create an instance of the Order class with the necessary data
        Order order = new Order(Integer.toString(adetSayisi), "not yok", urunAdi);

        // Save the order in the "Siparisler" collection of the database
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference siparislerCollectionRef = firestore.collection("Siparisler");

        // Create a new document with a generated ID
        DocumentReference newOrderRef = siparislerCollectionRef.document();

        // Create the "siparisDetaylari" subcollection reference
        CollectionReference siparisDetaylariCollectionRef = newOrderRef.collection("siparisDetaylari");

        // Set the fields of the order document
        newOrderRef.set(order)
                .addOnSuccessListener(aVoid -> {
                    // Order details saved successfully

                    // You can also save additional fields for the order
                    newOrderRef.update("masaNo", masaID);
                    newOrderRef.update("siparisTarihi", (System.currentTimeMillis()));
                    newOrderRef.update("hazirlanmaDurumu", "Hazır");
                    newOrderRef.update("odemeDurumu", "tamamlanmadi");
                    newOrderRef.update("teslimatDurumu", "edilmedi");

                    // Save the subcollection document
                    DocumentReference siparisDetaylariDocRef = siparisDetaylariCollectionRef.document();
                    siparisDetaylariDocRef.set(order)
                            .addOnSuccessListener(aVoid1 -> {
                                // Subcollection document saved successfully
                            })
                            .addOnFailureListener(e -> {
                                // Handle the error if the subcollection document couldn't be saved
                            });

                    // Add the price to the corresponding "masaID" document in the "Masalar" collection
                    CollectionReference masalarCollectionRef = firestore.collection("Masalar");
                    DocumentReference masaRef = masalarCollectionRef.document(masaID);
                    masaRef.update("toplamFiyat", FieldValue.increment(toplamFiyat))
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(SiparisOnay.this,"Siparişiniz eklendi", Toast.LENGTH_LONG).show();
                            })
                            .addOnFailureListener(e -> {
                                // Handle the error if the price couldn't be added to the "masaID" document
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle the error if the order details couldn't be saved
                });




    }









}