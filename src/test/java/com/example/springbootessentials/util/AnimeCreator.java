package com.example.springbootessentials.util;

import com.example.springbootessentials.domain.Anime;

public class AnimeCreator {


    public static Anime createAnimeToBeSaved() {
        return Anime.builder()
                .name("Create Anime")
                .build();
    }


    public static Anime createValidAnime() {
        return Anime.builder()
                .name("Create Anime")
                .id(1L)
                .build();
    }


    public static Anime createValidUpdateAnime() {
        return Anime.builder()
                .name("Update Anime")
                .id(1L)
                .build();
    }


}
