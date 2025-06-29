package com.coffee.pos.dto;

import com.coffee.pos.model.Order;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponseDTO {
  private int id;
  private Date orderDate;
  private int totalAmount;
  private MemberResponseDTO member;

  public static OrderResponseDTO toDTO(Order order, boolean includeMember) {
    MemberResponseDTO member =
        (order.getMember() != null && includeMember)
            ? MemberResponseDTO.toDTO(order.getMember(), false)
            : null;
    return OrderResponseDTO.builder()
        .id(order.getId())
        .orderDate(order.getOrderDate())
        .totalAmount(order.getTotalAmount())
        .member(member)
        .build();
  }
}
