package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class LoginUserTest {
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
    @DisplayName("Логин пользователя")
    @Description("Успешный логин пользователя")
    public void loginUser_success() {
        email = RandomStringUtils.randomAlphabetic(8).toLowerCase() + "@ya.ru";
        password = RandomStringUtils.randomAlphabetic(10);
        name = RandomStringUtils.randomAlphabetic(10);

        ValidatableResponse response = UserClient
                .createUser(email, password, name);
        accessToken = UserClient.extractAccessToken(response);

        UserClient
                .loginUser(email, password)
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Логин пользователя")
    @Description("Ошибка при авторизации с неверным логином")
    public void loginCourier_wrongLogin() {
        email = RandomStringUtils.randomAlphabetic(8).toLowerCase() + "@ya.ru";
        password = RandomStringUtils.randomAlphabetic(10);
        name = RandomStringUtils.randomAlphabetic(10);

        ValidatableResponse response = UserClient
                .createUser(email, password, name);
        accessToken = UserClient.extractAccessToken(response);

        UserClient
                .loginUser((RandomStringUtils.randomAlphabetic(8).toLowerCase() + "@ya.ru"), password)
                .statusCode(401)
                .body("message", is("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин пользователя")
    @Description("Ошибка при авторизации с неверным паролем")
    public void loginCourier_wrongPassword() {
        email = RandomStringUtils.randomAlphabetic(8).toLowerCase() + "@ya.ru";
        password = RandomStringUtils.randomAlphabetic(10);
        name = RandomStringUtils.randomAlphabetic(10);

        ValidatableResponse response = UserClient
                .createUser(email, password, name);
        accessToken = UserClient.extractAccessToken(response);

        UserClient
                .loginUser(email, (RandomStringUtils.randomAlphabetic(10)))
                .statusCode(401)
                .body("message", is("email or password are incorrect"));
    }
}
