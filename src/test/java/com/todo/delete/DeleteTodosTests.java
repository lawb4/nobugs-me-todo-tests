package com.todo.delete;

import com.todo.BaseTest;
import com.todo.models.Todo;
import com.todo.requests.TodoRequest;
import com.todo.requests.ValidatedTodoRequest;
import com.todo.specs.RequestSpec;
import io.qameta.allure.restassured.AllureRestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

public class DeleteTodosTests extends BaseTest {

    @BeforeEach
    public void setupEach() {
        deleteAllTodos();
    }

    /**
     * TC1: Успешное удаление существующего TODO с корректной авторизацией.
     */
    @Test
    public void testDeleteExistingTodoWithValidAuth() {
        ValidatedTodoRequest validatedAuthTodoRequest = new ValidatedTodoRequest(RequestSpec.authSpec());

        // Создаем TODO для удаления
        Todo todo = new Todo(1, "Task to Delete", false);
        validatedAuthTodoRequest.create(todo);

        // Отправляем DELETE запрос с корректной авторизацией
        validatedAuthTodoRequest.delete(1);

        // Получаем список всех TODO и проверяем, что удаленная задача отсутствует
        List<Todo> todos = validatedAuthTodoRequest.readAll();

        // Проверяем, что удаленная задача отсутствует в списке
        boolean found = false;
        for (Todo t : todos) {
            if (t.getId() == todo.getId()) {
                found = true;
                break;
            }
        }
        Assertions.assertFalse(found, "Удаленная задача все еще присутствует в списке TODO");
    }

    /**
     * TC2: Попытка удаления TODO без заголовка Authorization.
     */
    @Test
    public void testDeleteTodoWithoutAuthHeader() {
        ValidatedTodoRequest validatedTodoRequest = new ValidatedTodoRequest(RequestSpec.authSpec());

        // Создаем TODO для удаления
        Todo todo = new Todo(2, "Task to Delete", false);

        //createTodo(todo);
        validatedTodoRequest.create(todo);

        // Отправляем DELETE запрос без заголовка Authorization
        TodoRequest todoRequest = new TodoRequest(RequestSpec.unauthSpec());
        todoRequest.delete(todo.getId())
                .then()
                .statusCode(401);
                //.contentType(ContentType.JSON)
                //.body("error", notNullValue()); // Проверяем наличие сообщения об ошибке

        // Проверяем, что TODO не было удалено
        List<Todo> todos = validatedTodoRequest.readAll();

        // Проверяем, что задача все еще присутствует в списке
        boolean found = false;
        for (Todo t : todos) {
            if (t.getId() == todo.getId()) {
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Задача отсутствует в списке TODO, хотя не должна была быть удалена");
    }

    /**
     * TC3: Попытка удаления TODO с некорректными учетными данными.
     */
    @Test
    public void testDeleteTodoWithInvalidAuth() {
        ValidatedTodoRequest validatedTodoRequest = new ValidatedTodoRequest(RequestSpec.authSpec());

        // Создаем TODO для удаления
        Todo todo = new Todo(3, "Task to Delete", false);
        //createTodo(todo);
        validatedTodoRequest.create(todo);

        // Отправляем DELETE запрос с некорректной авторизацией
        TodoRequest todoRequest = new TodoRequest(RequestSpec.invalidAuthSpec());
        todoRequest.delete(todo.getId())
                .then()
                .statusCode(401);
//                .contentType(ContentType.JSON)
//                .body("error", notNullValue());

        // Проверяем, что TODO не было удалено
        List<Todo> todos = validatedTodoRequest.readAll();

        // Проверяем, что задача все еще присутствует в списке
        boolean found = false;
        for (Todo t : todos) {
            if (t.getId() == todo.getId()) {
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Задача отсутствует в списке TODO, хотя не должна была быть удалена");
    }

    /**
     * TC4: Удаление TODO с несуществующим id.
     */
    @Test
    public void testDeleteNonExistentTodo() {
        TodoRequest todoRequest = new TodoRequest(RequestSpec.authSpec());
        long nonExistentTodoId = 999;

        // Отправляем DELETE запрос для несуществующего TODO с корректной авторизацией
        todoRequest.delete(nonExistentTodoId)
                .then()
                .statusCode(404);
//                .contentType(ContentType.JSON)
//                .body("error", notNullValue());

        // Дополнительно можем проверить, что список TODO не изменился
        ValidatedTodoRequest validatedTodoRequest = new ValidatedTodoRequest(RequestSpec.authSpec());
        List<Todo> todos = validatedTodoRequest.readAll();

        // В данном случае, поскольку мы не добавляли задач с id 999, список должен быть пуст или содержать только ранее добавленные задачи
        Assertions.assertTrue(todos.isEmpty(), "Почему-то в списке существуют задачи, которых не создавали");
    }

    /**
     * TC5: Попытка удаления с некорректным форматом id (например, строка вместо числа).
     */
    @Test
    public void testDeleteTodoWithInvalidIdFormat() {
        // 241212 - Как я понимаю, это относится к контрактному тестированию, поэтому я пока не изменял этот тест

        // Отправляем DELETE запрос с некорректным id
        given()
                .filter(new AllureRestAssured())
                .auth()
                .preemptive()
                .basic("admin", "admin")
                .when()
                .delete("/todos/invalidId")
                .then()
                .statusCode(404);
//                .contentType(ContentType.JSON)
//                .body("error", notNullValue());
    }
}
