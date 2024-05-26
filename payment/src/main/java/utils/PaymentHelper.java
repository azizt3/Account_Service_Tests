package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentHelper {

    public static String formatSalary(Long cents) {
        Long change = cents%100;
        Long dollars = (cents - change)/100;
        return dollars + " dollar(s) " + change + " cent(s)";
    }

    public static String formatPeriod (String period) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("MM-yyyy");
        SimpleDateFormat targetFormat = new SimpleDateFormat("MMMM-yyyy");
        try {
            Date date = originalFormat.parse(period);
            return targetFormat.format(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
