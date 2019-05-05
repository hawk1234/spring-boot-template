package com.mz.example.api;

import com.mz.example.api.status.Status;
import com.mz.example.api.status.StatusResponse;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Timed
public class APIController {
    //TODO: 415 Unsupported Media Type - support charsets other then UTF-8 or restrict this API to accept only UTF-8 -
    // see: https://stackoverflow.com/questions/52973473/how-to-handle-character-encoding-in-spring-restcontroller

    private static final Logger LOG = LoggerFactory.getLogger(APIController.class);

    @AllowedOriginsAspect.CheckAllowedOrigins
    @RequestMapping(method = RequestMethod.GET, value = APIRequestMapping.USER_STATUS_METHOD)
    public StatusResponse handleStatus(@RequestHeader HttpHeaders headers){
        LOG.debug("Handling user status");

        return new StatusResponse(Status.WORKING);
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(method = RequestMethod.GET, value = APIRequestMapping.ADMIN_STATUS_METHOD)
    public StatusResponse handleAdminStatus() {
        LOG.debug("Handling admin status");

        return new StatusResponse(Status.WORKING);
    }
}
