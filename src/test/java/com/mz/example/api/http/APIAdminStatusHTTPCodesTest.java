package com.mz.example.api.http;

import com.mz.example.api.APIRequestMapping;
import com.mz.example.api.AbstractAPIControllerTest;
import com.mz.example.api.status.Status;
import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class APIAdminStatusHTTPCodesTest extends AbstractAPIControllerTest {

    @Test
    public void testAdminStatus() throws Exception {
        RestAssured.with()
                    .header("Authorization", "Basic "+ getAdminAuthorizationToken())
                .when()
                    .get(APIRequestMapping.ADMIN_STATUS_METHOD)
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .body("status", CoreMatchers.equalTo(Status.WORKING.name()));

    }

    @Test
    public void testAdminStatusFailOnNoCredentials() throws Exception {
        RestAssured.with()
                .when()
                    .get(APIRequestMapping.ADMIN_STATUS_METHOD)
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .body("status", CoreMatchers.equalTo(HttpStatus.UNAUTHORIZED.value()))
                    .body("error", CoreMatchers.equalTo(HttpStatus.UNAUTHORIZED.getReasonPhrase()))
                    .body("path", CoreMatchers.equalTo(APIRequestMapping.ADMIN_STATUS_METHOD));
    }

    @Test
    public void testAdminStatusFailOnInvalidCredentials() throws Exception {
        final String INVALID_CREDENTIALS = "invalidCredentials";
        RestAssured.with()
                    .header("Authorization", "Basic "+ INVALID_CREDENTIALS)
                .when()
                    .get(APIRequestMapping.ADMIN_STATUS_METHOD)
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .body("status", CoreMatchers.equalTo(HttpStatus.UNAUTHORIZED.value()))
                    .body("error", CoreMatchers.equalTo(HttpStatus.UNAUTHORIZED.getReasonPhrase()))
                    .body("path", CoreMatchers.equalTo(APIRequestMapping.ADMIN_STATUS_METHOD));
    }

    @Test
    public void testAdminStatusFailOnUserCredentials() throws Exception {
        RestAssured.with()
                    .header("Authorization", "Basic "+ getUserAuthorizationToken())
                .when()
                    .get(APIRequestMapping.ADMIN_STATUS_METHOD)
                .then()
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .body("status", CoreMatchers.equalTo(HttpStatus.FORBIDDEN.value()))
                    .body("error", CoreMatchers.equalTo(HttpStatus.FORBIDDEN.getReasonPhrase()))
                    .body("path", CoreMatchers.equalTo(APIRequestMapping.ADMIN_STATUS_METHOD));
    }
}
