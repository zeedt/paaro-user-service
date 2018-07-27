package com.zeed.user.security;

import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

public class ApiRequestMatcher implements RequestMatcher {
    @Override
    public boolean matches(HttpServletRequest httpServletRequest) {
        String contextPath = httpServletRequest.getRequestURI();
        if(contextPath != null && contextPath.contains("/user/")){
            return true;
        }
        if(contextPath != null && contextPath.contains("/authority/")){
            return true;
        }
        return false;

    }
}
