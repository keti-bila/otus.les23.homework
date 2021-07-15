package otus.les23.stubs;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static io.restassured.RestAssured.given;

@SpringBootTest
class StubsApplicationTests {

	private static WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().port(5050));

	@BeforeAll
	public static void setUpMockServer() {
		wireMockServer.start();
		WireMock.configureFor("localhost", 5050);
	}

	@Test
	public void getResourceTest() {
		WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/api/unknown/2"))
				.willReturn(WireMock.aResponse()
						.withStatus(200)
						.withBody("{\n" +
								"    \"data\": {\n" +
								"        \"id\": 2,\n" +
								"        \"name\": \"fuchsia rose\",\n" +
								"        \"year\": 2001,\n" +
								"        \"color\": \"#C74375\",\n" +
								"        \"pantone_value\": \"17-2031\"\n" +
								"    },\n" +
								"    \"support\": {\n" +
								"        \"url\": \"https://reqres.in/#support-heading\",\n" +
								"        \"text\": \"To keep ReqRes free, contributions towards server costs are appreciated!\"\n" +
								"    }\n" +
								"}")));

		Response response = given()
				.contentType(ContentType.JSON)
				.when()
				.get("http://localhost:5050/api/unknown/2")
				.then()
				.extract().response();

		Assertions.assertEquals(200, response.statusCode());
		Assertions.assertEquals("fuchsia rose", response.jsonPath().getString("data.name"));
		Assertions.assertEquals("https://reqres.in/#support-heading", response.jsonPath().getString("support.url"));
	}

	@Test
	public void registerTest() {
		WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/api/register"))
				.willReturn(WireMock.aResponse()
						.withStatus(200)
						.withBody("{\n" +
								"    \"id\": 4,\n" +
								"    \"token\": \"QpwL5tke4Pnpja7X4\"\n" +
								"}")));

		Response response = given()
				.contentType(ContentType.JSON)
				.with()
				.body("{\n" +
						"    \"email\": \"eve.holt@reqres.in\",\n" +
						"    \"password\": \"pistol\"\n" +
						"}")
				.when()
				.post("http://localhost:5050/api/register")
				.then()
				.extract().response();

		response.prettyPrint();

		Assertions.assertEquals(200, response.statusCode());
		Assertions.assertEquals(4, response.jsonPath().getInt("id"));
		Assertions.assertEquals("QpwL5tke4Pnpja7X4", response.jsonPath().getString("token"));
	}

	@Test
	public void createUserTest() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		String time = format.format(new Date());

		WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/api/users"))
				.willReturn(WireMock.aResponse()
						.withStatus(201)
						.withBody("{\n" +
								"    \"name\": \"morpheus\",\n" +
								"    \"job\": \"leader\",\n" +
								"    \"id\": \"78\",\n" +
								"    \"createdAt\": \"" + time + "\"\n" +
								"}")));

		Response response = given()
				.contentType(ContentType.JSON)
				.with()
				.body("{\n" +
						"    \"name\": \"morpheus\",\n" +
						"    \"job\": \"leader\"\n" +
						"}")
				.when()
				.post("http://localhost:5050/api/users")
				.then()
				.extract().response();

		response.prettyPrint();

		Assertions.assertEquals(201, response.statusCode());
		Assertions.assertEquals("morpheus", response.jsonPath().getString("name"));
		Assertions.assertEquals("78", response.jsonPath().getString("id"));
	}

	@AfterAll
	public static void tearDownMockServer() {
		wireMockServer.stop();
	}
}
