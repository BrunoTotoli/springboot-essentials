package com.example.springbootessentials.client;

import com.example.springbootessentials.domain.Anime;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class SpringClient {
    public static void main(String[] args) {

        final String DEFAULT_URL = "http://localhost:8080/animes";

        ResponseEntity<Anime> entity = new RestTemplate().getForEntity("http://localhost:8080/animes/2", Anime.class);
        log.info(entity);

        Anime object = new RestTemplate().getForObject("http://localhost:8080/animes/{id}", Anime.class, 1);
        log.info(object);

        Anime[] objects = new RestTemplate().getForObject("http://localhost:8080/animes/all", Anime[].class);
        log.info(Arrays.toString(objects));

        ResponseEntity<List<Anime>> exchange = new RestTemplate().exchange("http://localhost:8080/animes/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        log.info(exchange.getBody());

        Anime anime = Anime.builder().name("AnimePost").build();
        Anime savedAnime = new RestTemplate().postForObject(DEFAULT_URL, anime, Anime.class);
        log.info("Saved anime forObject {}", savedAnime);

        Anime anime1 = Anime.builder().name("AnimePostForExchange").build();
        ResponseEntity<Anime> animeExchangePost = new RestTemplate().exchange(DEFAULT_URL,
                HttpMethod.POST,
                new HttpEntity<>(anime1, createJsonHeader()),
                Anime.class);
        log.info("Saved anime exchange {}", animeExchangePost);


        Anime animeToBeUpdated = animeExchangePost.getBody();
        animeToBeUpdated.setName("Name changed for put");

        ResponseEntity<Void> animeExchangePostUpdated = new RestTemplate().exchange(DEFAULT_URL,
                HttpMethod.PUT,
                new HttpEntity<>(animeToBeUpdated, createJsonHeader()),
                Void.class);

        log.info(animeExchangePostUpdated);


        ResponseEntity<Void> exchangeDelete = new RestTemplate().exchange(DEFAULT_URL + "/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                animeToBeUpdated.getId());

        log.info(exchangeDelete);

    }

    private static HttpHeaders createJsonHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
