package io.github.proxyprint.kitchen.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author josesousa
 */
public class DistanceCalculator {

    public static double distance(double lat1, double lon1, double lat2, double lon2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;// convert to kilometers

        distance = Math.pow(distance, 2);

        // Round on 2 decimal places
        BigDecimal bd = new BigDecimal(distance);
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        return Math.sqrt(bd.doubleValue());
    }

}
