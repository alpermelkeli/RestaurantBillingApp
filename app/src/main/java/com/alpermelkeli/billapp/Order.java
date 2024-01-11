package com.alpermelkeli.billapp;

import java.util.Date;

import java.util.List;

public class Order {
    private String orderId;
    private String tableId;
    private long orderDate;
    private String paymentStatus;
    private List<SiparisDetayi> siparisDetaylari;

    public Order(String orderId, String tableId, long orderDate, String paymentStatus, List<SiparisDetayi> siparisDetaylari) {
        this.orderId = orderId;
        this.tableId = tableId;
        this.orderDate = orderDate;
        this.paymentStatus = paymentStatus;
        this.siparisDetaylari = siparisDetaylari;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getTableId() {
        return tableId;
    }

    public long getOrderDate() {
        return orderDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public List<SiparisDetayi> getSiparisDetaylari() {
        return siparisDetaylari;
    }
}


