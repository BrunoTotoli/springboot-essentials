package com.example.springbootessentials.repository;

import com.example.springbootessentials.domain.Anime;

import java.util.List;

public interface AnimeRepository {

    List<Anime> listAll();
}
