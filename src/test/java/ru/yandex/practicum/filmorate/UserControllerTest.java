package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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


    // Get /users
    @Test
    void getAllUsersThenEmpty() throws Exception {
        final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get(endPoint))
                .andExpect(status().isOk())
                .andReturn();

        final String responseJson = result.getResponse().getContentAsString();
        final List<User> users = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertTrue(users.isEmpty());
    }

    @Test
    void getAllUsersTest() throws Exception {
        final List<User> users = new ArrayList<>();
        users.add(User.builder().login("user1").name("name 1").email("email1@test.com").birthday(LocalDate.now()).build());
        users.add(User.builder().login("user2").name("name 2").email("email1@test.com").birthday(LocalDate.now()).build());
        users.add(User.builder().login("user3").name("name 3").email("email1@test.com").birthday(LocalDate.now()).build());

        for (User user : users) {
            addEntity(user);
        }

        final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get(endPoint))
                .andExpect(status().isOk())
                .andReturn();

        final String responseJson = result.getResponse().getContentAsString();
        final List<User> usersFromResponse = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        assertEquals(users.size(), usersFromResponse.size());
        assertThat(users).usingRecursiveComparison().ignoringFields("id").isEqualTo(usersFromResponse);
    }
}
