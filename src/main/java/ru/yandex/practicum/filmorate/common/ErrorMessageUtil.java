package ru.yandex.practicum.filmorate.common;

import static ru.yandex.practicum.filmorate.common.ErrorMessage.*;

public class ErrorMessageUtil {
    public static String getNoUserWithIdMessage(int id) {
        return getNoEntityWithIdMessage(NO_USER.getMessage(), id);
    }

    public static String getNoFriendWithIdMessage(int id) {
        return getNoEntityWithIdMessage(NO_FRIEND.getMessage(), id);
    }

    public static String getNoFilmWithIdMessage(int id) {
        return getNoEntityWithIdMessage(NO_FILM.getMessage(), id);
    }

    public static String getNoEntityWithIdMessage(final String noEntity, int id) {
        return String.format(noEntity + " c id = %s.", id);
    }

    public static String getUserUpdateFailMessage(int id) {
        return joinWithDot(getNoUserWithIdMessage(id), UPDATE_FAIL.getMessage());
    }

    public static String getUserDeleteFailMessage(int id) {
        return joinWithDot(getNoUserWithIdMessage(id), DELETE_FAIL.getMessage());
    }

    public static String getFilmUpdateFailMessage(int id) {
        return joinWithDot(getNoFilmWithIdMessage(id), UPDATE_FAIL.getMessage());
    }

    public static String getFilmDeleteFailMessage(int id) {
        return joinWithDot(getNoFilmWithIdMessage(id), DELETE_FAIL.getMessage());
    }

    private static String joinWithDot(final String... str) {
        String finalStr = String.join(". ", str);
        return finalStr + ".";
    }
}
