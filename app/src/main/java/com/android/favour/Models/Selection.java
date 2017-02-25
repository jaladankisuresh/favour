package com.android.favour.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Selection {

    public static class SelectedResult {

        int displayViewId;
        Selectable selectedItem;

        public SelectedResult(Selectable selectedItem, int displayViewId) {
            this.selectedItem = selectedItem;
            this.displayViewId = displayViewId;
        }

        public Selectable getSelectedItem() {
            return selectedItem;
        }

        public int getDisplayViewId() {
            return displayViewId;
        }
    }

    public enum SelectDisplayType {
        @SerializedName("1")
        SINGLE (1),
        @SerializedName("2")
        GROUP (2);

        private final int value;
        public int getValue() {
            return value;
        }
        SelectDisplayType(int value) {
            this.value = value;
        }
    }

    public interface Selectable{
        public void setSelected(boolean selected);
        public boolean isSelected();
        public int getId();
        public String getName();
        public String getImage();
    }

    public interface SelectableGroup {
        public List<? extends Selectable> getCollection();
        public String getTitle();
    }

    public static class SelectableItem
            implements Selection.Selectable, Serializable {
        @SerializedName("name")
        private String name;
        @SerializedName("id")
        private int id;
        @SerializedName("selected")
        private boolean isSelected = false;

        public SelectableItem(String name, int id){
            this.name = name;
            this.id = id;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
        public boolean isSelected() {
            return isSelected;
        }
        public String getName() { return name; }
        public int getId() { return id; }
        public String getImage() { return null; }
    }

    public static class SelectableGroupItem {
        private String title;
        private ArrayList<Selectable> collection;

        public SelectableGroupItem(String title) {
            this.title = title;
            collection = new ArrayList<Selectable>();
        }
        public boolean addSelectable(Selectable item) {
            return collection.add(item);
        }
        public boolean addSelectableList(List list) {
            return collection.addAll(list);
        }
        public String getTitle() { return title; }
        public List<Selectable> getCollection() { return collection; }
    }

    public interface SelectableListener {
        void onSelectableClicked(Selectable selectItem);
    }
}


