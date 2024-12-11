package com.ibs;

import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.internal.common.assertion.Assertion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AddProductExist {
    private final String URL = "http://localhost:8080/api/food";

    @Test
    void addProductTypeExistFruit() {
        // проверка, что фрукт яблоко уже есть
        List<ProductData> productsDefault = given()
                .log().all()
                .when()
                .contentType(ContentType.JSON)
                .get(URL)
                .then().log().all()
                .extract().body().jsonPath()
                .getList("", ProductData.class);
        String nameProductsDefault = productsDefault.stream().map(x -> x.getName()).filter(x -> x.contains("Яблоко"))
                .collect(Collectors.toList()).toString();
        System.out.println(nameProductsDefault);

                // Проверяем, что Яблоко есть
        int count_1 = (int) productsDefault.stream().map(x ->x.getName()).filter(x -> x.contains("Яблоко")).count();
        System.out.println(count_1);
        Assertions.assertEquals(1, count_1, "кол-во яблок не равно 1");
        // добавили ещё одно Яблоко

        String data = "{\"name\": \"Яблоко\", \"type\": \"FRUIT\", \"exotic\":false}";

        Cookies cookies = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(data)
                .when().relaxedHTTPSValidation()
                .post(URL)
                .then()
                .log().all()
                .statusCode(200)
                .extract().detailedCookies(); // Извлекаем все куки;

        List<ProductData> productsApple2 = given()
                .log().all()
                .cookies(cookies)
                .when()
                .contentType(ContentType.JSON)
                .get(URL)
                .then().log().all()
                .extract().body().jsonPath()
                .getList("", ProductData.class);

        int count = (int) productsApple2.stream().map(x ->x.getName()).filter(x -> x.contains("Яблоко")).count();
        System.out.println(count);
        Assertions.assertEquals(2, count, "кол-во яблок не равно 2");

        // сброс до дефолтных настроек
        given()
                .log().all()
                .cookies(cookies)
                .when()
                .contentType(ContentType.JSON)
                .post("http://localhost:8080/api/data/reset")
                .then()
                .log().all();

        // проверка, что фрукт яблоко удалили
        List<ProductData> deleteProduct = given()
                .log().all()
                .cookies(cookies)
                .when()
                .contentType(ContentType.JSON)
                .get(URL)
                .then().log().all()
                .extract().body().jsonPath()
                .getList("", ProductData.class);

        // Проверяем, что количество яблок уменьшилось
        int countAfterDelete = (int) deleteProduct.stream().map(x -> x.getName()).filter(x -> x.contains("Яблоко")).count();
        System.out.println(countAfterDelete);
        Assertions.assertEquals(1, countAfterDelete, "кол-во яблок не равно 1 после удаления");

    }

}
