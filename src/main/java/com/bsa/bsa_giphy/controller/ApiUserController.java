package com.bsa.bsa_giphy.controller;

import com.bsa.bsa_giphy.dto.GifGenerationDto;
import com.bsa.bsa_giphy.dto.UserIdDto;
import com.bsa.bsa_giphy.dto.History;
import com.bsa.bsa_giphy.dto.Gif;
import com.bsa.bsa_giphy.service.CacheOperationService;
import com.bsa.bsa_giphy.service.GifOperationService;
import com.bsa.bsa_giphy.service.HistoryOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.file.Path;
import java.util.List;

@Validated
@RestController
@RequestMapping("/user")
public class ApiUserController {

    private final GifOperationService gifOperationService;

    private final HistoryOperationService historyOperationService;

    private final CacheOperationService cacheOperationService;

    @Autowired
    public ApiUserController(GifOperationService gifOperationService,
                             HistoryOperationService historyOperationService,
                             CacheOperationService cacheOperationService) {
        this.gifOperationService = gifOperationService;
        this.historyOperationService = historyOperationService;
        this.cacheOperationService = cacheOperationService;
    }

    @GetMapping("/{userId}/all")
    List<Gif> getAllUserGifs(@PathVariable @Valid UserIdDto userId) {
        return gifOperationService.getAllUserGifs(userId.getUserId());
    }

    @GetMapping("/{userId}/history")
    List<History> getUserHistory(@PathVariable @Valid UserIdDto userId) {
        return historyOperationService.getUserHistory(userId.getUserId());
    }

    @GetMapping("/{userId}/search")
    public Path findGif(@PathVariable @Valid UserIdDto userId,
                        @RequestParam String query,
                        @RequestParam(required = false) Boolean force) {
        return (force != null && force)
                ? gifOperationService.findGifOnDisk(userId.getUserId(), query)
                : gifOperationService.findGif(userId.getUserId(), query);
    }


    @PostMapping("/{userId}/generate")
    Path generateGif(@PathVariable @Valid UserIdDto userId,
                     @RequestBody GifGenerationDto gifRequestDto) {
        return gifRequestDto.getForce()
                ? gifOperationService.getFromGiphy(userId.getUserId(), gifRequestDto.getQuery())
                : gifOperationService.getFromCache(userId.getUserId(), gifRequestDto.getQuery());
    }

    @DeleteMapping("/{userId}/history/clean")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteUserHistory(@PathVariable @Valid UserIdDto userId) {
        historyOperationService.deleteUserHistory(userId.getUserId());
    }

    @DeleteMapping("/{userId}/reset")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteUserCacheInDictionary(@PathVariable @Valid UserIdDto userId,
                                            @RequestParam(required = false) String query) {
        if(query == null) {
            cacheOperationService.deleteCacheFromMemory(userId.getUserId());
        } else {
            cacheOperationService.deleteCacheFromMemoryByQuery(userId.getUserId(), query);
        }
    }

    @DeleteMapping("/{userId}/clean")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteUserCache(@PathVariable @Valid UserIdDto userId) {
        cacheOperationService.deleteUserCacheFromDisk(userId.getUserId());
        cacheOperationService.deleteCacheFromMemory(userId.getUserId());
    }

}
