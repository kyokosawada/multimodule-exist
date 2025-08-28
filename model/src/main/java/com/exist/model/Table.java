package com.exist.model;

import java.util.ArrayList;
import java.util.List;

public class Table {

    private List<List<String>> rows = new ArrayList<>();

    public void add(List<String> row) {
        rows.add(row);
    }

    public List<String> get(int index) {
        return rows.get(index);
    }

    public int size() {
        return rows.size();
    }
    
    public void clear() {
        rows.clear();
    }

}