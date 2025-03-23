package com.potato.barcodescanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ItemModel {
    private int id;
    private String name;
    private String barcode;
    private byte[] image;

    public ItemModel(int id, String name, String barcode, byte[] image) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.image = image;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
