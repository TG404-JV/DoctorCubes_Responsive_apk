package com.tvm.doctorcube.study.fragment.models;

public class NoteItem {
    private String title, category, author, pageCount, fileSize, timestamp, description;
    private String url;

    public NoteItem(String title, String url, String category, String author, String pageCount, String fileSize, String timestamp, String description) {
        this.title = title;
        this.url = url;
        this.category = category;
        this.author = author;
        this.pageCount = pageCount;
        this.fileSize = fileSize;
        this.timestamp = timestamp;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getCategory() {
        return category;
    }

    public String getAuthor() {
        return author;
    }

    public String getPageCount() {
        return pageCount;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }
}