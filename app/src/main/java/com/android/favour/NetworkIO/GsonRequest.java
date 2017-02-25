package com.android.favour.NetworkIO;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Change this code judiciously as the code had been adapted from the following google site
 * http://developer.android.com/training/volley/request-custom.html
 *
 */

public class GsonRequest extends JsonRequest<WebServiceClient.IOResponse> {

    private static Gson gson;
    private final Type responseType;
    private final Map<String, String> headers;
    private final Response.Listener listener;

    private final int timeout = 10000, maxRetries = 2, backOffMult = 2;

    /**
     * Make a Post request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param responseType Relevant Type, for Gson's reflection
     * //@param headers Map of request headers
     */

    public GsonRequest(RequestType requestType, String url, Type responseType,
                       Response.Listener<WebServiceClient.IOResponse> listener)
                    throws Exception {
        this(requestType, url, null, responseType, null, listener);
    }

    public GsonRequest(RequestType requestType, String url, Object dataObject, Type responseType,
                       Response.Listener<WebServiceClient.IOResponse> listener)
                        throws Exception {
        this(requestType, url, dataObject, responseType, null, listener);
    }

    public GsonRequest(RequestType requestType, String url, Object dataObject, Type responseType, Map<String, String> headers,
                       Response.Listener<WebServiceClient.IOResponse> listener)
                        throws Exception {
        super(requestType.getValue(), url, (dataObject == null) ? null: new JsonCaster(dataObject).getJson(), listener, (Response.ErrorListener) listener);

        this.responseType = responseType;
        this.headers = headers;
        this.listener = listener;
        setRetryPolicy(new DefaultRetryPolicy(timeout, maxRetries, backOffMult));

        gson = getGsonInstance();
    }

   @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(WebServiceClient.IOResponse response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<WebServiceClient.IOResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            WebServiceClient.IOResponse responseObj = gson.fromJson(json,responseType);
            return Response.success(responseObj, HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
    public static Gson getGsonInstance() {
        if(gson == null) {
            //Type storyTitleFragmentType = new TypeToken<UserStory.StoryTitleFragment>() {}.getType();
            //arun: registration of deserializer for storyTitleType should happen in the client of
            // jsonbuilder i.e homeActivity.java.
            GsonBuilder gsonBuilder = new GsonBuilder()
                    //.registerTypeAdapter(UserStory.StoryTitleFragment.class, new StoryTitleDeserializer())
                    .setDateFormat("yyyy-MM-dd HH:mm:ss");
            gson = gsonBuilder.create();
        }
        return gson;
    }

    // We are trying to map enum RequestType to volley request types where GET - 0 and POST - 1
    public enum RequestType {
        GET(Method.GET),
        POST(Method.POST);

        private final int volleyType;
        RequestType(int volleyType){
            this.volleyType = volleyType;
        }

        public int getValue() {
            return volleyType;
        }
    }

    public static class JsonCaster {

        Object dataObject;
        String json;
        public JsonCaster(Object dataObject) throws Exception {
            this.dataObject = dataObject;
            if(dataObject instanceof JSONObject || dataObject instanceof String) {
                json = dataObject.toString();
            }
            else {
                json = getGsonInstance().toJson(dataObject);
            }
        }

        public String getJson() {
            return json;
        }
    }

}
