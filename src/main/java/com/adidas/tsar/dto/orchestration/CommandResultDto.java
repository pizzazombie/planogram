package com.adidas.tsar.dto.orchestration;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CommandResultDto {
    private final String service;
    private final String command;
}
