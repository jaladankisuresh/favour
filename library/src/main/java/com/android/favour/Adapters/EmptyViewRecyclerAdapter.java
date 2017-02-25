package com.android.favour.Adapters;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class EmptyViewRecyclerAdapter extends
        RecyclerView.Adapter<EmptyViewRecyclerAdapter.NoViewHolder> {

    private static EmptyViewRecyclerAdapter instance;

    private EmptyViewRecyclerAdapter() {
    }

    public static EmptyViewRecyclerAdapter getAdapter(){
        if(instance == null) {
            instance = new EmptyViewRecyclerAdapter();
        }
        return instance;
    }

    @Override
    public NoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(NoViewHolder holder, int position) {
        return;
    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public class NoViewHolder extends RecyclerView.ViewHolder {
        public NoViewHolder(View itemView) {
            super(itemView);
        }
    }
}

