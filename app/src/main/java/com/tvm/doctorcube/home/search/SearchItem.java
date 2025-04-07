package com.tvm.doctorcube.home.search;

public  class SearchItem {
    private String title;
    private String type;
    private Object data;
    private int sectionPosition;

    public SearchItem(String title, String type, Object data, int sectionPosition) {
        this.title = title;
        this.type = type;
        this.data = data;
        this.sectionPosition = sectionPosition;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public int getSectionPosition() {
        return sectionPosition;
    }
}
