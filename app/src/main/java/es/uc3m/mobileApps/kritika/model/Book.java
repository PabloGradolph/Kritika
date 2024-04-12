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

    public Book(String title, List<String> authors, String publisher, String publishedDate,
                String description, String thumbnail) {
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.thumbnail = thumbnail;
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

    // Getters y Setters
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

    // Getter y Setter para publisher
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    // Getter y Setter para publishedDate
    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    // Getter y Setter para description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter y Setter para ISBN-10
    public String getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    // Getter y Setter para ISBN-13
    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    // Getter y Setter para pageCount
    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    // Getter y Setter para printType
    public String getPrintType() {
        return printType;
    }

    public void setPrintType(String printType) {
        this.printType = printType;
    }

    // Getter y Setter para categories
    public List<String> getCategories() {
        return categories;
    }
    public void setCategories(Object categories) {
        if (categories instanceof String) {
            // If the authors field is just a String, convert it to a List with one element
            this.categories = Collections.singletonList((String) categories);
        } else if (categories instanceof List) {
            // Cast safely to List<String>
            this.categories = (List<String>) categories;
        }
    }

    // Getter y Setter para averageRating
    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    // Getter y Setter para ratingsCount
    public int getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(int ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    // Getter y Setter para language
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    // Getter y Setter para previewLink
    public String getPreviewLink() {
        return previewLink;
    }

    public void setPreviewLink(String previewLink) {
        this.previewLink = previewLink;
    }

    // Getter y Setter para infoLink
    public String getInfoLink() {
        return infoLink;
    }

    public void setInfoLink(String infoLink) {
        this.infoLink = infoLink;
    }



    // Método para actualizar los detalles
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

