package com.yourapp.utils;

import org.springframework.context.ApplicationContext;

public class SpringContext {

    private static ApplicationContext context;

    public static void setContext(ApplicationContext ctx) {
        context = ctx;
    }

    public static ApplicationContext getContext() {
        return context;
    }
}
