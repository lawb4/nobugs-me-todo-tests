package com.todo.requests;

import com.todo.models.Todo;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;

import java.util.List;

public class ValidatedTodoRequest extends Request implements CrudInterface<Todo>, SearchInterface<Todo> {
    private TodoRequest todoRequest;

    public ValidatedTodoRequest(RequestSpecification reqSpec) {
        super(reqSpec);
        todoRequest = new TodoRequest(reqSpec);
    }

    @Override
    public String create(Todo todo) {
        return todoRequest.create(todo)
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .asString();
    }

    @Override
    public String update(long id, Todo todo) {
        return todoRequest.update(id, todo)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString();
    }

    @Override
    public ValidatableResponse delete(long id) {
        return todoRequest.delete(id)
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Override
    public List<Todo> readAll() {
        Todo[] todos = todoRequest.readAll()
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(Todo[].class);
        return List.of(todos);
    }

    @Override
    public List<Todo> readAll(Integer offset, Integer limit) {
        Todo[] todos = todoRequest.readAll(offset, limit)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(Todo[].class);
        return List.of(todos);
    }

    @Override
    public List<Todo> readAll(Integer limit) {
        Todo[] todos = todoRequest.readAll(limit)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(Todo[].class);
        return List.of(todos);
    }
}