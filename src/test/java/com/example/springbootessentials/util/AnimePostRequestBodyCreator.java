package com.example.springbootessentials.util;

import com.example.springbootessentials.domain.Anime;
import com.example.springbootessentials.requests.AnimePostRequestBody;

public class AnimePostRequestBodyCreator {

    public static AnimePostRequestBody createAnimePostRequestBody() {
        return AnimePostRequestBody.builder()
                .name(AnimeCreator.createValidAnime().getName())
                .build();
    }
}
