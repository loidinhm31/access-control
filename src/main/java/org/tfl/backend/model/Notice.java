package org.tfl.backend.model;

public class Notice {
    private int id;
    private String content;
    private String author;
    private String date;
    private String label;

    public Notice(int id, String content, String author, String date, String label) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.date = date;
        this.label = label;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
