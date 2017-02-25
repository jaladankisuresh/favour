package com.android.favour.Adapters;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import com.android.favour.NetworkIO.ImageServiceClient;
import com.android.favour.R;
import com.android.favour.Models.Selection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MultiSelectOptionsArrayAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private List defaultCollection;
    private HashMap tokenObjects;
    private List collection;
    private ImageServiceClient proxyImageService;
    private Selection.SelectableListener listener;
    private int gridColumnsCount;

    public MultiSelectOptionsArrayAdapter(Selection.SelectableListener listener, List collection) {
        this(listener, collection, 1);
    }

    public MultiSelectOptionsArrayAdapter(Selection.SelectableListener listener, List collection, int columnsCount) {
        this.defaultCollection = collection;
        init(collection);

        this.listener = listener;
        gridColumnsCount = columnsCount;
        proxyImageService = new ImageServiceClient();
    }

    private void init(List<Selection.Selectable> defaultCollection) {
        collection = new ArrayList<>();
        collection.addAll(defaultCollection);

        tokenObjects = new HashMap();
    }

    public void updateItemToSelectedTokens(Selection.Selectable token){
        if(token.isSelected()) {
            tokenObjects.put(token.getId(), token);
        }
        else {
            tokenObjects.remove(token.getId());
        }
        //notifyItemChanged(defaultCollection.indexOf(token));
    }

    private void loadImageToView(ImageView view, Selection.Selectable selectItem){

        if(selectItem.isSelected()) {
            view.setImageResource(R.drawable.check);
        }
        else {
            if(selectItem.getImage() == null || selectItem.getImage().isEmpty()) {
                view.setImageResource(R.drawable.flag);
            }
            else {
                proxyImageService.getImage(selectItem.getImage(), view);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if( viewType == Selection.SelectDisplayType.SINGLE.getValue()) {
            View convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_itemselector, parent, false);
            return new SelectOptionsArrayAdapter.SingleViewHolder(convertView, viewType);
        }
        else { //if ( viewType == SelectDisplayType.GROUP.getValue()) {
            View convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_itemselector_group, parent, false);
            return new SelectOptionsArrayAdapter.GroupViewHolder(convertView, viewType);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        int viewType = getItemViewType(position);
        if( viewType == Selection.SelectDisplayType.SINGLE.getValue()) {

            final Selection.Selectable selectItem = (Selection.Selectable) collection.get(position);
            final SelectOptionsArrayAdapter.SingleViewHolder singleViewHolder = (SelectOptionsArrayAdapter.SingleViewHolder) viewHolder;

            boolean isSelected = tokenObjects.containsKey(selectItem.getId());
            singleViewHolder.select_profile_title.setText(selectItem.getName());
            selectItem.setSelected(isSelected);
            loadImageToView(singleViewHolder.img_profile_mini, selectItem);

            singleViewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selectItem.setSelected(!selectItem.isSelected()); //Onclick, toggle the selected flag
                    updateItemToSelectedTokens(selectItem);
                    listener.onSelectableClicked(selectItem);
                    loadImageToView(singleViewHolder.img_profile_mini, selectItem);
                }
            });

        }
        else { //if ( viewType == SelectDisplayType.GROUP.getValue()) {
            Selection.SelectableGroup selectGroup = (Selection.SelectableGroup) collection.get(position);
            SelectOptionsArrayAdapter.GroupViewHolder groupViewHolder = (SelectOptionsArrayAdapter.GroupViewHolder) viewHolder;

            groupViewHolder.select_group_title.setText(selectGroup.getTitle());
            RecyclerView recyclerView = groupViewHolder.lyt_select_group_collection;

            recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), gridColumnsCount));
            recyclerView.setAdapter(new MultiSelectableArrayAdapter(selectGroup.getCollection()));
        }

    }

    @Override
    public int getItemViewType(int position) {

        if(collection.get(position) instanceof Selection.Selectable) {
            return Selection.SelectDisplayType.SINGLE.getValue();
        }
        else if(collection.get(position) instanceof Selection.SelectableGroup) {
            return Selection.SelectDisplayType.GROUP.getValue();
        } else {
            return 0;
        }

    }

    @Override
    public int getItemCount() {
        return collection != null ? collection.size() : 0;
    }

    @Override
    public Filter getFilter() {
        return new SelectOptionsFilter();
    }


    private class SelectOptionsFilter extends Filter {

        private boolean isMatch(String text, Selection.Selectable selectItem) {
            //This can also be changed to contains filter, try using item.getName().contains(filterText)
            return selectItem.getName().toLowerCase().startsWith(text);
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (charSequence == null || charSequence.length() == 0) {
                return null;
            }
            String filterText = charSequence.toString().toLowerCase();
            List queryResults;
            FilterResults filterResults = new FilterResults();

            if(listener == null || !(listener instanceof SelectOptionsFilterListener)) {
                queryResults = new ArrayList();
                for(Object item: defaultCollection) {
                    if(item instanceof Selection.Selectable){
                        if(isMatch(filterText, (Selection.Selectable) item)){
                            queryResults.add(item);
                        }
                    }
                    else if(item instanceof Selection.SelectableGroup){
                        Selection.SelectableGroup selectGroup = (Selection.SelectableGroup) item;
                        List filteredCollection = new ArrayList();
                        for(Selection.Selectable selectItem : selectGroup.getCollection()){
                            if(isMatch(filterText, selectItem)){
                                filteredCollection.add(selectItem);
                            }
                        }
                        if(filteredCollection.size() > 0) {
                            Selection.SelectableGroupItem filteredGroup = new Selection.SelectableGroupItem(selectGroup.getTitle());
                            filteredGroup.addSelectableList(filteredCollection);
                            queryResults.add(filteredGroup);
                        }
                    }

                }
            }
            else { // if(listener != null && (listener instanceof SelectOptionsFilterListener)) {
                queryResults = ((SelectOptionsFilterListener) listener).autoComplete(filterText);
            }

            filterResults.values = queryResults;
            filterResults.count = queryResults.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            collection.clear();
            if (charSequence == null || charSequence.length() < 3) {
                collection.addAll(defaultCollection);
                notifyDataSetChanged();
                return;
            }

            List queryResultsList = (List) filterResults.values;
            if(queryResultsList != null){
                collection.addAll(queryResultsList);
                notifyDataSetChanged();
            }
        }

    }

    public class MultiSelectableArrayAdapter
            extends RecyclerView.Adapter<SelectOptionsArrayAdapter.SingleViewHolder> {

        private final List<? extends Selection.Selectable> childCollection;
        //private ImageServiceClient proxyImageService;

        public MultiSelectableArrayAdapter(List<? extends Selection.Selectable> collection) {
            this.childCollection = collection;
            //proxyImageService = new ImageServiceClient();
        }


        @Override
        public SelectOptionsArrayAdapter.SingleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_itemselector, parent, false);
            return new SelectOptionsArrayAdapter.SingleViewHolder(convertView, viewType);
        }

        @Override
        public void onBindViewHolder(final SelectOptionsArrayAdapter.SingleViewHolder singleViewHolder, final int position) {
            final Selection.Selectable selectItem = childCollection.get(position);
            boolean isSelected = tokenObjects.containsKey(selectItem.getId());
            singleViewHolder.select_profile_title.setText(selectItem.getName());
            selectItem.setSelected(isSelected);
            loadImageToView(singleViewHolder.img_profile_mini, selectItem);

            singleViewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selectItem.setSelected(!selectItem.isSelected()); //Onclick, toggle the selected flag
                    updateItemToSelectedTokens(selectItem);
                    listener.onSelectableClicked(selectItem);
                    loadImageToView(singleViewHolder.img_profile_mini, selectItem);
                }
            });
        }

        @Override
        public int getItemCount() {
            return childCollection.size();
        }
    }

    public interface SelectOptionsFilterListener {
        List autoComplete(CharSequence charSequence);
    }

}
