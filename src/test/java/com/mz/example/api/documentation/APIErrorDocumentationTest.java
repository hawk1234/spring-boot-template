package com.mz.example.api.documentation;

import com.mz.example.Application;
import com.mz.example.api.APIRequestMapping;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.RequestDispatcher;

//Separate configuration had to be created for documenting error structure. There are following problems with documenting errors using Spring REST docs:
//- MockMvc can't verify or document any fields or headers of the response as those are not available during documentation generation or regular validation.
//- RestAssured can verify HTTP response on error however, because Spring REST docs for RestAssured is separate library,
// this library is also unable to document any fields or headers of the response in case of any error.
//- I have used both MockMvc and RestAssured for testing because I found workaround on how I could document error structure for my API,
// however this solution doesn't work with RestAssured. And because RestAssured allows to better check error response in regular test
// it should be used as main testing framework.
//- The error structure documentation is divided into two tests one that generates example request and one that creates descriptions and example response.
// This two test are dependent and should be changed together. Of course this implementation doesn't rely on actual API methods and is unreliable,
// therefor additional manual checks may be necessary.
//Worth checking out some different frameworks for creating REST API documentation :)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@TestPropertySource(locations = "classpath:test.properties")
@WebAppConfiguration
public class APIErrorDocumentationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    protected MockMvc mockMvc;

    //documentation
    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation(System.getProperty("asciidoc_snippets"));

    @Before
    public final void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
                        .operationPreprocessors().withRequestDefaults(
                                Preprocessors.prettyPrint(),
                                Preprocessors.modifyUris().scheme("https")
                                        .host("api.example.host.com")
                                        .removePort()
                        ).withResponseDefaults(
                                Preprocessors.prettyPrint()
                        ))
                .build();
    }

    @Test
    public void testErrorRequestExampleDocumentation() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get(APIRequestMapping.ADMIN_STATUS_METHOD))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(MockMvcRestDocumentation.document("error request"));
    }

    @Test
    public void testErrorStructureDocumentation() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.UNAUTHORIZED.value())
                        .requestAttr(RequestDispatcher.ERROR_REQUEST_URI,
                                APIRequestMapping.ADMIN_STATUS_METHOD)
                        .requestAttr(RequestDispatcher.ERROR_MESSAGE,
                                "Unauthorized"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(MockMvcRestDocumentation.document("error structure",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()), Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        HeaderDocumentation.responseHeaders(
                                HeaderDocumentation.headerWithName("Content-Type").description("application/json;charset=UTF-8")),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("timestamp").type(JsonFieldType.STRING).description(
                                        "Time when error occurred"),
                                PayloadDocumentation.fieldWithPath("status").type(JsonFieldType.NUMBER).description(
                                        "HTTP error status"),
                                PayloadDocumentation.fieldWithPath("error").type(JsonFieldType.STRING).description(
                                        "HTTP error type"),
                                PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING).description(
                                        "A description of the cause of the error"),
                                PayloadDocumentation.fieldWithPath("path").type(JsonFieldType.STRING).description(
                                        "The path to which the request was made"))));
    }
}
