package utils;

import database.PaymentRepository;
import entity.Payment;
import exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentHelper {

    private static PaymentRepository paymentRepository;

    @Autowired
    public PaymentHelper(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

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

    public static Payment findPayment(String employee, String period) {
        return paymentRepository.findByEmployeeAndPeriod(employee, period)
            .orElseThrow(() -> new NotFoundException("Payment does not exist for this pay period"));
    }
}
