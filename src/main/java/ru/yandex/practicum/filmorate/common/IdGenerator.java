package ru.yandex.practicum.filmorate.common;

public class IdGenerator {
    private int lastId;

    public IdGenerator() {
        lastId = 0;
    }

    public IdGenerator(int startId) {
        this.lastId = startId;
    }

    public int getNext() {
        lastId++;
        return lastId;
    }

    public int getCurrent() {
        return lastId;
    }
}
