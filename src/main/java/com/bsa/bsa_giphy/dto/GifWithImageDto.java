package com.bsa.bsa_giphy.dto;

import lombok.Data;

@Data
public class GifWithImageDto {
    private final String id;
    private final byte[] gif;
}
