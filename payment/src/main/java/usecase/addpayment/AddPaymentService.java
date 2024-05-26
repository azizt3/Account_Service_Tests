package usecase.addpayment;

import database.PaymentRepository;
import dto.PaymentPostedDto;
import dto.PensionContributionDto;
import entity.Payment;
import exceptions.InvalidPaymentException;
import exceptions.NotFoundException;
import exceptions.PaymentExistsException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;
import static utils.UserHelper.userExists;

@Service
public class AddPaymentService {

    PaymentRepository paymentRepository;
    WebClient webClient;

    @Autowired
    public AddPaymentService (PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
        this.webClient = WebClient.builder()
            .filter(basicAuthentication())
            .baseUrl("http://localhost:8080").build();
    }

    @Transactional
    public PaymentPostedDto postPayment (PaymentRequest payments) {
        validatePaymentAdd(payments);
        paymentRepository.save(new Payment(payments.employee(), payments.period(), payments.salary()));
        PensionContributionDto pensionContribution = handlePensionContribution(payments).block();
        return new PaymentPostedDto("Added Successfully", Long.toString(pensionContribution.balance()));
    }

    public Mono<PensionContributionDto> handlePensionContribution(PaymentRequest payment) {
        return webClient.put()
            .uri("/api/auth/pension/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payment)
            .retrieve()
            .bodyToMono(PensionContributionDto.class);
    }


    public void validatePaymentAdd(PaymentRequest payment){
        if (payment.salary() < 0) throw new InvalidPaymentException("Salary cannot be negative!");
        if (paymentRepository.existsByEmployeeAndPeriod(payment.employee(), payment.period())) {
            throw new PaymentExistsException("Cannot add duplicate payment");
        }
        if (userExists(payment.employee())) throw new NotFoundException("");
    }

}
