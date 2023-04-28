package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import ru.yandex.practicum.filmorate.validation.NoWhitespace;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id;
    @Email(message = "Некорректный email")
    private String email;
    @NoWhitespace(message = "Логин не может содержать пробелы")
    @NotBlank(message = "Логин не может быть пустым")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата дня рождения не может быть в будущем")
    private LocalDate birthday;

    public void setEmptyNameAsLogin() {
        if (StringUtils.isBlank(getName())) {
            setName(getLogin());
        }
    }
}
