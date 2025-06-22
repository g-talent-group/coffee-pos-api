package com.coffee.pos.service;

import com.coffee.pos.model.Order;
import com.coffee.pos.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Create, update, delete, get by ID, get all
    public Order createOrder(Order order) {
        order.setCreateAt(new Timestamp(new Date().getTime()));
        order.setUpdateAt(new Timestamp(new Date().getTime()));
        return orderRepository.save(order);
    }
}
