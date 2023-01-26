import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.response.CreateUserResp;
import model.request.CreateUser;
import model.response.GetUserListResp;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDate;

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

    @Test
    public void createUser_1(){
        String user = "{\n" +
                "    \"name\": \"morpheus\",\n" +
                "    \"job\": \"leader\"\n" +
                "}";

        RestAssured.given()
                // первый вариант берется из Postman (раздел header)
                // 1-й вариант: .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .baseUri(BASE_URI)
                .log().all()
                .post("/api/users")
                .then()
                .log().all()
                .assertThat()
                .statusCode(201);
    }

    @Test
    public void createUser_2(){

        String name = "morpheus";
        String job = "leader";
        // можно использовать рандомайзер (генератор) (например faker) разных имен.

        CreateUser user = new CreateUser(name, job);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .baseUri(BASE_URI)
                .log().all()
                .post("/api/users")
                .then()
                .log().all()
                .assertThat()
                .statusCode(201)
                .body("name", Matchers.equalTo("morpheus"))
                .body("job", Matchers.equalTo("leader"));
    }

    @Test
    public void createUser_3(){
        CreateUser user = new CreateUser("morpheus", "leader");

        CreateUserResp createUserResp = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .baseUri(BASE_URI)
                .log().all()
                .post("/api/users")
                .then()
                .log().all()
                .extract().as(CreateUserResp.class);

        Assert.assertEquals(createUserResp.name, "morpheus");
        Assert.assertEquals(createUserResp.job, "leader");
        Assert.assertTrue(createUserResp.createdAt.contains(LocalDate.now().toString()));
    }

    @Test
    public void getUserListRespClass(){
        GetUserListResp getUserListResp = RestAssured.given()
                .when()
                .baseUri(BASE_URI)
                .log().all()
                .get("/api/users?page=2")
                .then()
                .log().all()
                .extract().as(GetUserListResp.class);
        Assert.assertEquals(getUserListResp.data.get(3).first_name, "Byron");

//        System.out.println(getUserListResp.data.get(4).id);
//        System.out.println(getUserListResp.data.get(1).last_name);
//        System.out.println(getUserListResp.page);
//        System.out.println(getUserListResp.total);

    }

}
