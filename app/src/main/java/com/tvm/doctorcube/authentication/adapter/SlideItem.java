package com.tvm.doctorcube.authentication.adapter;


public class SlideItem {
    private int image;
    private String caption;

    public SlideItem(int image, String caption) {
        this.image = image;
        this.caption = caption;
    }

    public int getImage() {
        return image;
    }

    public String getCaption() {
        return caption;
    }
}
