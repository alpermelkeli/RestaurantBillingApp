package com.alpermelkeli.billapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.CompletableFuture;

public class StokDuzenleOnay extends AppCompatActivity {
    private String urunAdi;
    private String stokMiktari;
    private String resimDosyaAdi;
    private ImageView urunFoto;
    private ImageView stokOnay;
    private EditText editTextStok;
    private TextView stok;
    private FirebaseFirestore firestore;

    private AppCompatButton kutu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stok_duzenle_onay);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        firestore = FirebaseFirestore.getInstance();
        urunAdi = getIntent().getStringExtra("urunAdi");
        stokMiktari = getIntent().getStringExtra("stokMiktari");
        resimDosyaAdi = getIntent().getStringExtra("resimDosyaAdi");
        urunFoto = findViewById(R.id.urunFoto);
        stokOnay = findViewById(R.id.stokOnay);
        editTextStok = findViewById(R.id.editTextStok);
        stok = findViewById(R.id.stok);
        kutu  = findViewById(R.id.kutu);
        stok.setText(stokMiktari);

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL); // Cache the image for better performance

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child(resimDosyaAdi);

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(StokDuzenleOnay.this)
                        .load(uri)
                        .apply(requestOptions)
                        .into(urunFoto);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors that occur during image loading
            }
        });
        kategoriBul(urunAdi).thenAccept(result -> {

            if (!result.equals("stokhelva")){
                kutu.setVisibility(View.INVISIBLE);

            }
        });

        stokOnay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String yeniStok = editTextStok.getText().toString();
                updateStokMiktari(yeniStok);
                finish();
            }
        });
        kutu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStokMiktari(Integer.toString(Integer.parseInt(stokMiktari)-1));
                Intent intent = new Intent(StokDuzenleOnay.this,StokDuzenle.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(StokDuzenleOnay.this,StokDuzenle.class);
        startActivity(intent);
    }

    private void updateStokMiktari(String yeniStok) {
        DocumentReference urunRef = firestore.collection("Urunler").document(urunAdi);
        urunRef.update("stokMiktari", yeniStok)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        stok.setText(yeniStok);
                        // Başarılı bir şekilde güncellendi, gerekli işlemleri yapabilirsiniz
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Güncelleme sırasında bir hata oluştu, hata durumunu işleyebilirsiniz
                    }
                });
    }
    private CompletableFuture<String> kategoriBul(String urunAdi) {
        CompletableFuture<String> future = new CompletableFuture<>();
        DocumentReference urunRef = firestore.collection("Urunler").document(urunAdi);
        urunRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String kategori = documentSnapshot.getString("kategori");
                    future.complete(kategori);
                }
            }
        });

        return future;
    }
    private int toplama(int a , int b){

        return a+b;

    }


}
