package com.mz.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;
import java.util.List;

@ConfigurationProperties(prefix = "com.mz.example")//TODO: change this to your package - here and in application.properties
@Validated
public class ApplicationConfig {

    private static final String CONTAINS_AT_LEAST_ONE_LETTER_AND_ONE_NUMBER = "([A-Za-z]+[0-9]|[0-9]+[A-Za-z])[A-Za-z0-9]*";

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationConfig.class);

    @NotNull
    @Size(min=8)
    @Pattern(regexp = CONTAINS_AT_LEAST_ONE_LETTER_AND_ONE_NUMBER)
    private String adminpass;
    @NotNull
    @Size(min=8)
    @Pattern(regexp = CONTAINS_AT_LEAST_ONE_LETTER_AND_ONE_NUMBER)
    private String userpass;

    public String getAdminpass() {
        return adminpass;
    }

    public String getUserpass() {
        return userpass;
    }

    public void setAdminpass(String adminpass) {
        this.adminpass = adminpass;
    }

    public void setUserpass(String userpass) {
        this.userpass = userpass;
    }

    private List<String> allowedOrigins;

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public boolean hasDefinedOrigins(){
        return allowedOrigins != null && !allowedOrigins.isEmpty();
    }

    public List<String> getAllowedOrigins(){
        return allowedOrigins;
    }

    ApplicationConfig(){}
}
