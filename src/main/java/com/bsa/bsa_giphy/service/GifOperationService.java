package com.bsa.bsa_giphy.service;

import com.bsa.bsa_giphy.dto.Gif;
import com.bsa.bsa_giphy.exception.FileProcessingException;
import com.bsa.bsa_giphy.exception.DataNotFoundException;
import com.bsa.bsa_giphy.repository.GifsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class GifOperationService {

    private final GifsApiClient gifsApiClient;

    private final GifsRepository gifsRepository;

    private final CacheOperationService cacheOperationService;

    @Value("${app.cache.dir}")
    private String cache;

    @Value("${app.users.dir}")
    private String users;

    @Autowired
    public GifOperationService(GifsApiClient gifsApiClient,
                               GifsRepository gifsRepository,
                               CacheOperationService cacheOperationService) {

        this.gifsApiClient = gifsApiClient;
        this.gifsRepository = gifsRepository;
        this.cacheOperationService = cacheOperationService;

    }

    public List<Gif> getAllUserGifs(String userId) {
        List<Gif> result = new ArrayList<>();

        Path userDirPath = Path.of(users + userId);
        if(!Files.exists(userDirPath)) {
            throw new DataNotFoundException();
        }

        File dir = new File(userDirPath.toString());
        File[] queryDirs = dir.listFiles();
        if(queryDirs == null)  {
            throw new DataNotFoundException();
        }

        for(File queryDir : queryDirs) {
            if(!queryDir.getName().equals("history.csv")) {
                result.addAll(cacheOperationService.listFilesInDirectory(queryDir));
            }
        }
        return result;
    }

    public Gif generateGif(String query) {
        downloadFromGiphy(query);
        Gif gif = new Gif(query);
        File cacheQueryDir = new File(cache + query);

        File[] files = cacheQueryDir.listFiles();
        if(files == null) {
            throw new FileProcessingException("Cannot get gif file");
        }

        gif.getGifs()
                .addAll(Arrays.stream(files)
                .map(File::toPath)
                .collect(Collectors.toList()));

        return gif;
    }

    public void downloadFromGiphy(String query) {
        try {

            var gif = gifsApiClient.getGif(query);
            Path dirPath = Path.of(cache + query);
            Path filePath = Path.of(dirPath + "/" + gif.getId() + ".gif");

            cacheOperationService.createDirectoriesIfNotExist(Path.of(cache), dirPath);

            if (!Files.exists(filePath)) {
                File file = new File(filePath.toString());
                OutputStream os = new FileOutputStream(file);
                os.write(gif.getGif());
                os.close();
            }

        } catch (IOException e) {
            throw new FileProcessingException(e.getMessage());
        }

    }

    public Path getFromCache(String userId, String query) {
        File dir = new File(cache + query + "/");
        File[] files = dir.listFiles();

        if (files == null || files.length == 0) {
            downloadFromGiphy(query);
            files = dir.listFiles();
        }
        if (files == null) {
            throw new FileProcessingException("Cannot get file from cache");
        }

        Random rand = new Random();
        File file = files[rand.nextInt(files.length)];
        String fileName = file.getName();

        Path usersFile = cacheOperationService.writeToUsersDir(userId, query, file.toPath(), fileName);

        gifsRepository.addGif(userId, query, usersFile);
        cacheOperationService.writeHistoryToCsv(userId, query, usersFile);
        return usersFile;
    }

    public Path findGifOnDisk(String userId, String query) {
        Path userDirPath = Path.of(users + userId),
                queryDirPath = Path.of(userDirPath + "/" + query);

        if(!Files.exists(userDirPath) || !Files.exists(queryDirPath)) {
            throw new DataNotFoundException();
        }

        File dir = new File(queryDirPath.toString());
        File[] files = dir.listFiles();
        if(files == null || files.length == 0) {
            throw new DataNotFoundException();
        }

        Random random = new Random();
        File file = files[random.nextInt(files.length)];
        gifsRepository.addGif(userId, query, file.toPath());
        return file.toPath();
    }

    public Path findGif(String userId, String query) {
        var gifPath = gifsRepository.findByQueryInUser(userId, query);
        return gifPath.orElseGet(() -> findGifOnDisk(userId, query));
    }

    public Path getFromGiphy(String userId, String query) {
        downloadFromGiphy(query);
        return getFromCache(userId, query);
    }

}
