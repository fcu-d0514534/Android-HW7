package com.example.user.myapplication;

import android.graphics.Bitmap;

/**
 * Created by user on 2017/6/6.
 */

public class Pet {

    private Bitmap imgUrl;
    private String kind;
    private String shelter;

    public Bitmap getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(Bitmap imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getShelter() {
        return shelter;
    }

    public void setShelter(String shelter) {
        this.shelter = shelter;
    }
}
