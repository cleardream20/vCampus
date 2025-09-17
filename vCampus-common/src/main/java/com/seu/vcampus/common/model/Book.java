package com.seu.vcampus.common.model;

import com.seu.vcampus.common.util.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Book implements Serializable, Jsonable {
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private int publishYear;
    private int totalCopies;
    private int availableCopies;
    private String location;
    private String imagePath;
    private String description;

    public boolean isAvailable() {
        return availableCopies > 0;
    }
}