package usecase.addpayment;

import database.PaymentRepository;
import dto.PaymentPostedDto;
import dto.PensionContributionDto;
import entity.Payment;
import exceptions.NotFoundException;
import exceptions.PaymentExistsException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import utils.PaymentFacade;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@Service
public class AddPaymentService {

    PaymentRepository paymentRepository;
    WebClient webClient;
    PaymentFacade paymentFacade;

    @Autowired
    public AddPaymentService (PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
        this.webClient = WebClient.builder()
            .filter(basicAuthentication())
            .baseUrl("http://localhost:8080").build();
    }

    @Transactional
    public PaymentPostedDto postPayment (PaymentRequest payment) {
        validateUniquePayment(payment);
        if (!paymentFacade.userExists(payment.employee())) throw new NotFoundException("");

        paymentRepository.save(new Payment(payment.employee(), payment.period(), payment.salary()));
        PensionContributionDto pensionContribution = handlePensionContribution(payment).block();
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

    private void validateUniquePayment(PaymentRequest payment){
        if (paymentRepository.existsByEmployeeAndPeriod(payment.employee(), payment.period())) {
            throw new PaymentExistsException("Cannot add duplicate payment");
        }
    }
}
