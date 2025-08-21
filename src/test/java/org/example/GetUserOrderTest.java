package org.example;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Optional.empty;
import static org.hamcrest.CoreMatchers.*;

public class GetUserOrderTest {
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

    @Before
    public void setUp() {
        String email = "test_" + System.currentTimeMillis() + "@example.com";
        String password = "password";
        String name = "Test User";
        ValidatableResponse response = UserClient.createUser(email, password, name);
        accessToken = UserClient.extractAccessToken(response);
    }

    @Test
    @DisplayName("Получение заказов пользователя")
    @Description("Получение заказов авторизованного пользователя")
    public void getUserOrdersWithAuth_success() {
        List<String> buns = Orders.getIngredients()
                .extract()
                .jsonPath()
                .getList("data.findAll { it.type == 'bun' }._id");

        List<String> fillings = Orders.getIngredients()
                .extract()
                .jsonPath()
                .getList("data.findAll { it.type in ['main', 'sauce'] }._id");

        List<String> orderIngredients = List.of(buns.get(0), buns.get(0), fillings.get(0));
        Orders.createOrder(orderIngredients, accessToken);
        ValidatableResponse response = Orders.getUserOrder(accessToken);
        response
                .statusCode(200)
                .body("orders", not(empty()))
                .body("orders[0].ingredients", hasItems(orderIngredients.toArray()));
    }

    @Test
    @DisplayName("Получение заказов пользователя")
    @Description("Получение заказов неавторизованного пользователя")
    public void getUserOrdersWithoutAuth_fail() {
        ValidatableResponse response = Orders.getUserOrder("invalid_token");
        response
                .statusCode(401)
                .body("message", is("You should be authorised"));
    }
}
