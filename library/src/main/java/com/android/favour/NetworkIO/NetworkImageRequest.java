package com.android.favour.NetworkIO;

import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.favour.R;

public class NetworkImageRequest {

    private String relativeUri;
    private ImageView imgView;
    // loading Spinner, if provided, overrides loading Image property.
    // That is, If both loading spinner and loading image are specified, it is spinner that is visible
    private ProgressBar loadingSpinner = null;
    private Priority imgPriority = Priority.NORMAL;
    private int loadingImage = R.drawable.wait;
    private int errorImage = R.drawable.error;

    private NetworkImageRequest() {
    }

    public NetworkImageRequest(String relativeUri, ImageView imgContainer) {
        this.relativeUri = relativeUri;
        this.imgView = imgContainer;
    }

    public static NetworkImageRequest create(){
        return new NetworkImageRequest();
    }

    public ImageView getImgView() {
        return imgView;
    }

    public NetworkImageRequest imgView(ImageView imgContainer) {
        this.imgView = imgContainer;
        return this;
    }
    
    public com.bumptech.glide.Priority getImgPriority() {
        return imgPriority.getValue();
    }
    public NetworkImageRequest priority(Priority imgPriority) {
        this.imgPriority = imgPriority;
        return this;
    }
    public String getUri() {
        return relativeUri;
    }

    public NetworkImageRequest uri(String uri) {
        this.relativeUri = uri;
        return this;
    }
    // loading Spinner, if provided, overrides loading Image property.
    // That is, If both loading spinner and loading image are specified, it is spinner that is visible
    public ProgressBar getLoadingSpinner() {
        return loadingSpinner;
    }

    public NetworkImageRequest loadingSpinner(ProgressBar loadingSpinner) {
        this.loadingSpinner = loadingSpinner;
        return this;
    }
    public int getLoadingImage() {
        return loadingImage;
    }

    public NetworkImageRequest loadingImage(int loadingImage) {
        this.loadingImage = loadingImage;
        return this;
    }

    public int getErrorImage() {
        return errorImage;
    }

    public NetworkImageRequest errorImage(int errorImage) {
        this.errorImage = errorImage;
        return this;
    }

    //NOTE: We are trying to do a simple mapping between ImageRequest Priority enum to Glide com.bumptech.glide.Priority enum
    public enum Priority {
        LOW(com.bumptech.glide.Priority.LOW),
        NORMAL(com.bumptech.glide.Priority.NORMAL),
        HIGH(com.bumptech.glide.Priority.HIGH),
        URGENT(com.bumptech.glide.Priority.IMMEDIATE);

        private final com.bumptech.glide.Priority glidePriority;
        Priority(com.bumptech.glide.Priority glidePriority){
            this.glidePriority = glidePriority;
        }

        public com.bumptech.glide.Priority getValue() {
            return glidePriority;
        }
    }
}
