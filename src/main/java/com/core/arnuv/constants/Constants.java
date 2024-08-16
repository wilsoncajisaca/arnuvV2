package com.core.arnuv.constants;

import java.util.regex.Pattern;

public class Constants {

    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    public static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    public static final String KEY_RADIO = "RADIO";
    public static final String KEY_PLANTILLA_MAIL = "PLANTILLAMAIL";
    public static final String KEY_LINK_MAPA_GOOGLE = "LINKMAPAGOOGLE";
    public static final int EARTH_RADIUS = 6371;

    public static String[] getPermittedRoutes() {
        return new String[]{
                "/content/**",
                "/landing/**",
                "/auth/**",
                "/index",
                "/paseos/**",
                "/crear/**",
                "/"
        };
    }
}
