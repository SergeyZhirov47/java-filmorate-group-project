package ru.yandex.practicum.filmorate.controllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
public class FilmControllerTest extends BaseControllerTest {
    @MockBean
    private FilmService filmService;

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

        final String json = asJsonString(film);

        this.mockMvc.perform(MockMvcRequestBuilders.post(endPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
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

        addEntity(film);

        film.setName("new name");
        film.setDescription("new description");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.now().minusDays(60));

        final String json = asJsonString(film);
        this.mockMvc.perform(MockMvcRequestBuilders.put(endPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }
}
