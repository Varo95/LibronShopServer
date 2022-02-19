package com.iesfranciscodelosrios.model;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER, name = "isManager", columnDefinition = "TINYINT(1)")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@Table(name = "_USER")
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Long id;
    @Column(name = "email", unique = true)
    protected String email;
    @Column(name = "password")
    protected String password;
    @Transient
    protected boolean isManager;

    public User(){
        this.id = -1L;
        this.email = "no_mail";
        this.password = "no_password";
        this.isManager = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    @Transient
    public boolean isManager() {
        return isManager;
    }
    @Transient
    public void setManager(boolean manager) {
        isManager = manager;
    }
}
