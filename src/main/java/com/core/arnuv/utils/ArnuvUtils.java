package com.core.arnuv.utils;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.core.arnuv.model.Personadetalle;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.core.arnuv.constants.Constants.EARTH_RADIUS;

@Component
public class ArnuvUtils {

    public static <T> Collection convertirLista(List lsource, Class<T> destinationType, String... excludedProperties) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.getConfiguration().setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);

        if (excludedProperties != null) {
            List<String> exclusiones = Arrays.asList(excludedProperties);
            modelMapper.getConfiguration().setPropertyCondition(context ->
                    !exclusiones.contains(context.getMapping().getLastDestinationProperty().getName()));
        }

        return lsource.stream().map(o -> (T) modelMapper.map(o, destinationType)).toList();
    }

    public static <T> T convertirObjeto(Object source, Class<T> destinationType, String... excludedProperties) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.getConfiguration().setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);

        List<String> exclusiones = Arrays.asList(excludedProperties);
        modelMapper.getConfiguration().setPropertyCondition(context ->
                !exclusiones.contains(context.getMapping().getLastDestinationProperty().getName()));

        return modelMapper.map(source, destinationType);
    }

    public static double distance(double startLat, double startLong, double endLat, double endLong) {
        double dLat = Math.toRadians(endLat - startLat);
        double dLong = Math.toRadians(endLong - startLong);
        double startLatRad = Math.toRadians(startLat);
        double endLatRad = Math.toRadians(endLat);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLong / 2) * Math.sin(dLong / 2) * Math.cos(startLatRad) * Math.cos(endLatRad);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
    
    public static Personadetalle getUserInSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Personadetalle user = (Personadetalle) session.getAttribute("loggedInUser");
        return user;
    }

}
