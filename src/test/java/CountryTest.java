import Model.Country;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class CountryTest {
    private Cookies cookies;

    @BeforeClass
    public void authenticate() {
        RestAssured.baseURI = "https://test-basqar.mersys.io";
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "nigeria_tenant_admin");
        credentials.put("password", "TnvLOl54WxR75vylop2A");

        cookies = given()
                .body(credentials)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract().response().getDetailedCookies();

    }

    @Test
    public void getBasePath() {
        given()
                .when()
                .get()
                .then()
                .statusCode(200)
        ;

    }

    @Test
    public void getCountries() {
        given()
                .cookies(cookies)
                .when()
                .get("/school-service/api/countries")
                .then()
                .statusCode(200)
        ;

    }

    @Test
    public void createCountry(){
        Country country =new Country();
        country.setName("Tugba");
        country.setCode("AYvv");

        String countryId=given()
                .cookies(cookies)
                .body(country)
                .contentType(ContentType.JSON)
                .when()
                .post("/school-service/api/countries")
                .then()
                .log().everything()
                .statusCode(201)
                .extract().jsonPath().getString("id")
                ;

        // delete the country

        given()
                .cookies(cookies)
                .when()
                .delete("/school-service/api/countries/"+countryId)
                .then()
                .statusCode(200)
                ;
    }

    @Test
    public void editTest(){
        Country country =new Country();
        country.setName("Tugba");
        country.setCode("AYvv");
// create the country
        String countryId=given()
                .cookies(cookies)
                .body(country)
                .contentType(ContentType.JSON)
                .when()
                .post("/school-service/api/countries")
                .then()
                .log().everything()
                .statusCode(201)
                .extract().jsonPath().getString("id")
                ;

        // edit the country
        // Editing country
        country.setId( countryId );
        country.setName( "Daulet Edited" );
        country.setCode( "Code Edited" );
        given()
                .cookies( cookies )
                .body( country )
                .contentType( ContentType.JSON )
                .when()
                .put("/school-service/api/countries")
                .then()
                .statusCode( 200 )
                .body( "name", equalTo( country.getName() ) )
                .body( "code", equalTo( country.getCode() ) )
        ;
        // delete the country

        given()
                .cookies(cookies)
                .when()
                .delete("/school-service/api/countries/"+countryId)
                .then()
                .statusCode(200)
        ;

        // delete the country again


        given()
                .cookies(cookies)
                .when()
                .delete("/school-service/api/countries/"+countryId)
                .then()
                .statusCode(404)
        ;
    }

}
