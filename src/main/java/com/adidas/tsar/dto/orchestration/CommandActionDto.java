package com.adidas.tsar.dto.orchestration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandActionDto<T> {
    private String service;
    private String command;
    private T payload;
}
