package com.alpermelkeli.billapp;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;


public class CiroFragment extends Fragment {
    private TextView krediKarti;
    private TextView pesin;
    private TextView toplamSiparisTutariTextView;
    private TextView siparisSayisiTextView;
    private TextView toplamUrunSayisiTextView;
    private Button baslangicTarihSecButton;
    private Button bitisTarihSecButton;
    private RecyclerView urunlerRecycler;
    private Calendar baslangicTarihi;
    private Calendar bitisTarihi;
    private SimpleDateFormat dateFormat;
    private FirebaseFirestore firestore;
    StorageReference storage;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ciro, container, false);
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        urunlerRecycler = view.findViewById(R.id.urunlerRecycler);

        urunlerRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        pesin = view.findViewById(R.id.pesin);
        krediKarti  =view.findViewById(R.id.krediKarti);

        toplamSiparisTutariTextView = view.findViewById(R.id.toplamSiparisTutariTextView);
        siparisSayisiTextView = view.findViewById(R.id.siparisSayisiTextView);
        toplamUrunSayisiTextView = view.findViewById(R.id.toplamUrunSayisiTextView);
        baslangicTarihSecButton = view.findViewById(R.id.baslangicTarihSecButton);
        bitisTarihSecButton = view.findViewById(R.id.bitisTarihSecButton);

        baslangicTarihi = Calendar.getInstance();
        bitisTarihi = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        baslangicTarihSecButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(true);
            }
        });

        bitisTarihSecButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(false);
            }
        });

        // Başlangıç ve bitiş tarihlerini varsayılan olarak bugün olarak ayarla
        updateSelectedDate();

        // Ciro verilerini almak için Firestore referansını al
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference ciroCollectionRef = firestore.collection("Ciro");

        // İstenilen tarih aralığındaki ciro verilerini sorgula
        Query ciroQuery = ciroCollectionRef
                .whereGreaterThanOrEqualTo("zaman", baslangicTarihi.getTimeInMillis())
                .whereLessThanOrEqualTo("zaman", bitisTarihi.getTimeInMillis());

        // Sorguyu gerçekleştir
        ciroQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                // Sorgu başarılı olduğunda yapılacak işlemler burada gerçekleştirilir

                // Toplam sipariş tutarı
                double toplamSiparisTutari = 0;


                // Sipariş sayısı
                int siparisSayisi = querySnapshot.size();

                // Sipariş edilen ürünlerin toplam sayısı
                int toplamUrunSayisi = 0;

                for (DocumentSnapshot document : querySnapshot.getDocuments()) {




                    // Sipariş tutarı
                    double siparisTutari = document.getDouble("fiyat");
                    toplamSiparisTutari += siparisTutari;


                    // Sipariş edilen ürün sayısı
                    int adet = Integer.parseInt(document.getString("adet"));
                    toplamUrunSayisi += adet;
                }

                // Elde edilen verileri kullanarak istediğiniz işlemleri gerçekleştirin
                // Örneğin, bu verileri bir TextView'e veya başka bir görsel öğeye yerleştirebilirsiniz
                toplamSiparisTutariTextView.setText("Toplam Sipariş Tutarı: " + toplamSiparisTutari);
                siparisSayisiTextView.setText("Sipariş Sayısı: " + siparisSayisi);
                toplamUrunSayisiTextView.setText("Toplam Ürün Sayısı: " + toplamUrunSayisi);

            }
        });
        retrieveSogukUrunler();

        return view;
    }

    private void showDatePickerDialog(final boolean isBaslangicTarihi) {
        DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (isBaslangicTarihi) {
                    baslangicTarihi.set(Calendar.YEAR, year);
                    baslangicTarihi.set(Calendar.MONTH, month);
                    baslangicTarihi.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                } else {
                    bitisTarihi.set(Calendar.YEAR, year);
                    bitisTarihi.set(Calendar.MONTH, month);
                    bitisTarihi.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                }

                updateSelectedDate();
            }
        };

        Calendar selectedDate;
        if (isBaslangicTarihi) {
            selectedDate = baslangicTarihi;
        } else {
            selectedDate = bitisTarihi;
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                datePickerListener,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void updateSelectedDate() {

        String baslangicTarihiStr = dateFormat.format(baslangicTarihi.getTime());
        String bitisTarihiStr = dateFormat.format(bitisTarihi.getTime());
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference ciroCollectionRef = firestore.collection("Ciro");
        baslangicTarihSecButton.setText("Başlangıç Tarihi: " + baslangicTarihiStr);
        bitisTarihSecButton.setText("Bitiş Tarihi: " + bitisTarihiStr);

        // İstenilen tarih aralığındaki ciro verilerini sorgula
        Query ciroQuery = ciroCollectionRef
                .whereGreaterThanOrEqualTo("zaman", baslangicTarihi.getTimeInMillis())
                .whereLessThanOrEqualTo("zaman", bitisTarihi.getTimeInMillis());

        // Sorguyu gerçekleştir
        ciroQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                // Sorgu başarılı olduğunda yapılacak işlemler burada gerçekleştirilir
                double krediSiparis = 0;
                double pesinSiparis = 0;
                // Toplam sipariş tutarı
                double toplamSiparisTutari = 0;

                // Sipariş sayısı
                int siparisSayisi = querySnapshot.size();

                // Sipariş edilen ürünlerin toplam sayısı
                int toplamUrunSayisi = 0;

                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    if (document.getString("odemeTipi").equals("Kredi Kartı")){
                        double krediSiparisMiktari = document.getDouble("fiyat");
                        krediSiparis += krediSiparisMiktari;

                    }
                    else if (document.getString("odemeTipi").equals("Peşin")){
                        double pesinSiparisMiktari = document.getDouble("fiyat");
                        pesinSiparis += pesinSiparisMiktari;

                    }
                    // Her bir ciro belgesi için işlemler yapılır

                    // Sipariş tutarı
                    double siparisTutari = document.getDouble("fiyat");
                    toplamSiparisTutari += siparisTutari;

                    // Sipariş edilen ürün sayısı
                    int adet = Integer.parseInt(document.getString("adet"));
                    toplamUrunSayisi += adet;
                }

                // Elde edilen verileri kullanarak istediğiniz işlemleri gerçekleştirin
                // Örneğin, bu verileri bir TextView'e veya başka bir görsel öğeye yerleştirebilirsiniz
                toplamSiparisTutariTextView.setText("Toplam Sipariş Tutarı: " + toplamSiparisTutari);
                siparisSayisiTextView.setText("Sipariş Sayısı: " + siparisSayisi);
                toplamUrunSayisiTextView.setText("Toplam Ürün Sayısı: " + toplamUrunSayisi);
                pesin.setText("Peşin: " + Double.toString(pesinSiparis));
                krediKarti.setText("Kredi kartı: "+Double.toString(krediSiparis));

            }
        });
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
        urunlerCollectionRef
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> productList = new ArrayList<>();
                    ProductAdapter adapter = new ProductAdapter(productList);
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        if(!document.getString("kategori").equals("stokhelva")){
                            String urunAdi = document.getString("urunAdi");
                            String resimDosyaAdi = "Products/" + document.getString("kategori") + "/" + document.getId() + ".jpg";
                            CompletableFuture<String> fiyatFuture = findSatısSayısı(urunAdi,baslangicTarihi,bitisTarihi);
                            fiyatFuture.thenAccept(fiyat -> {
                                System.out.println(fiyat);
                                Product product = new Product(urunAdi, resimDosyaAdi, fiyat);
                                productList.add(product);

                                // Ürün listesi güncellendikçe RecyclerView adapter'ına bilgi ver
                                adapter.notifyDataSetChanged();
                            });
                        }

                    }
                    // RecyclerView adapter'ını oluştur ve verileri ata

                    urunlerRecycler.setAdapter(adapter);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product1, parent, false);
            return new ProductAdapter.ProductViewHolder(view);
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
                textView.setText(product.getUrunAdi() + " · "+ "Satılan miktar: " + product.getFiyat());

                // Firebase Storage'dan ürün fotoğrafını yükle
                String imagePath = product.getResimDosyaAdi();
                StorageReference photoRef = storage.child(imagePath);
                photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getActivity())
                                .load(uri)
                                .centerCrop()
                                .into(imageView); // Use the final copy of the index variable
                    }
                });




            }
        }
    }
    public CompletableFuture<String> findSatısSayısı(String urunAdi, Calendar baslangicTarihi, Calendar bitisTarihi) {

        CompletableFuture<String> satısSayısı = new CompletableFuture<>();
        // Ciro koleksiyonuna erişim için Firestore referansını al
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference ciroCollectionRef = firestore.collection("Ciro");
        Query ciroQuery = ciroCollectionRef
                .whereGreaterThanOrEqualTo("zaman", baslangicTarihi.getTimeInMillis())
                .whereLessThanOrEqualTo("zaman", bitisTarihi.getTimeInMillis());

        // Ürün adına eşit olan belgeleri sorgula

        ciroQuery.get().addOnSuccessListener(querySnapshot -> {
            int adetToplam = 0;

            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                // Her bir ciro belgesi için adet değerini toplama ekle
                if (document.getString("urunAdi").equals(urunAdi)){
                    int adet = Integer.parseInt(document.getString("adet"));
                    adetToplam += adet;

                }

            }
            satısSayısı.complete(Integer.toString(adetToplam));
        }).addOnFailureListener(e -> {
            // Hata durumunda işle
        });
        return satısSayısı;
    }
}