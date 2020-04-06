package com.example.arcseekbar.weight.productinterface;

public abstract class Product {
    private String typeName;
    private int level;
    private String profit;//收益
    private int bitmapResId;

    public Product(String typeName, int level,String profit,int bitmapResId) {
        this.typeName = typeName ;
        this.level = level;
        this.profit = profit;
        this.bitmapResId = bitmapResId;
    }


    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public int getBitmapResId() {
        return bitmapResId;
    }

    public void setBitmapResId(int bitmapResId) {
        this.bitmapResId = bitmapResId;
    }



}
