package com.android.favour.NetworkIO;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.favour.NetworkIO.GsonRequest.RequestType;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WebServiceClient
        implements Response.Listener<WebServiceClient.IOResponse>, Response.ErrorListener{

    Listener callerActivity;
    Context activityContext;
    Type responseType;
    RequestQueue requestQueue;
    GsonRequest gsonRequest;
    private final String LOG_APP_TAG = "FAVOUR";

    private static final int VOLLEY_ERR_RESPONSE_CODE = -11;
    private static final int TIMEOUT_IN_SECS = 30;

    public WebServiceClient(Listener activityListener, final Type responseType) {
        this.callerActivity = activityListener;
        this.activityContext = activityListener.getListenerContext();
        this.responseType = com.google.gson.internal.$Gson$Types
                .newParameterizedTypeWithOwner(null, DataServiceWrapper.class, responseType);
    }

    public void makeGet(String uri) {
        this.makeRequest(RequestType.GET, uri, null);
    }

    public void makePost(String uri, Object dataObject){
        this.makeRequest(RequestType.POST, uri, dataObject);
    }

    private void makeRequest(RequestType requestType, String url, Object dataObject){

        Log.d(LOG_APP_TAG, url);
        try {
            gsonRequest = new GsonRequest(requestType, url, dataObject, responseType, this);
            requestQueue = VolleyService.getInstance(activityContext).getHttpRequestQueue();
            requestQueue.add(gsonRequest);
        }
        catch(Exception err){
            Log.e(LOG_APP_TAG, err.toString(), err);
        }
    }


    public Object makeBlockingRequest(Context context, RequestType requestType,
                                             String url, Type respType, JSONObject jsonRequest) {
        GsonRequest gsonRequest;
        Log.d(LOG_APP_TAG, url);
        try {
            RequestFuture<IOResponse> requestFuture = RequestFuture.newFuture();
            gsonRequest = new GsonRequest(requestType, url, jsonRequest, responseType, requestFuture);
            RequestQueue requestQueue = VolleyService.getInstance(context).getHttpRequestQueue();
            requestQueue.add(gsonRequest);

            IOResponse resp = requestFuture.get(TIMEOUT_IN_SECS, TimeUnit.SECONDS);
            return resp.getDataObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onResponse(IOResponse response) {
        if(response.getStatus() != 0) {
            callerActivity.onErrorResponse(response.getError());
        }
        else {
            callerActivity.onSuccessResponse(response.getDataObject());
        }
    }

    @Override
    public void onErrorResponse(VolleyError e) {
        String errMessage = (e instanceof TimeoutError) ? e.toString() : e.getMessage();
       // Log.e(LOG_APP_TAG, errMessage);
        Log.d(LOG_APP_TAG, Log.getStackTraceString(e));

        callerActivity.onErrorResponse(errMessage);
    }

    public interface Listener<T> {
        void onSuccessResponse(T dataObj);
        void onErrorResponse(String errMessage);
        Context getListenerContext();
    }

    public interface IOResponse {

        int getStatus();

        String getMessage();
        String getError();
        Object getDataObject();
    }
}
