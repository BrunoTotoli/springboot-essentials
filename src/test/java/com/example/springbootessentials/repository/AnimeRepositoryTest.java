package com.example.springbootessentials.repository;

import com.example.springbootessentials.domain.Anime;
import com.example.springbootessentials.util.AnimeCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@DisplayName("Tests for Anime Repository")
class AnimeRepositoryTest {

    @Autowired
    private AnimeRepository animeRepository;

    @Test
    @DisplayName("Save persists anime when Successful")
    void save_PersistAnime_WhenSuccessful() {
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        Anime savedAnime = this.animeRepository.save(animeToBeSaved);
        Assertions.assertThat(savedAnime).isNotNull();
        Assertions.assertThat(savedAnime.getId()).isNotNull();
        Assertions.assertThat(savedAnime.getName()).isEqualTo(animeToBeSaved.getName());
    }

    @Test
    @DisplayName("Save update anime when Successful")
    void save_UpdateAnime_WhenSuccessful() {
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        Anime savedAnime = this.animeRepository.save(animeToBeSaved);
        savedAnime.setName("Update Anime");
        Anime updatedAnime = this.animeRepository.save(savedAnime);
        Assertions.assertThat(savedAnime).isNotNull();
        Assertions.assertThat(updatedAnime.getId()).isNotNull();
        Assertions.assertThat(updatedAnime.getName()).isEqualTo(savedAnime.getName());
    }

    @Test
    @DisplayName("Delete anime when Successful")
    void delete_RemovesAnime_WhenSuccessful() {
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        Anime savedAnime = this.animeRepository.save(animeToBeSaved);
        this.animeRepository.delete(savedAnime);

        Optional<Anime> animeOptional = this.animeRepository.findById(savedAnime.getId());

        Assertions.assertThat(animeOptional).isEmpty();
    }

    @Test
    @DisplayName("Find by anime return list of anime when Successful")
    void findByName_ReturnListOfAnime_WhenSuccessful() {
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        Anime savedAnime = this.animeRepository.save(animeToBeSaved);
        String name = savedAnime.getName();

        List<Anime> animes = this.animeRepository.findByName(name);


        Assertions.assertThat(animes).isNotEmpty().contains(savedAnime);
    }

    @Test
    @DisplayName("Find by name return empty list of anime when no anime is found")
    void findByName_ReturnEmptyList_WhenAnimeIsNotFound() {
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        Anime savedAnime = this.animeRepository.save(animeToBeSaved);
        String name = "meusovos";

        List<Anime> animes = this.animeRepository.findByName(name);


        Assertions.assertThat(animes).isEmpty();
    }

    @Test
    @DisplayName("Save throw ConstraintViolationException when name is empty")
    void save_ThrowsConstraintViolationException_WheNameIsEmpty() {
        Anime anime = new Anime();
//        Assertions.assertThatThrownBy(() -> this.animeRepository.save(anime))
//                .isInstanceOf(ConstraintViolationException.class);

        Assertions.assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> this.animeRepository.save(anime))
                .withMessageContaining("The name cannot be empty");
    }

}