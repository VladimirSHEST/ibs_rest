package for_selenoid;

import com.ibs.ProductData;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class AddProductExistSelenoid {
    private final String URL = "http://localhost:8080/api/food";

    @Test
    void addProductTypeExistFruit() {
        // Проверка, что фрукт "Яблоко" уже существует
        List<ProductData> productsDefault = given()
                .log().all()
                .when()
                .contentType(ContentType.JSON)
                .get(URL)
                .then().log().all()
                .extract().body().jsonPath()
                .getList("", ProductData.class);

        String nameProductsDefault = productsDefault.stream()
                .map(ProductData::getName)
                .filter(name -> name.contains("Яблоко"))
                .collect(Collectors.toList())
                .toString();
        System.out.println(nameProductsDefault);

        // Проверяем, что "Яблоко" существует
        int count_1 = (int) productsDefault.stream()
                .map(ProductData::getName)
                .filter(name -> name.contains("Яблоко"))
                .count();
        System.out.println(count_1);
        Assertions.assertEquals(1, count_1, "Количество яблок не равно 1");

        // Добавляем ещё одно "Яблоко"
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
                .extract().detailedCookies(); // Извлекаем все куки

        // Проверяем, что количество "Яблоко" увеличилось
        List<ProductData> productsApple2 = given()
                .log().all()
                .cookies(cookies)
                .when()
                .contentType(ContentType.JSON)
                .get(URL)
                .then().log().all()
                .extract().body().jsonPath()
                .getList("", ProductData.class);

        int count = (int) productsApple2.stream()
                .map(ProductData::getName)
                .filter(name -> name.contains("Яблоко"))
                .count();
        System.out.println(count);
        Assertions.assertEquals(2, count, "Количество яблок не равно 2");

        // Сброс до дефолтных настроек
        given()
                .log().all()
                .cookies(cookies)
                .when()
                .contentType(ContentType.JSON)
                .post("http://localhost:8080/api/data/reset")
                .then()
                .log().all();

        // Проверка, что фрукт "Яблоко" удален
        List<ProductData> deleteProduct = given()
                .log().all()
                .cookies(cookies)
                .when()
                .contentType(ContentType.JSON)
                .get(URL)
                .then().log().all()
                .extract().body().jsonPath()
                .getList("", ProductData.class);

        // Проверяем, что количество "Яблоко" уменьшилось
        int countAfterDelete = (int) deleteProduct.stream()
                .map(ProductData::getName)
                .filter(name -> name.contains("Яблоко"))
                .count();
        System.out.println(countAfterDelete);
        Assertions.assertEquals(1, countAfterDelete, "Количество яблок не равно 1 после удаления");
    }
}