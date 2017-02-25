package com.android.favour.NetworkIO;

import com.google.gson.annotations.SerializedName;

public class DataServiceWrapper<T> implements WebServiceClient.IOResponse {
    private int status;
    private String message;
    private String error;
    @SerializedName("data")
    private T dataObject;

    public DataServiceWrapper() { }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getError() { return error; }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public T getDataObject() {
        return dataObject;
    }

}
