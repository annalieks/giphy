package com.bsa.bsa_giphy.controller;

import com.bsa.bsa_giphy.dto.Gif;
import com.bsa.bsa_giphy.dto.GifGenerationDto;
import com.bsa.bsa_giphy.service.CacheOperationService;
import com.bsa.bsa_giphy.service.GifOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cache")
public final class ApiCacheController {

    private final GifOperationService gifOperationService;

    private final CacheOperationService cacheOperationService;

    @Autowired
    public ApiCacheController(CacheOperationService cacheOperationService,
                              GifOperationService gifOperationService) {
        this.cacheOperationService = cacheOperationService;
        this.gifOperationService = gifOperationService;
    }

    @GetMapping
    public List<Gif> getCacheFromDisk(@RequestParam(required = false) String query) {
        return cacheOperationService.getCacheFromDisk(query);
    }

    @PostMapping("/generate")
    public Gif generateGif(@RequestBody GifGenerationDto gifGenerationDto) {
        return gifOperationService.generateGif(gifGenerationDto.getQuery());
    }

    @DeleteMapping
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCacheFromDisk() {
        cacheOperationService.deleteAllCacheFromDisk();
    }

}
