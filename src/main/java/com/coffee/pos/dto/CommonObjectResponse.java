package com.coffee.pos.dto;

import com.coffee.pos.enums.CommonStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonObjectResponse<T> {
  String msg;
  CommonStatus status;
  T data;

  public static <T> CommonObjectResponse<T> success(T data, String message) {
    return new CommonObjectResponse<>(message, CommonStatus.SUCCESS, data);
  }

  public static <T> CommonObjectResponse<T> error(String message) {
    return new CommonObjectResponse<>(message, CommonStatus.FAILED, null);
  }
}
