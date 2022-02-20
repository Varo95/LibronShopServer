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
    private Double balance;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<UserBook> pBooks;

    public Client(){
        super();
    }

    public Client(Double balance, List<UserBook> pBooks) {
        this.balance = balance;
        this.pBooks = pBooks;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public List<UserBook> getpBooks() {
        return pBooks;
    }

    public void setpBooks(List<UserBook> pBooks) {
        this.pBooks = pBooks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Client client = (Client) o;

        if (balance != null ? !balance.equals(client.balance) : client.balance != null) return false;
        return pBooks != null ? pBooks.equals(client.pBooks) : client.pBooks == null;
    }

    @Override
    public int hashCode() {
        int result = balance != null ? balance.hashCode() : 0;
        result = 31 * result + (pBooks != null ? pBooks.hashCode() : 0);
        return result;
    }
}
