package es.uc3m.mobileApps.kritika.model;

import java.util.Collections;
import java.util.List;

public class Book implements SearchInterface {
    private String id;
    private String title;
    private List<String> authors;
    private String publisher;
    private String publishedDate;
    private String description;
    private String isbn10;
    private String isbn13;
    private int pageCount;
    private String printType;
    private List<String> categories;
    private double averageRating;
    private int ratingsCount;
    private String language;
    private String thumbnail;
    private String previewLink;
    private String infoLink;

    public Book() {
    }

    public Book(String id, String title, List<String> authors, String publisher, String publishedDate,
                String description, String thumbnail, double averageRating) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.thumbnail = thumbnail;
        this.averageRating = averageRating;
    }

    public Book(String id, String title, List<String> authors, String publisher, String publishedDate,
                String description, String isbn10, String isbn13, int pageCount, String printType,
                List<String> categories, double averageRating, int ratingsCount, String language,
                String thumbnail, String previewLink, String infoLink) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.isbn10 = isbn10;
        this.isbn13 = isbn13;
        this.pageCount = pageCount;
        this.printType = printType;
        this.categories = categories;
        this.averageRating = averageRating;
        this.ratingsCount = ratingsCount;
        this.language = language;
        this.thumbnail = thumbnail;
        this.previewLink = previewLink;
        this.infoLink = infoLink;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String getImagePath() { // for search interface
        return this.thumbnail;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(Object authors) {
        if (authors instanceof String) {
            // If the authors field is just a String, convert it to a List with one element
            this.authors = Collections.singletonList((String) authors);
        } else if (authors instanceof List) {
            // Cast safely to List<String>
            this.authors = (List<String>) authors;
        }
    }

    // Getter and Setter for description
    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    // Method for updating details
    public void updateDetails(String title, List<String> authors, String publisher, String publishedDate,
                              String description, String thumbnail) {
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.thumbnail = thumbnail;
    }
}

