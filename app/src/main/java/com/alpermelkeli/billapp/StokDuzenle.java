package com.alpermelkeli.billapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class StokDuzenle extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private StorageReference storage;
    private RecyclerView recyclerViewSoguk, recyclerViewHelva, recyclerViewSicak;
    private String masaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stok_duzenle);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        recyclerViewSoguk = findViewById(R.id.recyclerViewSoguk);
        recyclerViewHelva = findViewById(R.id.recyclerViewHelva);
        recyclerViewSicak = findViewById(R.id.recyclerViewSicak);
        recyclerViewSoguk.setLayoutManager(new GridLayoutManager(this, 2)); // 2 sütunlu bir ızgara görünümü için
        recyclerViewHelva.setLayoutManager(new GridLayoutManager(this, 2)); // 2 sütunlu bir ızgara görünümü için
        recyclerViewSicak.setLayoutManager(new GridLayoutManager(this, 2)); // 2 sütunlu bir ızgara görünümü için
        retrieveUrunler("stokhelva", recyclerViewHelva);
        retrieveUrunler("soguk", recyclerViewSoguk);
        retrieveUrunler("sicak", recyclerViewSicak);
    }

    private void retrieveUrunler(String kategori, RecyclerView recyclerView) {
        CollectionReference urunlerCollectionRef = firestore.collection("Urunler");

        // Belirli kategorideki ürünleri sorgula
        urunlerCollectionRef.whereEqualTo("kategori", kategori)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> productList = new ArrayList<>();

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        // Firestore'dan çekilen her bir belgeyi Product nesnesine dönüştür
                        String urunAdi = document.getString("urunAdi");
                        String resimDosyaAdi = "Products/" + kategori + "/" + document.getString("urunAdi") + ".jpg";
                        String stokMiktar = document.getString("stokMiktari");
                        if (stokMiktar == null) {
                            stokMiktar = "0";
                        }

                        Product product = new Product(urunAdi, resimDosyaAdi, stokMiktar);
                        productList.add(product);
                    }

                    // RecyclerView adapter'ını oluştur ve verileri ata
                    ProductAdapter adapter = new ProductAdapter(productList);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    // Hata durumunu işle
                });
    }

    public class Product {

        private String urunAdi;
        private String resimDosyaAdi;
        private String stokMiktar;

        public Product(String urunAdi, String resimDosyaAdi, String stokMiktar) {
            this.urunAdi = urunAdi;
            this.resimDosyaAdi = resimDosyaAdi;
            this.stokMiktar = stokMiktar;
        }

        public String getUrunAdi() {
            return urunAdi;
        }

        public String getResimDosyaAdi() {
            return resimDosyaAdi;
        }

        public String getStokMiktar() {
            return stokMiktar;
        }

    }

    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
        private List<Product> productList;

        public ProductAdapter(List<Product> productList) {
            this.productList = productList;
        }

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product1, parent, false);
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
                textView.setText(product.getUrunAdi() + " · Stok Miktarı: " + product.getStokMiktar());

                String imagePath = product.getResimDosyaAdi();
                StorageReference photoRef = storage.child(imagePath);
                photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(itemView.getContext())
                                .load(uri)
                                .centerCrop()
                                .into(imageView);
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(itemView.getContext(), StokDuzenleOnay.class);
                        intent.putExtra("urunAdi", product.getUrunAdi());
                        intent.putExtra("resimDosyaAdi", product.getResimDosyaAdi());
                        intent.putExtra("stokMiktari", product.getStokMiktar());
                        itemView.getContext().startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(StokDuzenle.this,GarsonSettings.class);
        startActivity(intent);
    }
}
