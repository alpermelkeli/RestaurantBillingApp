package com.alpermelkeli.billapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SicakSiparis extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private StorageReference storage;
    RecyclerView recyclerViewSicak;

    String masaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sicak_siparis);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        masaId = getIntent().getStringExtra("masaId");
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        recyclerViewSicak = findViewById(R.id.recyclerViewSicak);
        recyclerViewSicak.setLayoutManager(new GridLayoutManager(this, 2)); // 2 sütunlu bir ızgara görünümü için
        retrieveSogukUrunler();

    }

    public class Product {
        private String urunAdi;
        private String resimDosyaAdi;
        private String fiyat;

        public Product(String urunAdi, String resimDosyaAdi, String fiyat) {
            this.urunAdi = urunAdi;
            this.resimDosyaAdi = resimDosyaAdi;
            this.fiyat = fiyat;
        }

        public String getUrunAdi() {
            return urunAdi;
        }

        public String getResimDosyaAdi() {
            return resimDosyaAdi;
        }
        public String getFiyat(){

            return fiyat;

        }
    }



    private void retrieveSogukUrunler() {
        CollectionReference urunlerCollectionRef = firestore.collection("Urunler");

        // "soguk" kategorisindeki ürünleri sorgula
        urunlerCollectionRef.whereEqualTo("kategori", "sicak")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> productList = new ArrayList<>();

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        // Firestore'dan çekilen her bir belgeyi Product nesnesine dönüştür
                        String urunAdi = document.getString("urunAdi");
                        String resimDosyaAdi = "Products/sicak/" + document.getString("urunAdi") + ".jpg";
                        String fiyat = document.getString("fiyat");
                        Product product = new Product(urunAdi, resimDosyaAdi,fiyat);
                        productList.add(product);
                    }

                    // RecyclerView adapter'ını oluştur ve verileri ata
                    ProductAdapter adapter = new ProductAdapter(productList);
                    recyclerViewSicak.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    // Hata durumunu işle
                });
    }


    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

        private List<Product> productList;

        public ProductAdapter(List<Product> productList) {
            this.productList = productList;
        }

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            Product product = productList.get(position);
            holder.bind(product);
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }
        public class ProductViewHolder extends RecyclerView.ViewHolder {

            private ImageView imageView;
            private TextView textView;

            public ProductViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
                textView = itemView.findViewById(R.id.textView);
            }

            public void bind(Product product) {
                // Ürün adını ve fotoğrafını görüntüle
                textView.setText(product.getUrunAdi() + " · " + product.getFiyat()+ "₺");



                // Firebase Storage'dan ürün fotoğrafını yükle
                String imagePath = product.getResimDosyaAdi();
                StorageReference photoRef = storage.child(imagePath);
                photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(SicakSiparis.this)
                                .load(uri)
                                .centerCrop()
                                .into(imageView); // Use the final copy of the index variable
                    }
                });
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println(product.getUrunAdi());
                        Intent intent = new Intent(SicakSiparis.this,SiparisOnay.class);
                        intent.putExtra("urunAdi",product.getUrunAdi());
                        intent.putExtra("resimDosyaAdi",product.getResimDosyaAdi());
                        intent.putExtra("fiyat", product.getFiyat());
                        intent.putExtra("masaID", masaId);
                        startActivity(intent);

                    }
                });



            }
        }
    }


}
