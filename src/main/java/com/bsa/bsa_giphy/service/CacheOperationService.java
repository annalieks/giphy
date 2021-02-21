package com.bsa.bsa_giphy.service;

import com.bsa.bsa_giphy.dto.Gif;
import com.bsa.bsa_giphy.exception.DataNotFoundException;
import com.bsa.bsa_giphy.exception.FileProcessingException;
import com.bsa.bsa_giphy.repository.GifsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
public class CacheOperationService {

    private final GifsRepository gifsRepository;

    @Value("${app.cache.dir}")
    private String cache;

    @Value("${app.users.dir}")
    private String users;

    @Autowired
    public CacheOperationService(GifsRepository gifsRepository) {
        this.gifsRepository = gifsRepository;
    }

    public void deleteUserCacheFromDisk(String userId) {
        Path dirPath = Path.of(users + userId + "/");
        if(!Files.exists(dirPath)) {
            throw new DataNotFoundException();
        }
        deleteDirectory(dirPath);
    }

    public void deleteCacheFromMemory(String userId) {
        gifsRepository.deleteByUser(userId);
    }

    public void deleteCacheFromMemoryByQuery(String userId, String query) {
        gifsRepository.deleteByQuery(userId, query);
    }

    public void deleteAllCacheFromDisk() throws FileProcessingException {
        final Path cachePath = Path.of(cache);

        if(Files.exists(cachePath)) {
            deleteDirectory(cachePath);
        }

        if(!Files.exists(cachePath)) {
            createDirectoriesIfNotExist(cachePath);
        }
    }

    private void deleteDirectory(Path dirPath) {
        try {

            if(Files.exists(dirPath)) {
                Files.walk(dirPath)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }

        } catch (IOException e) {
            throw new FileProcessingException("Could not delete folder");
        }
    }

    public List<Gif> getCacheFromDisk(String query) {
        if(query == null) {
            return listAll();
        }

        File dir = new File(cache + query);
        return listFilesInDirectory(dir);
    }

    public List<Gif> listAll() {
        List<Gif> result = new ArrayList<>();
        File cacheDir = new File(cache);

        File[] dirs = cacheDir.listFiles();
        if(dirs == null || dirs.length == 0) {
            throw new DataNotFoundException();
        }

        for(File dir : dirs) {
            result.addAll(listFilesInDirectory(dir));
        }

        return result;
    }

    public List<Path> listAllFilesPaths() {
        List<Path> result = new ArrayList<>();
        File cacheDir = new File(cache);

        File[] dirs = cacheDir.listFiles();
        if(dirs == null || dirs.length == 0) {
            throw new DataNotFoundException();
        }

        for(File dir : dirs) {
            var files = dir.listFiles();
            if(files == null || files.length == 0) {
                throw new DataNotFoundException();
            }
            for(File file : files) {
                result.add(file.toPath());
            }
        }

        return result;
    }

    public List<Gif> listFilesInDirectory(File dir) {
        List<Gif> result = new ArrayList<>();
        File[] files = dir.listFiles();
        if(files == null) {
            throw new DataNotFoundException();
        }

        String query = dir.getName();
        Gif gif = new Gif(query);

        for(File file : files) {
            gif.getGifs().add(file.toPath());
        }

        result.add(gif);
        return result;
    }

    public Path writeToUsersDir(String userId, String query,
                                Path cacheFilePath, String fileName) {
        try {

            Path usersDirPath = Path.of(users + userId + "/" + query);
            createDirectoriesIfNotExist(
                    Path.of(users),
                    Path.of(users + userId),
                    usersDirPath
            );

            if (!Files.exists(Path.of(usersDirPath + "/" + fileName))) {
                Files.copy(cacheFilePath, Path.of(usersDirPath + "/" + fileName));
            }

            return  Path.of(usersDirPath + "/" + fileName);

        } catch (IOException e) {
            throw new FileProcessingException("Could not update users cache");
        }

    }

    public void writeHistoryToCsv(String userId, String query, Path filePath) {
        try {

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

            Date date = new Date(System.currentTimeMillis());
            String currDate = formatter.format(date);

            createDirectoriesIfNotExist(
                    Path.of(users),
                    Path.of(users + userId)
            );

            File csvOutputFile = new File(users + userId + "/" + "history.csv");
            FileWriter fw = new FileWriter(csvOutputFile, true);
            fw.write(currDate + "," + query + ","
                    + filePath.toAbsolutePath() + "\n");
            fw.close();

        } catch (IOException e) {
            throw new FileProcessingException("Could not add record to users history");
        }
    }

    public void createDirectoriesIfNotExist(Path ... dirPaths) {
        try {

            for(Path dirPath : dirPaths) {
                if (!Files.exists(dirPath)) {
                    Files.createDirectory(dirPath);
                }
            }

        } catch (IOException e) {
            throw new FileProcessingException("Unable to create a directory");
        }
    }

}
