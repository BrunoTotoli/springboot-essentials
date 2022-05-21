package com.example.springbootessentials.repository;

import com.example.springbootessentials.domain.Anime;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AnimeRepository extends JpaRepository<Anime,Long> {

}
