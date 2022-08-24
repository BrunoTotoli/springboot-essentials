package com.example.springbootessentials.integration;

import com.example.springbootessentials.domain.Anime;
import com.example.springbootessentials.domain.DevDojoUser;
import com.example.springbootessentials.repository.AnimeRepository;
import com.example.springbootessentials.repository.DevDojoUserRepository;
import com.example.springbootessentials.requests.AnimePostRequestBody;
import com.example.springbootessentials.util.AnimeCreator;
import com.example.springbootessentials.util.AnimePostRequestBodyCreator;
import com.example.springbootessentials.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AnimeControllerIT {
    @Autowired
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateUser;
    @Autowired
    @Qualifier(value = "testRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateAdmin;
    @Autowired
    private AnimeRepository animeRepository;
    @Autowired
    private DevDojoUserRepository devDojoUserRepository;

    private static final DevDojoUser USER = DevDojoUser.builder()
            .name("user")
            .password("{bcrypt}$2a$10$bCKFCwDEPsrIr9cKB6FyUeHagLZtAIvOZWMOsi4Gt2hZ66JYBul1u")
            .authorities("ROLE_USER")
            .build();

    private static final DevDojoUser ADMIN = DevDojoUser.builder()
            .name("admin")
            .password("{bcrypt}$2a$10$bCKFCwDEPsrIr9cKB6FyUeHagLZtAIvOZWMOsi4Gt2hZ66JYBul1u")
            .authorities("ROLE_ADMIN,ROLE_USER")
            .build();

    @TestConfiguration
    @Lazy
    static class Config {
        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("user", "senha");
            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("admin", "senha");
            return new TestRestTemplate(restTemplateBuilder);
        }
    }

    @Test
    @DisplayName("list Returns list of anime inside page object when successful")
    void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        devDojoUserRepository.save(USER);

        String expectedName = savedAnime.getName();

        PageableResponse<Anime> animePage = testRestTemplateUser.exchange("/animes", HttpMethod.GET, null, new ParameterizedTypeReference<PageableResponse<Anime>>() {
        }).getBody();

        Assertions.assertThat(animePage).isNotNull();
        Assertions.assertThat(animePage.toList()).isNotEmpty().hasSize(1);
        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }


    @Test
    @DisplayName("listAll Returns list of anime when successful")
    void list_ReturnsListOfAnimes_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        devDojoUserRepository.save(USER);
        String expectedName = savedAnime.getName();

        List<Anime> animes = testRestTemplateUser.exchange("/animes/all", HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {
        }).getBody();

        Assertions.assertThat(animes).isNotNull().isNotEmpty().hasSize(1);
        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById Returns anime when successful")
    void findById_ReturnsAnime_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        devDojoUserRepository.save(USER);
        Long expectedId = savedAnime.getId();

        Anime anime = testRestTemplateUser.getForObject("/animes/{id}", Anime.class, expectedId);

        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByName Returns list of anime when successful")
    void findByName_ReturnsListOfAnime_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        devDojoUserRepository.save(USER);
        String expectedName = savedAnime.getName();

        String url = String.format("/animes/find?name=%s", expectedName);

        List<Anime> animes = testRestTemplateUser.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {
        }).getBody();

        Assertions.assertThat(animes).isNotNull().isNotEmpty().hasSize(1);
        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("findByName Returns empty list of anime is not found")
    void findByName_ReturnsEmptyListOfAnime_WhenSuccessful() {
        devDojoUserRepository.save(USER);

        String url = String.format("/animes/find?name=%s", "empty");

        List<Anime> animes = testRestTemplateUser.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {
        }).getBody();


        Assertions.assertThat(animes).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("save Returns anime when successful")
    void save_ReturnsAnime_WhenSuccessful() {
        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();
        devDojoUserRepository.save(USER);

        ResponseEntity<Anime> animeResponseEntity = testRestTemplateUser.postForEntity("/animes", animePostRequestBody, Anime.class);


        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(Objects.requireNonNull(animeResponseEntity.getBody()).getId()).isNotNull();
    }

    @Test
    @DisplayName("replace update anime when successful")
    void replace_UpdatesAnime_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        devDojoUserRepository.save(USER);
        savedAnime.setName("new name");

        ResponseEntity<Void> animeResponseEntity = testRestTemplateUser.exchange("/animes", HttpMethod.PUT, new HttpEntity<>(savedAnime), Void.class);

        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }


    @Test
    @DisplayName("delete removes anime when successful")
    void delete_RemoveAnime_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        devDojoUserRepository.save(ADMIN);

        ResponseEntity<Void> animeResponseEntity = testRestTemplateAdmin.exchange("/animes/admin/{id}", HttpMethod.DELETE, new HttpEntity<>(savedAnime), Void.class, savedAnime.getId());

        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete returns 403 when user is not admin")
    void delete_Returns403_WhenUserIsNotAdmin() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        devDojoUserRepository.save(USER);

        ResponseEntity<Void> animeResponseEntity = testRestTemplateUser.exchange("/animes/admin/{id}", HttpMethod.DELETE, new HttpEntity<>(savedAnime), Void.class, savedAnime.getId());

        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }


}
