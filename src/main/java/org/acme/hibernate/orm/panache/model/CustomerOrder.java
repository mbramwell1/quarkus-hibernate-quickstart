package org.acme.hibernate.orm.panache.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Cacheable
public class CustomerOrder extends PanacheEntity {

    @Column
    public String item;
    @Column
    public Long customerId;
    @Column
    public String customerName;

    public CustomerOrder() {
    }

    public CustomerOrder(String item, Long customerId) {
        this.customerId = customerId;
        this.item = item;
    }

}
