package com.iesfranciscodelosrios.model;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "Client")
@DiscriminatorValue("0")
public class Client extends User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Column(name = "balance")
    private Long balance;
    @OneToMany(mappedBy = "buyer")
    private List<Book> pBooks;

    public Client(){
        super();
    }

    public Client(Long balance, List<Book> pBooks) {
        this.balance = balance;
        this.pBooks = pBooks;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public List<Book> getpBooks() {
        return pBooks;
    }

    public void setpBooks(List<Book> pBooks) {
        this.pBooks = pBooks;
    }
}
