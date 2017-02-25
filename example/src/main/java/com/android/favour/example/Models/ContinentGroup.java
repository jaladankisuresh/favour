package com.android.favour.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ContinentGroup extends Continent
    implements Selection.SelectableGroup, Serializable {
    @SerializedName("countries")
    List<Country> countries;

    @Override
    public List<Country> getCollection() {
        return countries;
    }

    @Override
    public String getTitle() {
        return name;
    }
}
