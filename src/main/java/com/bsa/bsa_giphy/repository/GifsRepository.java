package com.bsa.bsa_giphy.repository;

import com.bsa.bsa_giphy.dto.Gif;
import com.bsa.bsa_giphy.exception.DataNotFoundException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.*;

@Repository
@Getter
public class GifsRepository {

    private static final HashMap<String, List<Gif>> dict = new HashMap<>();

    ObjectNode objectNode;

    public void addGif(String userId, String query, Path filePath) {
        var gifsList = dict.get(userId);
        if(gifsList == null) {
            gifsList = new ArrayList<>();
            Gif gif = new Gif(query);

            gif.getGifs().add(filePath);
            gifsList.add(gif);

            dict.put(userId, gifsList);
        } else {
            var gifByQuery = findByQuery(gifsList, query);

            if(gifByQuery != null) {
                gifByQuery.getGifs().add(filePath);
            } else {
                Gif gif = new Gif(query);
                gif.getGifs().add(filePath);
                gifsList.add(gif);
            }
        }
    }

    private static Gif findByQuery(List<Gif> gifsList, String query) {
        return gifsList
                .stream()
                .filter(gif -> gif.getQuery().equals(query))
                .findFirst()
                .orElse(null);
    }

    public Optional<Path> findByQueryInUser(String userId, String query) {
        var gifsList = dict.get(userId);
        if(gifsList == null || gifsList.isEmpty()) {
            return Optional.empty();
        }

        var gifByQuery = findByQuery(gifsList, query);
        if(gifByQuery != null) {
            Random random = new Random();
            int length = gifByQuery.getGifs().size();
            return gifByQuery.getGifs().stream().skip(random.nextInt(length)).findFirst();
        }
        return Optional.empty();
    }

    public void deleteByUser(String userId) {
        dict.remove(userId);
    }

    public void deleteByQuery(String userId, String query) {
        var gifsList = dict.get(userId);
        if(gifsList == null) {
            throw new DataNotFoundException();
        }

        var gifByQuery = findByQuery(gifsList, query);
        if(gifByQuery != null) {
            gifsList.remove(gifByQuery);
        }
    }
}
