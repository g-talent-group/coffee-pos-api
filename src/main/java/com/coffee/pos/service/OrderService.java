package com.coffee.pos.service;

import com.coffee.pos.dto.OrderResponseDTO;
import com.coffee.pos.model.Employee;
import com.coffee.pos.model.Member;
import com.coffee.pos.model.Model;
import com.coffee.pos.model.Order;
import com.coffee.pos.repository.MemberRepository;
import com.coffee.pos.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, MemberRepository memberRepository) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
    }

    // Create, update, delete, get by ID, get all
    public Order createOrder(Order order, Long id) {
        // 檢查 member 是否存在
        Member member = memberRepository.findById(id).orElse(null);
        if (member == null) {
            return null;
        }
        Order.initTime(order);
        order.setMember(member);
        log.info("createOrder: {}", order);
        return orderRepository.save(order);
    }

    public List<OrderResponseDTO> getAllOrder(boolean includeMember) {
        return orderRepository.findAll().stream().map(order -> OrderResponseDTO.toDTO(order, includeMember)).toList();
    }

    public OrderResponseDTO getOrderById(int id, boolean includeMember) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found with id: " + 1d));
        return OrderResponseDTO.toDTO(order, includeMember);
    }

    public List<Order> getOrderByTotalAmountGreaterThan(int totalAmount) {
        return orderRepository.findByTotalAmountGreaterThan(totalAmount);
    }
}
