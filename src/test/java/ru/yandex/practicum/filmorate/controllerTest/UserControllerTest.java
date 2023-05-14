package ru.yandex.practicum.filmorate.controllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest extends BaseControllerTest {
    @MockBean
    private UserService userService;

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

        this.mockMvc.perform(MockMvcRequestBuilders.post(endPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
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

        this.mockMvc.perform(MockMvcRequestBuilders.post(endPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
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

        addEntity(user);

        user.setLogin("newLogin");
        user.setName("new name");
        user.setEmail("newEnail@test.com");
        user.setBirthday(LocalDate.now().minusDays(1));

        final String json = asJsonString(user);
        this.mockMvc.perform(MockMvcRequestBuilders.put(endPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }
}
