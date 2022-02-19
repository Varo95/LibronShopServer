package com.iesfranciscodelosrios.model;

import com.iesfranciscodelosrios.model.nmrelation.UserBook;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@DiscriminatorValue("0")
public class Client extends User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Column(name = "balance")
    private Long balance;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserBook> pBooks;

    public Client(){
        super();
    }

    public Client(Long balance, List<UserBook> pBooks) {
        this.balance = balance;
        this.pBooks = pBooks;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public List<UserBook> getpBooks() {
        return pBooks;
    }

    public void setpBooks(List<UserBook> pBooks) {
        this.pBooks = pBooks;
    }

}
