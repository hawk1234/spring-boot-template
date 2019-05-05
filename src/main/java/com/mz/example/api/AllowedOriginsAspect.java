package com.mz.example.api;

import com.mz.example.api.exceptions.AccessForbiddenException;
import com.mz.example.config.ApplicationConfig;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Checks request origin.
 * 1. Don't allow requests with origin different than allowed or not specified
 * 2. Don't perform this check if allowed origins not specified
 *
 * NOTE: Requests still can be easily forged to simulate expected origin by other servers.
 * But this check will give small layer of protection when trying to access API without Origin
 * header (automatically added by browsers).
 *
 * TODO: If not allowed Origin passed in header DefaultCorsProcessor handles error. How to override DefaultCorsProcessor behaviour?
 */
@Aspect
public class AllowedOriginsAspect {

    private static final String ORIGIN_HEADER = "Origin";

    @Autowired
    private ApplicationConfig config;

    @Pointcut("@annotation(com.mz.example.api.AllowedOriginsAspect.CheckAllowedOrigins)")
    public void checkedMethods() {}

    @Before("checkedMethods()")
    public void checkBefore(JoinPoint joinPoint) {
        HttpHeaders httpHeaders = validateArguments(joinPoint);
        if(!config.hasDefinedOrigins()
                || config.getAllowedOrigins().contains(CorsConfiguration.ALL)){
            return;
        }
        String origin = httpHeaders.getOrigin();
        if(origin == null || !config.getAllowedOrigins().contains(origin)){
            throw new AccessForbiddenException();
        }
    }

    private HttpHeaders validateArguments(JoinPoint joinPoint){
        Optional<HttpHeaders> httpHeaders = Stream.of(joinPoint.getArgs())
                .filter(arg -> arg != null && HttpHeaders.class.isAssignableFrom(arg.getClass()))
                .map(HttpHeaders.class::cast)
                .findFirst();
        if(!httpHeaders.isPresent()){
            throw new IllegalStateException("Methods annotated with "+CheckAllowedOrigins.class+" must declare "+HttpHeaders.class.getName()+" argument.");
        }
        return httpHeaders.get();
    }

    @Target(value = ElementType.METHOD)
    public @interface CheckAllowedOrigins{}
}
