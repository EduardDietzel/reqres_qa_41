import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class ApiTests {
    public final String BASE_URI = "https://reqres.in";


    // Пример выполнения get-запроса без проверок
    @Test
    public void getListUser_1() {
        // всегда начинаем тест со слова - given()
        given()
                .when()
                .log().all()     // это мы просим вывести весь get-запрос в консоль
                .get("https://reqres.in/api/users?page=2")
                .then()
                .log().all();
    }

    // Пример выполнения гет-запроса с проверкой полей с помощью TestNG
    @Test
    public void getListUser_2() {
        Response response = given()
                .when()
//                .log().all()     // это мы просим вывести весь get-запрос в консоль
                .get("https://reqres.in/api/users?page=2")
                .then()
//                .log().all()
                .extract().response();
        Assert.assertEquals(response.getStatusCode(), 200, "The actual statusCode is not 200");
        Assert.assertEquals(response.body().jsonPath().getInt("data[0].id"), 7);                                   // проверка айди
        Assert.assertEquals(response.body().jsonPath().getString("data[1].email"), "lindsay.ferguson@reqres.in");  // проверка имейла
        Assert.assertEquals(response.body().jsonPath().getString("data[2].last_name"), "Funke");                   // проверка фамилии
    }

    // Пример выполнения гет-запроса с проверкой полей с помощью Rest Assured
    @Test
    public void getListUser_3() {
        given()
                .when()
                .baseUri(BASE_URI)
                .log().all()
                .get("/api/users?page=2")
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("data[0].id", Matchers.equalTo(7))
                .body("data[1].email", Matchers.equalTo("lindsay.ferguson@reqres.in"));
    }
}
