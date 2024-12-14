package com.todo.requests;

public interface SearchInterface<T> {
    Object readAll();
    Object readAll(Integer offset, Integer limit);
    Object readAll(Integer limit);
}