package com.iesfranciscodelosrios.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "Manager")
@DiscriminatorValue("1")
public class Manager extends User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Transient
    private List<Book> allBooks;
    //TODO tengo que hacer una consulta personalizada para traerme todos los putos libros y rellenar la lista

    public Manager(){
        super();
    }

    public Manager(List<Book> allBooks) {
        super();
        this.allBooks = allBooks;
    }

    public List<Book> getAllBooks() {
        return allBooks;
    }

    public void setAllBooks(List<Book> allBooks) {
        this.allBooks = allBooks;
    }
}
