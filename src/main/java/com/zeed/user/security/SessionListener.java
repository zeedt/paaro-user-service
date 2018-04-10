package com.zeed.user.security;

/**
 * Created by Felix.Ike on 10/25/15.
 */import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        event.getSession().setMaxInactiveInterval(1*60);

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
    }
}
