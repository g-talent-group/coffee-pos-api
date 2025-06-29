package com.coffee.pos;

import com.coffee.pos.model.Employee;
import com.coffee.pos.service.OrderService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class main {
    public static void main(String[] args) {
        Employee e = Employee.builder().name("Morris").build();
        log.info(e.toString());
    }
}
