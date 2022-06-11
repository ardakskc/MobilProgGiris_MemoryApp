package com.example.noteapp;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.net.URI;

public class Notes implements Serializable {
    private int Id;
    private String Baslik;
    private String date;
    private String text;
    private int mood;
    private String lokasyon;
    private String foto;
    private String sifre;


    public Notes(String baslik, String date, String text, int mood, String lokasyon) {
        Baslik = baslik;
        this.date = date;
        this.text = text;
        this.mood = mood;
        this.lokasyon = lokasyon;
        this.foto="NULL";
        this.sifre="NULL";
    }

    public Notes(int id, String baslik, String date, String text, int mood, String lokasyon,String sifre) {
        Id = id;
        Baslik = baslik;
        this.date = date;
        this.text = text;
        this.mood = mood;
        this.lokasyon = lokasyon;
        this.foto="NULL";
        this.sifre = sifre;
    }

    public Notes(String baslik, String date, String text, int mood, String lokasyon, String foto) {
        Baslik = baslik;
        this.date = date;
        this.text = text;
        this.mood = mood;
        this.lokasyon = lokasyon;
        this.foto = foto;
        this.sifre="NULL";
    }

    public Notes(int id, String baslik, String date, String text, int mood, String lokasyon,String sifre, String foto) {
        Id = id;
        Baslik = baslik;
        this.date = date;
        this.text = text;
        this.mood = mood;
        this.lokasyon = lokasyon;
        this.foto = foto;
        this.sifre = sifre;
    }
    public Boolean pht_isNull(){
        if (foto.equals("NULL")){
            return Boolean.TRUE;
        }else {
            return Boolean.FALSE;
        }
    }

    public String getSifre() {
        return sifre;
    }

    public void setSifre(String sifre) {
        this.sifre = sifre;
    }

    public String getLokasyon() {
        return lokasyon;
    }

    public void setLokasyon(String lokasyon) {
        this.lokasyon = lokasyon;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getBaslik() {
        return Baslik;
    }

    public void setBaslik(String baslik) {
        Baslik = baslik;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
