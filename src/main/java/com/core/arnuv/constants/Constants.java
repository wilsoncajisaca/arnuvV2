package com.core.arnuv.constants;

import java.util.regex.Pattern;

public class Constants {

    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    public static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static final int EARTH_RADIUS = 6371;

    public static String[] getPermittedRoutes() {
        //TODO: REMOVE IMG FROM ROUTES
        return new String[]{
                "/content/**",
                "/img/**",
                "/landing/**",
                "/auth/**",
                "/index",
                "/"
        };
    }
}
