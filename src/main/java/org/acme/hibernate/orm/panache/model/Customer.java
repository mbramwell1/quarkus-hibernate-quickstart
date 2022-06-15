package org.acme.hibernate.orm.panache.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Cacheable
public class Customer extends PanacheEntity {

    @Column
    public String name;
    @Column
    public String job;

    public Customer() {
    }

    public Customer(String name, String job) {
        this.name = name;
        this.job = job;
    }

}
