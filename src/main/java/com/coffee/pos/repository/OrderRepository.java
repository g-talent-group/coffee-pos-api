package com.coffee.pos.repository;

import com.coffee.pos.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    public List<Order> findByTotalAmountGreaterThan(int totalAmount);
}
