package com.todo.requests;

public interface SearchInterface<T> {
    Object readAll();
    Object readAll(int offset, int limit);
}