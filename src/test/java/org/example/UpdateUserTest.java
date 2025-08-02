package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class UpdateUserTest {
    private String email;
    private String password;
    private String name;
    private String accessToken;
    private String newEmail;
    private String newName;

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
    @DisplayName("Изменение данных пользователя с авторизацией")
    @Description("Изменение email авторизованного пользователя")
    public void updateEmailAuthUser() {
        email = RandomStringUtils.randomAlphabetic(8).toLowerCase() + "@ya.ru";
        password = RandomStringUtils.randomAlphabetic(10);
        name = RandomStringUtils.randomAlphabetic(10);
        newEmail = RandomStringUtils.randomAlphabetic(8).toLowerCase() + "@ya.ru";

        ValidatableResponse response = UserClient
                .createUser(email, password, name);
        accessToken = UserClient.extractAccessToken(response);
        UserClient
                .updateAuthUser(newEmail, password, name, accessToken)
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", is(newEmail))
                .body("user.name", is(name));
    }

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией")
    @Description("Изменение имени авторизованного пользователя")
    public void updateNameAuthUser() {
        email = RandomStringUtils.randomAlphabetic(8).toLowerCase() + "@ya.ru";
        password = RandomStringUtils.randomAlphabetic(10);
        name = RandomStringUtils.randomAlphabetic(10);
        newName = RandomStringUtils.randomAlphabetic(10);

        ValidatableResponse response = UserClient
                .createUser(email, password, name);
        accessToken = UserClient.extractAccessToken(response);
        UserClient
                .updateAuthUser(email, password, newName, accessToken)
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", is(email))
                .body("user.name", is(newName));
    }

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией")
    @Description("Изменение email пользователя на уже существующий")
    public void updateAuthUserToExistingEmail() {
        email = RandomStringUtils.randomAlphabetic(8).toLowerCase() + "@ya.ru";
        password = RandomStringUtils.randomAlphabetic(10);
        name = RandomStringUtils.randomAlphabetic(10);
        newEmail = RandomStringUtils.randomAlphabetic(8).toLowerCase() + "@ya.ru";


        ValidatableResponse firstResponse = UserClient
                .createUser(email, password, name);
        String firstUserToken = UserClient.extractAccessToken(firstResponse);

        ValidatableResponse response = UserClient
                .createUser(newEmail, password, name);
        accessToken = UserClient.extractAccessToken(response);

        UserClient
                .updateAuthUser(email, password, name, accessToken)
                .statusCode(403)
                .body("success", is(false))
                .body("message", is("User with such email already exists"));

        UserClient.deleteUser(firstUserToken);
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизацией")
    @Description("Изменение данных неавторизованного пользователя")
    public void updateNoAuthUser() {
        email = RandomStringUtils.randomAlphabetic(8).toLowerCase() + "@ya.ru";
        password = RandomStringUtils.randomAlphabetic(10);
        name = RandomStringUtils.randomAlphabetic(10);
        newName = RandomStringUtils.randomAlphabetic(10);
        newEmail = RandomStringUtils.randomAlphabetic(8).toLowerCase() + "@ya.ru";


        ValidatableResponse response = UserClient
                .createUser(email, password, name);
        accessToken = UserClient.extractAccessToken(response);
        UserClient
                .updateNoAuthUser(newEmail, password, newName)
                .statusCode(401)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }
}
