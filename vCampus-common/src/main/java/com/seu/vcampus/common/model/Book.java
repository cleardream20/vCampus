package com.seu.vcampus.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Book implements Serializable {
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private int publishYear;
    private int totalCopies;
    private int availableCopies;
    private String location;
    private String imagePath;

    public boolean isAvailable() {
        return availableCopies > 0;
    }
}