package com.mz.example.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

/**
 * Methods for users will be CORS enabled with no password check. Access to admin methods should be checked with HTTP basic authentication.
 *
 * Fields defining methods paths should be public, static, final and should start with USER_ or ADMIN_ prefix.
 */
public final class APIRequestMapping {

    private static final Logger LOG = LoggerFactory.getLogger(APIRequestMapping.class);

    static final String BASE_PATH = "/api/";//TODO: change this to fit base path for your application API
    static final String BASE_ADMIN_PATH = BASE_PATH+"admin/";

    public static final String USER_STATUS_METHOD = BASE_PATH + "status";

    public static final String ADMIN_STATUS_METHOD = BASE_ADMIN_PATH + "status";

    public static String[] getUserMethods(){
        return getMethods("USER_.*");
    }

    public static String[] getAdminMethods(){
        return getMethods("ADMIN_.*");
    }

    private static String[] getMethods(String pattern){
        return Stream.of(APIRequestMapping.class.getDeclaredFields())
                .filter(field -> java.lang.reflect.Modifier.isStatic(field.getModifiers()))
                .filter(field -> java.lang.reflect.Modifier.isPublic(field.getModifiers()))
                .filter(field -> field.getName().matches(pattern))
                .map(field -> {
                    try {
                        return field.get(null).toString();
                    } catch (IllegalAccessException e) {
                        //should not happen as we read only public fields
                        LOG.error("Error while reading method paths.", e);
                        return null;
                    }
                })
                .filter(methodPath -> methodPath != null)
                .toArray(String[]::new);
    }
}
