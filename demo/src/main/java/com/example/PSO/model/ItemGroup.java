package com.example.PSO.model;

import java.util.List;

public class ItemGroup {
    private final int id;
    private final String groupName;
    private final Group superGroup;
    private int itemCount;
    private final List<Integer> items;

    public ItemGroup(final int id, final String groupName, final List<Integer> items, final Group superGroup) {
        this.id = id;
        this.groupName = groupName;
        this.superGroup = superGroup;
        this.itemCount = items.size();
        this.items = items;
    }

    public void addItem(final Integer item) {
        items.add(item);
        itemCount++;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getItemCount() {
        return itemCount;
    }

    public List<Integer> getItems() {
        return items;
    }

    public Group getSuperGroup() {
        return superGroup;
    }
}
