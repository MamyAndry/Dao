package com.dao.database;

public class Pageable {
    String order;
    int length;
    int start;
    
    public Pageable(){}

    public Pageable(String order, int length, int start) {
        this.setOrder(order);
        this.setLength(length);
        this.setStart(start);
    }

    public String getOrder() {
        return order;
    }
    public void setOrder(String order) {
        this.order = order;
    }
    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }
    public int getStart() {
        return start;
    }
    public void setStart(int start) {
        this.start = start;
    }
}
