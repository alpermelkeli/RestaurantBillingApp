package com.alpermelkeli.billapp;

public class SiparisDetayi {
    private String urunAdi;
    private String adet;
    private String notlar;

    public SiparisDetayi(String urunAdi, String adet, String notlar) {
        this.urunAdi = urunAdi;
        this.adet = adet;
        this.notlar = notlar;
    }

    public String getUrunAdi() {
        return urunAdi;
    }

    public String getAdet() {
        return adet;
    }

    public String getNotlar() {
        return notlar;
    }
}

