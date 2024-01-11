package com.alpermelkeli.billapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.CompletableFuture;

public class OdemeOnay extends AppCompatActivity {
    String urunAdi;
    String orderID;
    String masaID;
    String adet;
    double fiyat;

    TextView urunAdiTextView;
    TextView orderIDTextView;
    TextView masaIDTextView;
    TextView adetTextView;
    Button krediKartiButton;
    Button pesinButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odeme_onay);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        urunAdi = getIntent().getStringExtra("urunAdi");
        orderID = getIntent().getStringExtra("orderID");
        masaID = getIntent().getStringExtra("masaID");
        adet = getIntent().getStringExtra("adet");
        initializeViews();
        urunAdiTextView.setText("Ürün Adı: " + urunAdi);
        orderIDTextView.setText("Order ID: " + orderID);
        masaIDTextView.setText("Masa ID: " + masaID);
        adetTextView.setText("Adet: " + adet);

        krediKartiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kredi kartı seçildiğinde yapılacak işlemler
                Toast.makeText(OdemeOnay.this, "Kredi Kartı seçildi", Toast.LENGTH_SHORT).show();
                showConfirmationDialog("Kredi Kartı", "Kredi Kartı ile ödemeyi onaylıyor musunuz?");

            }
        });

        pesinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Peşin seçildiğinde yapılacak işlemler
                Toast.makeText(OdemeOnay.this, "Peşin seçildi", Toast.LENGTH_SHORT).show();
                showConfirmationDialog("Peşin", "Peşin ödemeyi onaylıyor musunuz?");


            }
        });



    }
    private void initializeViews(){
        urunAdiTextView = findViewById(R.id.urunAdiTextView);
        orderIDTextView = findViewById(R.id.orderIDTextView);
        masaIDTextView = findViewById(R.id.masaIDTextView);
        adetTextView = findViewById(R.id.adetTextView);
        krediKartiButton = findViewById(R.id.krediKartiButton);
        pesinButton = findViewById(R.id.pesinButton);

    }
    private void showConfirmationDialog(String paymentMethod, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(OdemeOnay.this);
        builder.setTitle("Ödeme Onayı");
        builder.setMessage(message);
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                decreaseMasa(paymentMethod);
                finish();
            }
        });
        builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void decreaseMasa(String paymentMethod) {
        findUrunFiyat().thenAccept(result -> {
            siparisleriGuncelle();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            CollectionReference masalarCollectionRef = firestore.collection("Masalar");

            DocumentReference masaDocumentRef = masalarCollectionRef.document(masaID);
            masaDocumentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    double toplamFiyat = documentSnapshot.getDouble("toplamFiyat");

                    if (toplamFiyat > 0) {
                        double azaltilanMiktar = Double.parseDouble(result) * Integer.parseInt(adet);
                        fiyat = azaltilanMiktar;
                        ciroyaEkle(paymentMethod,fiyat);
                        double yeniToplamFiyat = toplamFiyat - azaltilanMiktar;
                        if (yeniToplamFiyat < 0) {
                            yeniToplamFiyat = 0.0;
                        }

                        masaDocumentRef.update("toplamFiyat", yeniToplamFiyat)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Başarılı bir şekilde güncellendi
                                        Toast.makeText(OdemeOnay.this, "Masa toplam fiyatı güncellendi", Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                }
            });
        });
    }

    private CompletableFuture<String> findUrunFiyat(){

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference urunlerCollectionRef = firestore.collection("Urunler");
        DocumentReference urunDocumentRef = urunlerCollectionRef.document(urunAdi);
        CompletableFuture<String> future = new CompletableFuture<>();

        urunDocumentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                future.complete((String) documentSnapshot.get("fiyat"));
            }
        });
        return future;

    }
    private void siparisleriGuncelle() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference siparislerCollectionRef = firestore.collection("Siparisler");
        DocumentReference siparisDocumentRef = siparislerCollectionRef.document(orderID);
        CollectionReference siparisDetaylariCollectionRef = siparisDocumentRef.collection("siparisDetaylari");

        stokAzalt();

        siparisDetaylariCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if (!querySnapshot.isEmpty()) {
                    DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                    String siparisDetayDokumanID = documentSnapshot.getId();
                    CollectionReference siparislerCollectionRef = firestore.collection("Siparisler");
                    DocumentReference siparisDocumentRef = siparislerCollectionRef.document(orderID);
                    DocumentReference siparisDetaylariDocumentRef = siparisDetaylariCollectionRef.document(siparisDetayDokumanID);
                    siparisDocumentRef.delete();
                    siparisDetaylariDocumentRef.delete();

                }
            }
        });
    }
    public class Ciro {
        private String odemeTipi;
        private long zaman;
        private double fiyat;
        private String urunAdi;
        private String adet;

        public Ciro(String odemeTipi, long zaman, double fiyat, String urunAdi, String adet) {
            this.odemeTipi = odemeTipi;
            this.zaman = zaman;
            this.fiyat = fiyat;
            this.urunAdi = urunAdi;
            this.adet = adet;
        }

        public String getOdemeTipi() {
            return odemeTipi;
        }

        public void setOdemeTipi(String odemeTipi) {
            this.odemeTipi = odemeTipi;
        }

        public long getZaman() {
            return zaman;
        }

        public void setZaman(long zaman) {
            this.zaman = zaman;
        }

        public double getFiyat() {
            return fiyat;
        }

        public void setFiyat(double fiyat) {
            this.fiyat = fiyat;
        }

        public String getUrunAdi() {
            return urunAdi;
        }

        public void setUrunAdi(String urunAdi) {
            this.urunAdi = urunAdi;
        }

        public String getAdet() {
            return adet;
        }

        public void setAdet(String adet) {
            this.adet = adet;
        }
    }

    private void ciroyaEkle(String paymentMethod, double fiyat) {
        // Ciro verilerini oluştur
        String odemeTipi = paymentMethod; // Ödeme tipini burada belirleyin
        long zaman = System.currentTimeMillis(); // Zamanı şu anki zaman olarak al


        // Firestore referansını al
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference ciroCollectionRef = firestore.collection("Ciro");

        // Yeni bir ciro dokümanı oluştur
        DocumentReference yeniCiroRef = ciroCollectionRef.document();

        // Ciro verilerini ekle
        Ciro ciro = new Ciro(odemeTipi, zaman, (fiyat), urunAdi, adet);
        yeniCiroRef.set(ciro)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Ciro başarıyla oluşturuldu
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Ciro oluşturulamadı
                    }
                });
    }

    private void stokAzalt(){
        FirebaseFirestore firestore;
        firestore = FirebaseFirestore.getInstance();
        CollectionReference urunlerCollectionRef = firestore.collection("Urunler");
        DocumentReference urunlerRef = urunlerCollectionRef.document(urunAdi);

        urunlerRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String stokMiktari = documentSnapshot.getString("stokMiktari");
                try {
                    urunlerRef.update("stokMiktari", Integer.toString(Integer.parseInt(stokMiktari) - Integer.parseInt(adet)));

                }catch (Exception e){
                    System.out.println(e);
                }

            }
        });



    }




}
