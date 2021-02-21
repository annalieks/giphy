package com.bsa.bsa_giphy.service;

import com.bsa.bsa_giphy.dto.GifWithImageDto;
import com.bsa.bsa_giphy.exception.DataNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class HttpGifsApiClient implements GifsApiClient {

    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${api.gifs-url}")
    private String gifsApiUrl;

    @Value("${api.key}")
    private String apiKey;

    private final HttpClient client;

    @Autowired
    public HttpGifsApiClient(HttpClient client) {
        this.client = client;
    }

    @Override
    public GifWithImageDto getGif(String query) {
        try {

            var response = client.send(buildGetRequest(query), HttpResponse.BodyHandlers.ofString());
            JsonNode gifInfo = mapper.readTree(response.body());

            var gifId = gifInfo.path("data").path("id");
            var gifUrl = gifInfo.path("data").path("images").path("original").path("url");
            if(gifId.toString().isEmpty() || gifUrl.toString().isEmpty()) {
                throw new DataNotFoundException();
            }

            byte[] image = getGifPicture(gifUrl.asText());

            return new GifWithImageDto(gifId.asText(), image);

        } catch(IOException | InterruptedException e) {
            throw new DataNotFoundException();
        }
    }

    @Override
    public byte[] getGifPicture(String url) {
        try {

            var response = client.send(buildGetPictureRequest(url),
                    HttpResponse.BodyHandlers.ofByteArray());
            return response.body();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    private HttpRequest buildGetRequest(String query) {
        query = query.replaceAll(" ", "%20");
        return HttpRequest
                .newBuilder()
                .uri(URI.create(gifsApiUrl + "?tag=" + query + "&api_key=" + apiKey))
                .GET()
                .build();
    }

    private HttpRequest buildGetPictureRequest(String url) {
        return HttpRequest
                .newBuilder()
                .uri(URI.create(url))
                .setHeader("Accept", "image/gif")
                .GET()
                .build();
    }
}
