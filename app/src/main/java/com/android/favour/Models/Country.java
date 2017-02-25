package com.android.favour.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Country
        implements Serializable, Selection.Selectable{

    @SerializedName("id")
    private int id;
    @SerializedName("code")
    private String code;
    @SerializedName("name")
    private String name;
    @SerializedName("img")
    private String img;
    @SerializedName("continentCode")
    private String region;
    @SerializedName("selected")
    private boolean isSelected = false;

    @Override
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getImage() {
        return img;
    }

    public String getRegion() {
        return region;
    }

    @Override
    public String toString() {
        return getName();
    }
}
