package com.exist.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TableTest {

    @Test
    void testEmptyTable() {
        Table t = new Table();
        assertEquals(0, t.size());
    }

    @Test
    void testAddRow() {
        Table t = new Table();
        t.add(Arrays.asList("foo", "bar"));
        assertEquals(1, t.size());
        assertEquals(Arrays.asList("foo", "bar"), t.get(0));
    }

    @Test
    void testMultipleRows() {
        Table t = new Table();
        t.add(Arrays.asList("row1c1", "row1c2"));
        t.add(Arrays.asList("row2c1", "row2c2"));
        assertEquals(2, t.size());
        assertEquals(Arrays.asList("row2c1", "row2c2"), t.get(1));
    }

    @Test
    void testClear() {
        Table t = new Table();
        t.add(Arrays.asList("foo", "bar"));
        t.clear();
        assertEquals(0, t.size());
    }

}
