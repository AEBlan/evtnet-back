package com.evtnet.evtnetback.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class RequestUtils {
    
    @Autowired
    private HttpServletRequest request;
    
    public String getFullRequestUrl() {
        return request.getRequestURL().toString();
    }
}