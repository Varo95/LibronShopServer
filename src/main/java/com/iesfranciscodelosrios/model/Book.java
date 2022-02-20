package com.iesfranciscodelosrios.model;

import com.iesfranciscodelosrios.model.nmrelation.UserBook;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "BOOK")
@NamedQueries(
        @NamedQuery(name = "getStockedBooks", query = "SELECT b from Book b WHERE b.stock=true")
)
@NaturalIdCache
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Book implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "title")
    @NaturalId
    private String title;
    @Column(name = "author")
    @NaturalId
    private String author;
    //Imagen guardada en Base64
    @Column(name = "frontPage", columnDefinition = "LONGTEXT")
    @NaturalId
    private String frontPage;
    @Column(name = "price")
    @NaturalId
    private Double price;
    @Column(name = "releasedDate")
    @NaturalId
    private LocalDateTime releasedDate;
    @Column(name = "stock")
    @NaturalId
    private boolean stock;
    @OneToMany(mappedBy = "book",cascade = CascadeType.ALL)
    private List<UserBook> buyers;

    public Book() {
        this.id = -1L;
    }

    public Book(Long id,String author, String title, String frontPage, Double price, LocalDateTime releasedDate, boolean stock) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.frontPage = frontPage;
        this.price = price;
        this.releasedDate = releasedDate;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFrontPage() {
        return frontPage;
    }

    public void setFrontPage(String frontPage) {
        this.frontPage = frontPage;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDateTime getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(LocalDateTime releasedDate) {
        this.releasedDate = releasedDate;
    }

    public boolean isStock() {
        return stock;
    }

    public void setStock(boolean stock) {
        this.stock = stock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        if (stock != book.stock) return false;
        if (!Objects.equals(id, book.id)) return false;
        if (!Objects.equals(title, book.title)) return false;
        if (!Objects.equals(author, book.author)) return false;
        if (!Objects.equals(frontPage, book.frontPage)) return false;
        if (!Objects.equals(price, book.price)) return false;
        return Objects.equals(releasedDate, book.releasedDate);
    }

}
