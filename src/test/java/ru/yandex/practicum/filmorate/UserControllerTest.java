package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest extends BaseControllerTest {

    public UserControllerTest() {
        endPoint = "/users";
    }

    // Post /users
    @Test
    void addValidUserTest() throws Exception {
        final User user = User.builder()
                .login("login")
                .name("user name")
                .email("mail@test.com")
                .birthday(LocalDate.now())
                .build();

        assertEquals(0, user.getId());

        final String json = asJsonString(user);

        final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post(endPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        final String responseJson = result.getResponse().getContentAsString();
        final User responseUser = objectMapper.readValue(responseJson, User.class);

        assertNotEquals(0, responseUser.getId());
        assertThat(user).usingRecursiveComparison().ignoringFields("id").isEqualTo(responseUser);
    }

    @Test
    void emptyNameShouldChangeOnLogin() throws Exception {
        final User user = User.builder()
                .login("login")
                .email("mail@test.com")
                .birthday(LocalDate.now())
                .build();

        assertNull(null, user.getName());

        final String json = asJsonString(user);

        final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post(endPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();


        final String responseJson = result.getResponse().getContentAsString();
        final User responseUser = objectMapper.readValue(responseJson, User.class);

        assertNotNull(responseUser.getName());
        assertEquals(responseUser.getName(), responseUser.getLogin());
    }

    // Put /users
    @Test
    void updateUserTest() throws Exception {
        User user = User.builder()
                .login("login")
                .email("mail@test.com")
                .name("userName")
                .birthday(LocalDate.now())
                .build();

        MvcResult result = addEntity(user);
        user = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);

        user.setLogin("newLogin");
        user.setName("new name");
        user.setEmail("newEnail@test.com");
        user.setBirthday(LocalDate.now().minusDays(1));

        final String json = asJsonString(user);
        result = this.mockMvc.perform(MockMvcRequestBuilders.put(endPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        final String responseJson = result.getResponse().getContentAsString();
        final User responseUser = objectMapper.readValue(responseJson, User.class);

        assertEquals(user.getId(), responseUser.getId());
        assertThat(user).usingRecursiveComparison().ignoringFields("id").isEqualTo(responseUser);
    }
}
