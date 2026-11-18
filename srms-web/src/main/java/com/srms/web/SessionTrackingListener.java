package com.srms.web;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.atomic.AtomicInteger;

/** Counts active HttpSessions; the number is shown on the dashboard. */
@WebListener
public class SessionTrackingListener implements HttpSessionListener {

    private static final AtomicInteger ACTIVE = new AtomicInteger();

    public static int activeSessions() { return ACTIVE.get(); }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        ACTIVE.incrementAndGet();
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        ACTIVE.updateAndGet(v -> Math.max(0, v - 1));
    }
}
