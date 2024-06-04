package usecase.updatepayment;

import database.PaymentRepository;
import dto.PaymentUpdatedDto;
import entity.Payment;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usecase.addpayment.PaymentRequest;

import static utils.PaymentHelper.findPayment;

@Service
public class UpdatePaymentService {

    PaymentRepository paymentRepository;


    @Autowired
    public UpdatePaymentService (PaymentRepository paymentRepository){
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentUpdatedDto handlePaymentUpdate(PaymentRequest paymentRequest) {
        Payment payment = findPayment(paymentRequest.employee(), paymentRequest.period());
        payment.updatePayment(paymentRequest.salary());
        paymentRepository.save(payment);
        return new PaymentUpdatedDto("Added successfully!");
    }
}
