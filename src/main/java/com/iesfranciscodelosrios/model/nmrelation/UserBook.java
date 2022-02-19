package com.iesfranciscodelosrios.model.nmrelation;

import com.iesfranciscodelosrios.model.Book;
import com.iesfranciscodelosrios.model.Client;
import com.iesfranciscodelosrios.model.User;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "USERBOOK")
@NamedQueries(
        @NamedQuery(name = "getClientPurchasedBooks", query = "SELECT b from UserBook b WHERE b.user=:user")
)
public class UserBook implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = User.class)
    private User user;
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Book.class)
    private Book book;
    @Column(name = "purchaseDate")
    private LocalDateTime purchaseDate;

    public UserBook() {
    }

    public UserBook(Long id, Client user, Book book, LocalDateTime purchaseDate) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.purchaseDate = purchaseDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

}
