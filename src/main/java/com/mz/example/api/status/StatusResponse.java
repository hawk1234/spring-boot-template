package com.mz.example.api.status;

import java.util.Date;

public class StatusResponse {

    private final Date timestamp;
    private final Status status;

    public StatusResponse(Status status){
        timestamp = new Date();
        this.status = status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Status getStatus() {
        return status;
    }
}
