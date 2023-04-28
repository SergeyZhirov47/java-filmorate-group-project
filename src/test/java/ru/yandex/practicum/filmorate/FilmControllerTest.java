package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
public class FilmControllerTest extends BaseControllerTest {
    public FilmControllerTest() {
        endPoint = "/films";
    }

    // Post /films
    @Test
    void addValidFilmTest() throws Exception {
        final Film film = Film.builder()
                .name("film name")
                .description("description")
                .releaseDate(LocalDate.now().minusDays(1))
                .duration(60)
                .build();

        assertEquals(0, film.getId());

        final String json = asJsonString(film);

        final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post(endPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        final String responseJson = result.getResponse().getContentAsString();
        final Film responseFilm = objectMapper.readValue(responseJson, Film.class);

        assertNotEquals(0, responseFilm.getId());
        assertThat(film).usingRecursiveComparison().ignoringFields("id").isEqualTo(responseFilm);
    }

    // Put /users
    @Test
    void updateFilmTest() throws Exception {
        Film film = Film.builder()
                .name("name")
                .description("desc")
                .releaseDate(LocalDate.now().minusDays(1))
                .duration(60)
                .build();

        MvcResult result = addEntity(film);
        film = objectMapper.readValue(result.getResponse().getContentAsString(), Film.class);

        film.setName("new name");
        film.setDescription("new description");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.now().minusDays(60));

        final String json = asJsonString(film);
        result = this.mockMvc.perform(MockMvcRequestBuilders.put(endPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        final String responseJson = result.getResponse().getContentAsString();
        final Film responseFilm = objectMapper.readValue(responseJson, Film.class);

        assertEquals(film.getId(), responseFilm.getId());
        assertThat(film).usingRecursiveComparison().ignoringFields("id").isEqualTo(responseFilm);
    }


    // Get /users
    @Test
    void getAlFilmsThenEmpty() throws Exception {
        final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get(endPoint))
                .andExpect(status().isOk())
                .andReturn();

        final String responseJson = result.getResponse().getContentAsString();
        final List<Film> films = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertTrue(films.isEmpty());
    }

    @Test
    void getAllFilmsTest() throws Exception {
        final List<Film> films = new ArrayList<>();
        films.add(Film.builder().name("name 1").description("desc1").releaseDate(LocalDate.now().minusDays(1)).duration(60).build());
        films.add(Film.builder().name("name 2").description("desc2").releaseDate(LocalDate.now().minusDays(1)).duration(60).build());
        films.add(Film.builder().name("name 3").description("desc3").releaseDate(LocalDate.now().minusDays(1)).duration(60).build());

        for (Film film : films) {
            addEntity(film);
        }

        final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get(endPoint))
                .andExpect(status().isOk())
                .andReturn();

        final String responseJson = result.getResponse().getContentAsString();
        final List<Film> filmsFromResponse = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertEquals(films.size(), filmsFromResponse.size());
        assertThat(films).usingRecursiveComparison().ignoringFields("id").isEqualTo(filmsFromResponse);
    }
}
