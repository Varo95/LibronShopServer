package com.iesfranciscodelosrios.model;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Book implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "title")
    private String title;
    //Imagen guardada en Base64
    @Column(name = "frontPage", columnDefinition = "LONGTEXT")
    private String frontPage;
    @Column(name = "price")
    private Double price;
    @Column(name = "releasedDate")
    private LocalDateTime releasedDate;
    @Column(name = "pDate")
    private LocalDateTime pDate;
    @Column(name = "stock")
    private boolean stock;
    @ManyToOne
    private Client buyer;

    public Book() {
        this.id = -1L;
    }

    public Book(Long id, String title, String frontPage, Double price, LocalDateTime releasedDate, LocalDateTime pDate, boolean stock, Client buyer) {
        this.id = id;
        this.title = title;
        this.frontPage = frontPage;
        this.price = price;
        this.releasedDate = releasedDate;
        this.pDate = pDate;
        this.stock = stock;
        this.buyer = buyer;
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

    public LocalDateTime getpDate() {
        return pDate;
    }

    public void setpDate(LocalDateTime pDate) {
        this.pDate = pDate;
    }

    public boolean isStock() {
        return stock;
    }

    public void setStock(boolean stock) {
        this.stock = stock;
    }

    public Client getBuyer() {
        return buyer;
    }

    public void setBuyer(Client buyer) {
        this.buyer = buyer;
    }
}
