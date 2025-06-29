package com.coffee.pos.repository;

import com.coffee.pos.model.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
  public List<Order> findByTotalAmountGreaterThan(int totalAmount);
}
