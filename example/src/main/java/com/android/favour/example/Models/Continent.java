package com.android.favour.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Continent implements Serializable {
    @SerializedName("code")
    String code;
    @SerializedName("name")
    String name;
}
