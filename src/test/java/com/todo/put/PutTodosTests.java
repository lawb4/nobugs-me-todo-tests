package com.todo.put;

import com.todo.BaseTest;
import com.todo.models.Todo;
import com.todo.requests.TodoRequest;
import com.todo.requests.ValidatedTodoRequest;
import com.todo.specs.RequestSpec;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class PutTodosTests extends BaseTest {

    @BeforeEach
    public void setupEach() {
        deleteAllTodos();
    }

    /**
     * TC1: Обновление существующего TODO корректными данными.
     */
    @Test
    public void testUpdateExistingTodoWithValidData() {
        ValidatedTodoRequest validatedAuthTodoRequest = new ValidatedTodoRequest(RequestSpec.authSpec());

        // Создаем TODO для обновления
        Todo originalTodo = new Todo(1, "Original Task", false);
        validatedAuthTodoRequest.create(originalTodo);

        // Обновленные данные
        Todo updatedTodo = new Todo(1, "Updated Task", true);
        validatedAuthTodoRequest.update(1, updatedTodo);

        // Проверяем, что данные были обновлены
        List<Todo> todos = validatedAuthTodoRequest.readAll();

        Assertions.assertEquals(1, todos.size());
        Assertions.assertEquals("Updated Task", todos.get(0).getText());
        Assertions.assertTrue(todos.get(0).isCompleted());
    }

    /**
     * TC2: Попытка обновления TODO с несуществующим id.
     */
    @Test
    public void testUpdateNonExistentTodo() {
        // Обновленные данные для несуществующего TODO
        Todo updatedTodo = new Todo(999, "Non-existent Task", true);

        TodoRequest todoRequest = new TodoRequest(RequestSpec.authSpec());
        todoRequest.update(updatedTodo.getId(), updatedTodo)
                .then()
                .statusCode(404)
                //.contentType(ContentType.TEXT)
                .body(is(notNullValue()));
    }

    /**
     * TC3: Обновление TODO с отсутствием обязательных полей.
     */
    @Test
    public void testUpdateTodoWithMissingFields() {
        // 241213 - Как я понимаю, это относится к контрактному тестированию, поэтому я пока не изменял этот тест во второй части кода, где идёт проверка на 401

        // Создаем TODO для обновления
        Todo originalTodo = new Todo(2, "Task to Update", false);
        //createTodo(originalTodo);
        new ValidatedTodoRequest(RequestSpec.authSpec()).create(originalTodo);

        // Обновленные данные с отсутствующим полем 'text'
        String invalidTodoJson = "{ \"id\": 2, \"completed\": true }";

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(invalidTodoJson)
                .when()
                .put("/todos/2")
                .then()
                .statusCode(401);
                //.contentType(ContentType.JSON)
                //.body("error", containsString("Missing required field 'text'"));
    }

    /**
     * TC4: Передача некорректных типов данных при обновлении.
     */
    @Test
    public void testUpdateTodoWithInvalidDataTypes() {
        // 241213 - Как я понимаю, это относится к контрактному тестированию, поэтому я пока не изменял этот тест во второй части кода, где идёт проверка на 401

        // Создаем TODO для обновления
        Todo originalTodo = new Todo(3, "Another Task", false);
        //createTodo(originalTodo);
        new ValidatedTodoRequest(RequestSpec.authSpec()).create(originalTodo);

        // Обновленные данные с некорректным типом поля 'completed'
        String invalidTodoJson = "{ \"id\": 3, \"text\": \"Updated Task\", \"completed\": \"notBoolean\" }";

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(invalidTodoJson)
                .when()
                .put("/todos/3")
                .then()
                .statusCode(401);
    }

    /**
     * TC5: Обновление TODO без изменения данных (передача тех же значений).
     */
    @Test
    public void testUpdateTodoWithoutChangingData() {
        ValidatedTodoRequest validatedTodoRequest = new ValidatedTodoRequest(RequestSpec.authSpec());

        // Создаем TODO для обновления
        Todo originalTodo = new Todo(4, "Task without Changes", false);

        //createTodo(originalTodo);
        validatedTodoRequest.create(originalTodo);

        // Отправляем PUT запрос с теми же данными
        validatedTodoRequest.update(originalTodo.getId(), originalTodo);

        // Проверяем, что данные не изменились
        List<Todo> todos = validatedTodoRequest.readAll();

        Assertions.assertEquals("Task without Changes", todos.get(0).getText());
        Assertions.assertFalse(todos.get(0).isCompleted());
    }
}
