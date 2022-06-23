package com.example.springbootessentials.util;

import com.example.springbootessentials.requests.AnimePutRequestBody;

public class AnimePutRequestBodyCreator {

    public static AnimePutRequestBody createAnimePostRequestBody() {
        return AnimePutRequestBody.builder()
                .name(AnimeCreator.createValidUpdateAnime().getName())
                .id(AnimeCreator.createValidUpdateAnime().getId())
                .build();
    }
}
