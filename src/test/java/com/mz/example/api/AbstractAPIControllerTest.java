package com.mz.example.api;

import com.mz.example.AbstractApplicationTest;
import com.mz.example.config.ApplicationConfig;
import io.restassured.RestAssured;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Base64;

public abstract class AbstractAPIControllerTest extends AbstractApplicationTest {

    @Autowired
    protected ApplicationConfig config;

    @Before
    public void setup(){
        RestAssured.port = getServerPort();
    }

    protected String getUserAuthorizationToken(){
        return Base64.getEncoder().encodeToString(("user:"+config.getUserpass()).getBytes());
    }

    protected String getAdminAuthorizationToken(){
        return Base64.getEncoder().encodeToString(("admin:"+config.getAdminpass()).getBytes());
    }
}
