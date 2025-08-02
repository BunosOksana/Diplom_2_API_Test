package org.example;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.util.List;

import static io.restassured.RestAssured.given;

public class Orders {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    private static final String ORDER_URI = "/api/orders";
    private static final String INGREDIENTS_URI = "/api/ingredients";
    private static final RequestSpecification REQUEST_SPECIFICATION_TEMPLATE =
            new RequestSpecBuilder().setBaseUri(BASE_URL).addHeader("Content-Type", "application/json").log(LogDetail.ALL).build();

    @Step("Создание заказа")
    public static ValidatableResponse createOrder(List<String> ingredients, String accessToken) {
        return given().log().ifValidationFails()
                .spec(REQUEST_SPECIFICATION_TEMPLATE)
                .header("Authorization", accessToken != null ? accessToken : "")
                .log()
                .all()
                .body("{\"ingredients\": " + new Gson().toJson(ingredients) + "}")
                .post(ORDER_URI)
                .then()
                .log()
                .all();

    }

    @Step("Получение заказа пользователя")
    public static ValidatableResponse getUserOrder(String accessToken) {
        return given().log().ifValidationFails()
                .spec(REQUEST_SPECIFICATION_TEMPLATE)
                .header("Authorization", accessToken)
                .log()
                .all()
                .get(ORDER_URI)
                .then()
                .log()
                .all();
    }

    @Step("Получение списка инредиентов")
    public static ValidatableResponse getIngredients() {
        return given().log().ifValidationFails()
                .spec(REQUEST_SPECIFICATION_TEMPLATE)
                .log()
                .all()
                .get(INGREDIENTS_URI)
                .then()
                .log()
                .all();
    }
}
