package com.coffee.pos.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.sql.Timestamp;
import java.util.Date;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class Model {

  @Column(name = "create_at")
  private Timestamp createAt;

  @Column(name = "update_at")
  private Timestamp updateAt;

  public static void initTime(Model model) {
    model.setCreateAt(new Timestamp(new Date().getTime()));
    model.setUpdateAt(new Timestamp(new Date().getTime()));
  }

  public static void updateTime(Model model) {
    model.setUpdateAt(new Timestamp(new Date().getTime()));
  }
}
