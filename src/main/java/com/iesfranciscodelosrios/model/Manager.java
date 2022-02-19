package com.iesfranciscodelosrios.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.io.Serial;
import java.io.Serializable;

@Entity
@DiscriminatorValue("1")
public class Manager extends User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public Manager(){
        super();
    }

}
