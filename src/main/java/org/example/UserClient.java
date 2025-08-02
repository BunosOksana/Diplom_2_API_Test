package org.example;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.given;

public class UserClient {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    private static final String CREATE_USER = "/api/auth/register";
    private static final String LOGIN_USER = "/api/auth/login";
    private static final String DATA_USER = "/api/auth/user";
    private static final RequestSpecification REQUEST_SPECIFICATION_TEMPLATE =
            new RequestSpecBuilder().setBaseUri(BASE_URL).addHeader("Content-Type", "application/json").log(LogDetail.ALL).build();
    private static final ResponseSpecification RESPONSE_SPECIFICATION_TEMLATE =
            new ResponseSpecBuilder().log(LogDetail.ALL).build();

    @Step("Создание пользователя")
    public static ValidatableResponse createUser(String email, String password, String name) {
        return given()
                .spec(REQUEST_SPECIFICATION_TEMPLATE)
                .body("{\n" +
                        "    \"email\": \"" + email + "\",\n" +
                        "    \"password\": \"" + password + "\",\n" +
                        "    \"name\": \"" + name + "\"\n" +
                        "}")
                .post(CREATE_USER)
                .then()
                .spec(RESPONSE_SPECIFICATION_TEMLATE);
    }

    @Step("Получение accessToken")
    public static String extractAccessToken(ValidatableResponse response) {
        return response.extract().path("accessToken");
    }

    @Step("Логинимся пользователем")
    public static ValidatableResponse loginUser(String email, String password) {
        return given()
                .spec(REQUEST_SPECIFICATION_TEMPLATE)
                .body("{\n" +
                        "    \"email\": \"" + email + "\",\n" +
                        "    \"password\": \"" + password + "\"\n" +
                        "}")
                .post(LOGIN_USER)
                .then()
                .spec(RESPONSE_SPECIFICATION_TEMLATE);
    }

    @Step("Изменение данных пользователя с авторизацей")
    public static ValidatableResponse updateAuthUser(String email, String password, String name, String accessToken) {
        return given()
                .spec(REQUEST_SPECIFICATION_TEMPLATE)
                .header("Authorization", accessToken)
                .body("{\n" +
                        "    \"email\": \"" + email + "\",\n" +
                        "    \"password\": \"" + password + "\",\n" +
                        "    \"name\": \"" + name + "\"\n" +
                        "}")
                .patch(DATA_USER)
                .then()
                .spec(RESPONSE_SPECIFICATION_TEMLATE);
    }

    @Step("Изменение данных пользователя без авторизацей")
    public static ValidatableResponse updateNoAuthUser(String email, String password, String name) {
        return given()
                .spec(REQUEST_SPECIFICATION_TEMPLATE)
                .body("{\n" +
                        "    \"email\": \"" + email + "\",\n" +
                        "    \"password\": \"" + password + "\",\n" +
                        "    \"name\": \"" + name + "\"\n" +
                        "}")
                .patch(DATA_USER)
                .then()
                .spec(RESPONSE_SPECIFICATION_TEMLATE);
    }

    @Step("Удаление пользователя")
    public static void deleteUser(String accessToken) {
        if (accessToken != null && !accessToken.isEmpty()) {
            Response response = given()
                    .spec(REQUEST_SPECIFICATION_TEMPLATE)
                    .header("Authorization", accessToken)
                    .delete("/api/auth/user")
                    .then()
                    .extract()
                    .response();

            int status = response.getStatusCode();
            if (status != 202) {
                System.err.println("[WARNING] Не удалось удалить пользователя. Статус: " + status);
            }
        }
    }
}
