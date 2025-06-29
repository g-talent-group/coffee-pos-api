package com.coffee.pos.dto;

import com.coffee.pos.model.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDTO {
    private Long id;
    private String name;
    private String phone;
    private List<OrderResponseDTO> orders;

    public static MemberResponseDTO toDTO(Member member, boolean includeOrders) {
        MemberResponseDTO.MemberResponseDTOBuilder builder = MemberResponseDTO.builder()
                .id(member.getId())
                .phone(member.getPhone())
                .name(member.getName());

        if (includeOrders) {
            List<OrderResponseDTO> orders = member.getOrders().stream()
                    .map(order -> OrderResponseDTO.toDTO(order, false))
                    .toList();
            builder.orders(orders);
        }

        return builder.build();
    }
}
