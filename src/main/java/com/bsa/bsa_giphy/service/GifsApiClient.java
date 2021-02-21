package com.bsa.bsa_giphy.service;

import com.bsa.bsa_giphy.dto.GifWithImageDto;

public interface GifsApiClient {
    GifWithImageDto getGif(String query);
    byte[] getGifPicture(String url);
}
