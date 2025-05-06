package roomescape;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReservationApiTest {

    @Test
    @DisplayName("예약 조회 테스트")
    void searchReservationsWithStatus200Test() {
        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("예약 생성 테스트")
    void createReservationWithStatus201Test() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운");
        params.put("date", LocalDate.now().plusDays(3).toString());
        params.put("timeId", "1");
        params.put("themeId", "1");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("예약 삭제 테스트")
    void deleteReservationWith204Test() {
        RestAssured.given().log().all()
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("이미 예약된 시간에 예약 시도 시 400 상태코드를 반환한다")
    void createDuplicateReservationReturns400Test() {
        // 첫 번째 예약 생성
        Map<String, String> params = new HashMap<>();
        params.put("name", "첫번째예약");
        params.put("date", LocalDate.now().plusDays(3).toString());
        params.put("timeId", "1");
        params.put("themeId", "2");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then()
                .statusCode(201);

        // 동일한 날짜/시간/테마로 두 번째 예약 시도
        params.put("name", "두번째예약");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);  // 중복 예약 시도는 실패해야 함
    }

}
