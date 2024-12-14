package com.todo.requests;

import com.todo.models.Todo;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class TodoRequest extends Request implements CrudInterface<Todo>, SearchInterface<Todo> {
    private static final String TODO_ENDPOINT = "/todos/";

    public TodoRequest(RequestSpecification reqSpec) {
        super(reqSpec);
    }

    @Override
    public Response create(Todo todo) {
        return given()
                .spec(reqSpec)
                .body(todo)
                .when()
                .post(TODO_ENDPOINT);
    }

    @Override
    public Response update(long id, Todo todo) {
        return given()
                .spec(reqSpec)
                .body(todo)
                .put(TODO_ENDPOINT + id);
    }

    @Override
    public Response delete(long id) {
        return given()
                .spec(reqSpec)
                .delete(TODO_ENDPOINT + id);
    }

    @Override
    public Response readAll() {
        return given()
                .when()
                .get(TODO_ENDPOINT);
    }

    @Override
    public Response readAll(Integer offset, Integer limit) {
        return given()
                .queryParam("offset", offset)
                .queryParam("limit", limit)
                .when()
                .get(TODO_ENDPOINT);
    }

    @Override
    public Response readAll(Integer limit) {
        return given()
                .queryParam("limit", limit)
                .when()
                .get(TODO_ENDPOINT);
    }
}
