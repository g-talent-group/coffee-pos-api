package com.coffee.pos.controller;

import com.coffee.pos.dto.OrderResponseDTO;
import com.coffee.pos.model.Order;
import com.coffee.pos.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/order")
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{id}")
    public Order createOrder(@RequestBody Order order, @PathVariable Long id) {
        return orderService.createOrder(order, id);
    }

    @GetMapping
    public List<OrderResponseDTO> getAllOrder(@RequestParam(defaultValue = "false") boolean includeMember) {
        return orderService.getAllOrder(includeMember);
    }

    @GetMapping("/{id}")
    public OrderResponseDTO getOrderById(@PathVariable int id, @RequestParam(defaultValue = "false") boolean includeMember) {
        return orderService.getOrderById(id, includeMember);
    }

    @GetMapping("/amount/{amount}")
    public List<Order> getOrderByTotalAmountGreaterThan(@PathVariable int amount) {
        return orderService.getOrderByTotalAmountGreaterThan(amount);
    }

}
