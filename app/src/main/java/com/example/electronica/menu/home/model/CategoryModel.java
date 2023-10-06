package com.example.electronica.menu.home.model;

public class CategoryModel {
    String catName;
    int catImage;

    public CategoryModel(String catName, int catImage) {
        this.catName = catName;
        this.catImage = catImage;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public int getCatImage() {
        return catImage;
    }

    public void setCatImage(int catImage) {
        this.catImage = catImage;
    }
}
