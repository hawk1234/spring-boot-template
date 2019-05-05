package com.mz.example.api.documentation;

import com.mz.example.api.APIRequestMapping;
import com.mz.example.api.AbstractAPIControllerTest;
import com.mz.example.api.status.Status;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation;
import org.springframework.restdocs.snippet.Snippet;

import java.util.ArrayList;
import java.util.List;

public class APIDocumentationTest extends AbstractAPIControllerTest {

    //documentation
    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation(System.getProperty("asciidoc_snippets"));
    private RequestSpecification documentationSpec;

    @Before
    public void setupDocumentation() {
        this.documentationSpec = new RequestSpecBuilder()
                .addFilter(RestAssuredRestDocumentation.documentationConfiguration(restDocumentation)
                        .operationPreprocessors().withRequestDefaults(
                                Preprocessors.prettyPrint(),
                                Preprocessors.modifyUris().scheme("https")
                                        .host("api.example.host.com")
                                        .removePort()
                        ).withResponseDefaults(
                                Preprocessors.prettyPrint()
                        )).build();
    }

    @Test
    public void testUserStatusDocumentation() throws Exception {
        performStatusMethodTest(APIRequestMapping.USER_STATUS_METHOD, null, "status");
    }

    @Test
    public void testAdminStatusDocumentation() throws Exception {
        performStatusMethodTest(APIRequestMapping.ADMIN_STATUS_METHOD, getAdminAuthorizationToken(), "admin status");
    }

    private void performStatusMethodTest(String methodPath, String credentials, String docSnippetName) throws Exception{
        List<Snippet> snippets = new ArrayList<>();
        snippets.add(HeaderDocumentation.responseHeaders(
                HeaderDocumentation.headerWithName("Content-Type").description("application/json;charset=UTF-8")));
        snippets.add(PayloadDocumentation.responseFields(
                PayloadDocumentation.fieldWithPath("timestamp").type(JsonFieldType.STRING).description(
                        "Exact time when server responded."),
                PayloadDocumentation.fieldWithPath("status").type(JsonFieldType.STRING).description(
                        "Application status. Application may be in one of following states:\n\n"+
                                "- "+Status.WORKING+": Application is serving incoming requests. +\n")));

        if(credentials != null){
            snippets.add(HeaderDocumentation.requestHeaders(
                    HeaderDocumentation.headerWithName("Authorization").description("Basic auth credentials")));
        }

        RequestSpecification requestSpecification = RestAssured.given(documentationSpec)
                .filter(RestAssuredRestDocumentation.document(docSnippetName, snippets.toArray(new Snippet[snippets.size()])));
        if(credentials != null){
            requestSpecification = requestSpecification.header("Authorization", "Basic "+ credentials);
        }
        requestSpecification.when()
                    .get(methodPath)
                .then()
                    .statusCode(HttpStatus.OK.value());
    }
}
