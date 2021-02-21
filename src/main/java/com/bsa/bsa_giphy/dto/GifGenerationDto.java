package com.bsa.bsa_giphy.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Getter
public class GifGenerationDto {
    private final String query;
    private final Boolean force;
}
