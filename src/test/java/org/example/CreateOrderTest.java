package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;

public class CreateOrderTest {
    private String accessToken;
    private List<String> validIngredients;

    @After
    public void tearDown() {
        if (accessToken != null) {
            UserClient.deleteUser(accessToken);
            System.out.println("Удаление пользователя с токеном: " + accessToken);
        } else {
            System.out.println("Токен отсутствует, удаление не выполнено");
        }
    }

    @Before
    public void setUp() {
        String email = "test_" + System.currentTimeMillis() + "@example.com";
        String password = "password";
        String name = "Test User";
        ValidatableResponse response = UserClient.createUser(email, password, name);
        accessToken = UserClient.extractAccessToken(response);

        validIngredients = Orders.getIngredients()
                .extract()
                .jsonPath()
                .getList("data._id");
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Создание заказа авторизованным пользователем с ингредиентами")
    public void createOrderWithAuthAndValidIngredients_success() {
        List<String> buns = Orders.getIngredients()
                .extract()
                .jsonPath()
                .getList("data.findAll { it.type == 'bun' }._id");

        List<String> fillings = Orders.getIngredients()
                .extract()
                .jsonPath()
                .getList("data.findAll { it.type in ['main', 'sauce'] }._id");

        ValidatableResponse response = Orders.createOrder(
                List.of(buns.get(0), buns.get(0), fillings.get(0)),
                accessToken
        );

        response
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Создание заказа неавторизованным пользователем")
    public void createOrderNoAuth_success() {
        List<String> buns = Orders.getIngredients()
                .extract()
                .jsonPath()
                .getList("data.findAll { it.type == 'bun' }._id");

        ValidatableResponse response = Orders.createOrder(
                List.of(buns.get(0), buns.get(0)),
                null
        );

        response.statusCode(200);
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Создание заказа без ингредиентов")
    public void createOrderNoIngredients_unsuccess() {
        ValidatableResponse response = Orders.createOrder(
                List.of(),
                accessToken
        );
        response
                .statusCode(400)
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Создание заказа с неверным хэшом")
    public void createOrderInvalidHash_unsuccess() {
        ValidatableResponse response = Orders.createOrder(
                List.of("invalid_hash"),
                accessToken
        );
        response.statusCode(500);
    }
}
