package com.alpermelkeli.billapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrderAdapterKasa extends RecyclerView.Adapter<OrderAdapterKasa.OrderViewHolder> {
    private List<Order> orderList;


    public OrderAdapterKasa(List<Order> orderList) {
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

        holder.tableIdTextView.setText("" + order.getTableId());
        holder.orderDateTextView.setText("Sipariş tarihi: " + convertTime(order.getOrderDate()));
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




    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
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
        public void bind(Order order){
            itemView.setOnClickListener(new View.OnClickListener() {
                String urunAdi;
                String adet;
                String notlar;
                @Override
                public void onClick(View v) {
                    List<SiparisDetayi> siparisDetaylari = order.getSiparisDetaylari();
                    StringBuilder siparisDetaylariText = new StringBuilder();

                    for (SiparisDetayi siparisDetayi : siparisDetaylari) {
                        urunAdi = siparisDetayi.getUrunAdi();
                        adet = siparisDetayi.getAdet();
                        notlar = siparisDetayi.getNotlar();

                        String siparisDetay = "Adet: " + urunAdi + ", Ürün Adı: " + notlar;
                    }


                    Intent intent = new Intent(itemView.getContext(), OdemeOnay.class);
                    intent.putExtra("orderID", order.getOrderId());
                    intent.putExtra("masaID",order.getTableId());
                    intent.putExtra("urunAdi", notlar);
                    intent.putExtra("adet", urunAdi);
                    itemView.getContext().startActivity(intent);


                }
            });


        }

    }
    public String convertTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
        return format.format(date);
    }
}
