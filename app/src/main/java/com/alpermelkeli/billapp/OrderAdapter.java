package com.alpermelkeli.billapp;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tableIdTextView.setText(order.getTableId());
        holder.orderDateTextView.setText("Sipariş Tarihi: " + convertTime(order.getOrderDate()));
        holder.paymentStatusTextView.setText("Ödeme: " + order.getPaymentStatus());

        List<SiparisDetayi> siparisDetaylari = order.getSiparisDetaylari();
        StringBuilder siparisDetaylariText = new StringBuilder();

        for (SiparisDetayi siparisDetayi : siparisDetaylari) {
            String urunAdi = siparisDetayi.getUrunAdi();
            String adet = siparisDetayi.getAdet();
            String notlar = siparisDetayi.getNotlar();

            String siparisDetay = "Adet: " + urunAdi + ", Ürün Adı: " + notlar;


            siparisDetaylariText.append(siparisDetay).append("\n");
        }

        holder.siparisDetaylariTextView.setText(siparisDetaylariText.toString());
        holder.bind(order);

    }


    public List<Order> getOrderList() {
        return orderList;
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView orderIdTextView;
        public TextView tableIdTextView;
        public TextView orderDateTextView;
        public TextView paymentStatusTextView;
        public TextView siparisDetaylariTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tableIdTextView = itemView.findViewById(R.id.tableIdTextView);
            orderDateTextView = itemView.findViewById(R.id.orderDateTextView);
            paymentStatusTextView = itemView.findViewById(R.id.paymentStatusTextView);
            siparisDetaylariTextView = itemView.findViewById(R.id.siparisDetaylariTextView);
        }
        public void bind(Order order) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog(order);
                }
            });
        }

        private void showDeleteConfirmationDialog(Order order) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Siparişi Sil");
            builder.setMessage("Seçilen siparişi silmek istediğinizden emin misiniz?");
            builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    siparisleriGuncelle(order);
                }
            });
            builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        private void siparisleriGuncelle(Order order) {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            CollectionReference siparislerCollectionRef = firestore.collection("Siparisler");
            DocumentReference siparisDocumentRef = siparislerCollectionRef.document(order.getOrderId());
            CollectionReference siparisDetaylariCollectionRef = siparisDocumentRef.collection("siparisDetaylari");



            siparisDetaylariCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                        String siparisDetayDokumanID = documentSnapshot.getId();
                        CollectionReference siparislerCollectionRef = firestore.collection("Siparisler");
                        DocumentReference siparisDocumentRef = siparislerCollectionRef.document(order.getOrderId());
                        DocumentReference siparisDetaylariDocumentRef = siparisDetaylariCollectionRef.document(siparisDetayDokumanID);
                        siparisDocumentRef.delete();
                        siparisDetaylariDocumentRef.delete();
                        orderList.remove(order);
                        notifyDataSetChanged();
                        decreaseMasa(order);


                    }
                }

            });


        }
        private void decreaseMasa(Order order) {
            findUrunFiyat(order).thenAccept(result -> {

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                CollectionReference masalarCollectionRef = firestore.collection("Masalar");

                DocumentReference masaDocumentRef = masalarCollectionRef.document(order.getTableId());
                masaDocumentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        double toplamFiyat = documentSnapshot.getDouble("toplamFiyat");

                        if (toplamFiyat > 0) {
                            double azaltilanMiktar = Double.parseDouble(result) * Integer.parseInt(order.getSiparisDetaylari().get(0).getUrunAdi());
                            double fiyat = azaltilanMiktar;
                            double yeniToplamFiyat = toplamFiyat - azaltilanMiktar;

                            if (yeniToplamFiyat < 0) {
                                yeniToplamFiyat = 0.0;
                            }

                            masaDocumentRef.update("toplamFiyat", yeniToplamFiyat)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Başarılı bir şekilde güncellendi
                                            Toast.makeText(itemView.getContext(), "Masa toplam fiyatı güncellendi", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
            });
        }

        private CompletableFuture<String> findUrunFiyat(Order order){

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            CollectionReference urunlerCollectionRef = firestore.collection("Urunler");
            DocumentReference urunDocumentRef = urunlerCollectionRef.document(order.getSiparisDetaylari().get(0).getNotlar());
            CompletableFuture<String> future = new CompletableFuture<>();

            urunDocumentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    future.complete((String) documentSnapshot.get("fiyat"));
                }
            });
            return future;

        }


    }

    public String convertTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
        return format.format(date);
    }

}


