package io.github.proxyprint.kitchen.models.printshops;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Comparator for sorting requests by date.
 * Created by daniel on 14-04-2016.
 */
public class RegisterRequestDateComparator implements Comparator<RegisterRequest> {

    @Override
    public int compare(RegisterRequest rr1, RegisterRequest rr2) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Date drr1, drr2 = null;
        try {
            drr1 = sdf.parse(rr1.getpShopDateRequest());
            drr2 = sdf.parse(rr2.getpShopDateRequest());
            int r = drr2.compareTo(drr1);
            if(r!=0) return r;
        } catch (ParseException e) { e.printStackTrace(); }
        return rr1.getpShopName().compareTo(rr2.getpShopName());
    }

}
