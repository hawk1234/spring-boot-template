package com.mz.example.api;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class APIErrorAttributes extends DefaultErrorAttributes{

    private static final String MESSAGE_UNAVAILABLE = "Message unavailable";

    private static final String[] ERROR_ATTRIBUTES = new String[] {
            "timestamp",
            "status",
            "error",
            "message",
            "path"
    };

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        Map<String, Object> attributes = super.getErrorAttributes(webRequest, includeStackTrace);
        return ensureErrorStructureIsStandardized(attributes);
    }

    private Map<String, Object> ensureErrorStructureIsStandardized(Map<String, Object> attributes){
        Map<String, Object> result = new LinkedHashMap<>();
        Arrays.stream(ERROR_ATTRIBUTES).forEach(errorAtt -> result.put(errorAtt, attributes.get(errorAtt)));

        //<editor-fold desc="HACK: This implementation relies on DefaultErrorAttributes private implementation and may differ depending on spring version.">
        if(HttpStatus.INTERNAL_SERVER_ERROR.value() == (Integer) result.get(ERROR_ATTRIBUTES[1])){
            result.put(ERROR_ATTRIBUTES[3], MESSAGE_UNAVAILABLE);//Do not expose messages for internal server errors.
        }

        //Extract exact reason for validation error and return it instead of general error.
        Object errors = attributes.get("errors");
        if(errors instanceof List && !((List) errors).isEmpty()){
            Object firstError = ((List) errors).get(0);
            if(firstError instanceof ObjectError){
                result.put(ERROR_ATTRIBUTES[3], ((ObjectError) firstError).getDefaultMessage());
            }
        }
        //</editor-fold>
        return result;
    }
}
