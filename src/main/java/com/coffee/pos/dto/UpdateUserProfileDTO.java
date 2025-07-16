package com.coffee.pos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileDTO {
    @Email(message = "郵箱格式不正確")
    private String email;

    @Size(max = 100, message = "姓名長度不能超過100字符")
    private String fullName;
}