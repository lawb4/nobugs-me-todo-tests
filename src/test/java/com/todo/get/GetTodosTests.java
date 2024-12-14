package com.todo.get;


import com.todo.BaseTest;
import com.todo.models.Todo;
import com.todo.requests.TodoRequest;
import com.todo.requests.ValidatedTodoRequest;
import com.todo.specs.RequestSpec;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.restassured.AllureRestAssured;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@Epic("TODO Management")
@Feature("Get Todos API")
public class GetTodosTests extends BaseTest {

    @BeforeEach
    public void setupEach() {
        deleteAllTodos();
    }

    @Test
    @Description("Получение пустого списка TODO, когда база данных пуста")
    public void testGetTodosWhenDatabaseIsEmpty() {
        ValidatedTodoRequest validatedTodoRequest = new ValidatedTodoRequest(RequestSpec.authSpec());
        List<Todo> todos = validatedTodoRequest.readAll();

        Assertions.assertTrue(todos.isEmpty());
    }

    @Test
    @Description("Получение списка TODO с существующими записями")
    public void testGetTodosWithExistingEntries() {
        ValidatedTodoRequest validatedTodoRequest = new ValidatedTodoRequest(RequestSpec.authSpec());

        // Предварительно создать несколько TODO
        Todo todo1 = new Todo(1, "Task 1", false);
        Todo todo2 = new Todo(2, "Task 2", true);

        validatedTodoRequest.create(todo1);
        validatedTodoRequest.create(todo2);

        List<Todo> todos = validatedTodoRequest.readAll();
        Assertions.assertEquals(2, todos.size());

        // Дополнительная проверка содержимого
        Assertions.assertEquals(1, todos.get(0).getId());
        Assertions.assertEquals("Task 1", todos.get(0).getText());
        Assertions.assertFalse(todos.get(0).isCompleted());

        Assertions.assertEquals(2, todos.get(1).getId());
        Assertions.assertEquals("Task 2", todos.get(1).getText());
        Assertions.assertTrue(todos.get(1).isCompleted());
    }

    @Test
    @Description("Использование параметров offset и limit для пагинации")
    public void testGetTodosWithOffsetAndLimit() {
        ValidatedTodoRequest validatedTodoRequest = new ValidatedTodoRequest(RequestSpec.authSpec());

        // Создаем 5 TODO
        for (int i = 1; i <= 5; i++) {
            validatedTodoRequest.create(new Todo(i, "Task " + i, i % 2 == 0));
        }

        List<Todo> todos = validatedTodoRequest.readAll(2, 2);
        Assertions.assertEquals(2, todos.size());

        // Проверяем, что получили задачи с id 3 и 4
        Assertions.assertEquals(3, todos.get(0).getId());
        Assertions.assertEquals("Task 3", todos.get(0).getText());

        Assertions.assertEquals(4, todos.get(1).getId());
        Assertions.assertEquals("Task 4", todos.get(1).getText());
    }

    @Test
    @DisplayName("Передача некорректных значений в offset и limit")
    public void testGetTodosWithInvalidOffsetAndLimit() {
        TodoRequest todoRequest = new TodoRequest(RequestSpec.authSpec());

        todoRequest.readAll(-1, 2)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .contentType("text/plain")
                .body(containsString("Invalid query string"));


        //Тест с нечисловым limit
        given()
                .filter(new AllureRestAssured())
                .queryParam("offset", 0)
                .queryParam("limit", "abc")
                .when()
                .get("/todos")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .contentType("text/plain")
                .body(containsString("Invalid query string"));

//        todoRequest.readAll(0, Integer.valueOf("abc"))
//                .then()
//                .statusCode(400)
//                .contentType("text/plain")
//                .body(containsString("Invalid query string"));

        todoRequest.readAll(null, 2)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .contentType("text/plain")
                .body(containsString("Invalid query string"));
    }

    @Test
    @DisplayName("Проверка ответа при превышении максимально допустимого значения limit")
    public void testGetTodosWithExcessiveLimit() {
        ValidatedTodoRequest validatedTodoRequest = new ValidatedTodoRequest(RequestSpec.authSpec());

        // Создаем 10 TODO
        for (int i = 1; i <= 10; i++) {
            validatedTodoRequest.create((new Todo(i, "Task " + i, i % 2 == 0)));
        }

        List<Todo> todos = validatedTodoRequest.readAll(1000);

        // Проверяем, что вернулось 10 задач
        Assertions.assertEquals(10, todos.size());
    }
}
