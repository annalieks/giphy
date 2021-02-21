package com.bsa.bsa_giphy.dto;

import lombok.Data;

import java.nio.file.Path;
import java.util.*;

@Data
public class Gif {
    private final String query;
    private final Set<Path> gifs = new TreeSet<>();
}
