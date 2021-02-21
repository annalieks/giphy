package com.bsa.bsa_giphy.controller;

import com.bsa.bsa_giphy.dto.GifGenerationDto;
import com.bsa.bsa_giphy.dto.Gif;
import com.bsa.bsa_giphy.service.CacheOperationService;
import com.bsa.bsa_giphy.service.GifOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.List;

@RestController
public class ApiGifsController {

    private final CacheOperationService cacheOperationService;

    @Autowired
    public ApiGifsController(CacheOperationService cacheOperationService) {
        this.cacheOperationService = cacheOperationService;
    }

    @GetMapping("/gifs")
    public List<Path> getGifsList() {
        return cacheOperationService.listAllFilesPaths();
    }

}
