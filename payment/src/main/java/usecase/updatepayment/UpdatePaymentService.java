package usecase.updatepayment;

import database.PaymentRepository;
import dto.PaymentUpdatedDto;
import entity.Payment;
import exceptions.InvalidPaymentException;
import exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import usecase.addpayment.PaymentRequest;

@Service
public class UpdatePaymentService {

    PaymentRepository paymentRepository;

    public UpdatePaymentService (PaymentRepository paymentRepository){
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentUpdatedDto handlePaymentUpdate(PaymentRequest payment) {
        if (payment.salary() < 0) throw new InvalidPaymentException("Salary cannot be negative!");
        Payment updatedPayment = paymentRepository.findByEmployeeAndPeriod(payment.employee(), payment.period())
            .orElseThrow(() -> new NotFoundException("Payment does not exist for this pay period"));
        updatedPayment.setSalary(payment.salary());
        paymentRepository.save(updatedPayment);
        return new PaymentUpdatedDto("Added successfully!");
    }
}
