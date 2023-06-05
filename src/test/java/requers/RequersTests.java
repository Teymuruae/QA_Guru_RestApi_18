package requers;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequersTests {


    @Test
    void usersEmailEndsWithReqresTest() {
        Spec.installSpec(HttpStatus.SC_OK);
        ValidatableResponse response =
                RestAssured
                        .when()
                        .get("users")
                        .then();
        List<String> emails = response.extract().path("data.findAll {it.email}.email");
        emails.forEach(x -> Assertions.assertTrue(x.endsWith("reqres.in")));
    }

    @Test
    void getAllUsersIdGreaterThan7() {
        Spec.installSpec(HttpStatus.SC_OK);
        Response response =
                RestAssured
                        .when()
                        .get("users?page=2");
        List<Map<String, Object>> body = response.path("data.findAll {it.id > 7}");
        Assertions.assertNotEquals(0, body.size());
        body.forEach(x -> Assertions.assertTrue((int) x.get("id") > 7));

    }

    @Test
    void createUserTest() {
        String name = "morpheus";
        String job = "leader";
        Map<String, ?> body = new HashMap<>() {{

            put("name", name);
            put("job", job);
        }};
        Spec.installSpec(HttpStatus.SC_CREATED);
        RestAssured
                .given()
                .body(body)
                .when()
                .post("users")
                .then()
                .assertThat()
                .body("name", Matchers.is(name))
                .body("job", Matchers.equalTo(job))
                .body("id", Matchers.notNullValue());
    }

    @Test
    void correctNameByIdTest() {
        Spec.installSpec(HttpStatus.SC_OK);
        String name =
                RestAssured
                        .get("users?page=2")
                        .path("data.find {it.id == 7}.first_name");
        MatcherAssert.assertThat(name, Matchers.equalTo("Michael"));
    }

    @Test
    void yearsInResourceTest(){
        List <Integer> expectedYears = List.of(2000, 2001, 2002,2003, 2004, 2005);
        Spec.installSpec(HttpStatus.SC_OK);
        List <Integer> years =
                RestAssured
                        .get("unknown")
                        .path("data.year");
        Collections.sort(years);
      Assertions.assertEquals(expectedYears, years);

    }
}
