package com.coffee.pos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.sql.Timestamp;

@Data
@Entity
@Builder
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "order_date")
    private Date orderDate;

    @Column(name = "total_amount")
    private int totalAmount;

    @Column(name = "create_at")
    private Timestamp createAt;

    @Column(name = "update_at")
    private Timestamp updateAt;
}


