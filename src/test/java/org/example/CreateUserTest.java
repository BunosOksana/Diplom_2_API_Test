package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class CreateUserTest {
    private String email;
    private String password;
    private String name;
    private String accessToken;

    @After
    public void tearDown() {
        if (accessToken != null) {
            UserClient.deleteUser(accessToken);
            System.out.println("Удаление пользователя с токеном: " + accessToken);
        } else {
            System.out.println("Токен отсутствует, удаление не выполнено");
        }
    }

    @Test
    @DisplayName("Создание пользователя")
    @Description("Успешное создание уникального пользователя")
    public void createUser_success() {
        email = RandomStringUtils.randomAlphabetic(8).toLowerCase() + "@ya.ru";
        password = RandomStringUtils.randomAlphabetic(10);
        name = RandomStringUtils.randomAlphabetic(10);

        ValidatableResponse response = UserClient
                .createUser(email, password, name)
                .statusCode(200)
                .body("success", is(true));
        accessToken = UserClient.extractAccessToken(response);
    }

    @Test
    @DisplayName("Создание пользователя")
    @Description("Создание пользователя, который уже существует")
    public void createUserDuplicate_unsuccess() {
        email = RandomStringUtils.randomAlphabetic(8).toLowerCase() + "@ya.ru";
        password = RandomStringUtils.randomAlphabetic(10);
        name = RandomStringUtils.randomAlphabetic(10);

        ValidatableResponse response = UserClient
                .createUser(email, password, name);
               accessToken = UserClient.extractAccessToken(response);
        UserClient
                .createUser(email, password, name)
                .statusCode(403)
                .body("message", is("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя")
    @Description("Создание пользователя без email")
    public void createUserNoEmail_unsuccess() {
        password = RandomStringUtils.randomAlphabetic(10);
        name = RandomStringUtils.randomAlphabetic(10);

        UserClient
                .createUser("", password, name)
                .statusCode(403)
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя")
    @Description("Создание пользователя без пароля")
    public void createUserNoPassword_unsuccess() {
        email = RandomStringUtils.randomAlphabetic(8).toLowerCase() + "@ya.ru";
        name = RandomStringUtils.randomAlphabetic(10);

        UserClient
                .createUser(email, "", name)
                .statusCode(403)
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя")
    @Description("Создание пользователя без имени")
    public void createUserNoName_unsuccess() {
        email = RandomStringUtils.randomAlphabetic(8).toLowerCase() + "@ya.ru";
        password = RandomStringUtils.randomAlphabetic(10);

        UserClient
                .createUser(email, password, "")
                .statusCode(403)
                .body("message", is("Email, password and name are required fields"));
    }
}