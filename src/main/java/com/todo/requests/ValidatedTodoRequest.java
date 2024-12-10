package com.todo.requests;

import com.todo.models.Todo;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

public class ValidatedTodoRequest extends Request implements CrudInterface<Todo> {
    private TodoRequest todoRequest;

    public ValidatedTodoRequest(RequestSpecification reqSpec) {
        super(reqSpec);
        todoRequest = new TodoRequest(reqSpec);
    }

    @Override
    public Todo create(Todo todo) {
        return todoRequest.create(todo)
                .then()
                .statusCode(201)
                .extract()
                .as(Todo.class);
    }

    @Override
    public Todo update(long id, Todo todo) {
        return todoRequest.update(id, todo)
                .then()
                .statusCode(200)
                .extract()
                .as(Todo.class);
    }

    @Override
    public ValidatableResponse delete(long id) {
        return todoRequest.delete(id)
                .then()
                .statusCode(204);
    }
}