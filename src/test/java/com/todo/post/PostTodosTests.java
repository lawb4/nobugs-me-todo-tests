package com.todo.post;

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

public class PostTodosTests extends BaseTest {

    @BeforeEach
    public void setupEach() {
        deleteAllTodos();
    }

    @Test
    public void testCreateTodoWithValidData() {
        ValidatedTodoRequest validatedAuthTodoRequest = new ValidatedTodoRequest(RequestSpec.authSpec());

        Todo newTodo = new Todo(1, "New Task", false);

        // Отправляем POST запрос для создания нового TODO
        validatedAuthTodoRequest.create(newTodo);

        // Проверяем, что TODO было успешно создано
        List<Todo> todos = validatedAuthTodoRequest.readAll();

        // Ищем созданную задачу в списке
        boolean found = false;
        for (Todo todo : todos) {
            if (todo.getId() == newTodo.getId()) {
                Assertions.assertEquals(newTodo.getText(), todo.getText());
                Assertions.assertEquals(newTodo.isCompleted(), todo.isCompleted());
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Созданная задача не найдена в списке TODO");
    }

    /**
     * TC2: Попытка создания TODO с отсутствующими обязательными полями.
     */
    @Test
    public void testCreateTodoWithMissingFields() {
        // 241212 - Как я понимаю, это относится к контрактному тестированию, поэтому я пока не изменял этот тест
        // Создаем JSON без обязательного поля 'text'
        String invalidTodoJson = "{ \"id\": 2, \"completed\": true }";

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(invalidTodoJson)
                .when()
                .post("/todos")
                .then()
                .statusCode(400)
                .contentType(ContentType.TEXT)
                .body(notNullValue()); // Проверяем, что есть сообщение об ошибке
    }

    /**
     * TC3: Создание TODO с максимально допустимой длиной поля 'text'.
     */
    @Test
    public void testCreateTodoWithMaxLengthText() {
        ValidatedTodoRequest validatedAuthTodoRequest = new ValidatedTodoRequest(RequestSpec.authSpec());

        // Предполагаем, что максимальная длина поля 'text' составляет 255 символов
        String maxLengthText = "A".repeat(255);
        Todo newTodo = new Todo(3, maxLengthText, false);

        // Отправляем POST запрос для создания нового TODO
        validatedAuthTodoRequest.create(newTodo);

        // Проверяем, что TODO было успешно создано
        List<Todo> todos = validatedAuthTodoRequest.readAll();

        // Ищем созданную задачу в списке
        boolean found = false;
        for (Todo todo : todos) {
            if (todo.getId() == newTodo.getId()) {
                Assertions.assertEquals(newTodo.getText(), todo.getText());
                Assertions.assertEquals(newTodo.isCompleted(), todo.isCompleted());
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Созданная задача не найдена в списке TODO");
    }

    /**
     * TC4: Передача некорректных типов данных в полях.
     */
    @Test
    public void testCreateTodoWithInvalidDataTypes() {
        // 241212 - Как я понимаю, это относится к контрактному тестированию, поэтому я пока не изменял этот тест
        // Поле 'completed' содержит строку вместо булевого значения
        String invalidTodoJson = "{ \"id\": 4, \"text\": \"Invalid Data Type\", \"completed\": \"notBoolean\" }";

        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(invalidTodoJson)
                .when()
                .post("/todos")
                .then()
                .statusCode(400)
                .contentType(ContentType.TEXT)
                .body(notNullValue()); // Проверяем, что есть сообщение об ошибке
    }

    /**
     * TC5: Создание TODO с уже существующим 'id' (если 'id' задается клиентом).
     */
    @Test
    public void testCreateTodoWithExistingId() {
        // Сначала создаем TODO с id = 5
        Todo firstTodo = new Todo(5, "First Task", false);

        //createTodo(firstTodo);
        new ValidatedTodoRequest(RequestSpec.authSpec()).create(firstTodo);

        // Пытаемся создать другую TODO с тем же id
        Todo duplicateTodo = new Todo(5, "Duplicate Task", true);

        TodoRequest todoRequest = new TodoRequest(RequestSpec.authSpec());

        todoRequest.create(duplicateTodo)
                .then()
                .statusCode(400) // Конфликт при дублировании 'id'
                //.contentType(ContentType.TEXT)
                .body(is(notNullValue())); // Проверяем, что есть сообщение об ошибке
    }
}
