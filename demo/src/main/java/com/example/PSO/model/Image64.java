package com.example.PSO.model;

public class Image64 {
    String image;
    int index;
    ItemGroup itemGroup;

    public Image64() {
    }

    public Image64(String image, int index, ItemGroup itemGroup) {
        this.image = image;
        this.index = index;
        this.itemGroup = itemGroup;
    }

    public String getImage() {
        return image;
    }

    public int getIndex() {
        return index;
    }

    public ItemGroup getItemGroup() {
        return itemGroup;
    }
}
