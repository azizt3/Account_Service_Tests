package usecase.getpayment;

import database.PaymentRepository;
import dto.PaymentDto;
import dto.UserDto;
import entity.Payment;
import exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.List;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;
import static utils.PaymentHelper.formatPeriod;
import static utils.PaymentHelper.formatSalary;

@Service
public class GetPaymentService {

    PaymentRepository paymentRepository;
    WebClient webClient;

    @Autowired
    public GetPaymentService (PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
        this.webClient = WebClient.builder()
            .filter(basicAuthentication())
            .baseUrl("http://localhost:8080").build();
    }

    protected PaymentDto handleGetPayment(String period) throws ParseException {
        String username = getAuthenticatedUser();
        Payment payment = paymentRepository.findByEmployeeAndPeriod(username, period)
            .orElseThrow(() -> new NotFoundException("Payment does not exist for this pay period"));
        UserDto user = getUserInfo(username).block();
        return buildPaymentDto(payment, user);
    }

    protected PaymentDto[] handleGetAllPayments() throws ParseException{
        String username = getAuthenticatedUser();
        List<Payment> allPayments = paymentRepository.findByEmployeeOrderByPeriodDesc(username);
        return allPayments.stream()
            .map(payment -> buildPaymentDto(payment, getUserInfo(payment.getEmployee()).block()))
            .toList().toArray(new PaymentDto[0]);
    }

     private PaymentDto buildPaymentDto(Payment payment, UserDto user) {
        return new PaymentDto(
            user.name(),
            user.lastname(),
            formatPeriod(payment.getPeriod()),
            formatSalary(payment.getSalary())
        );
    }

    private String getAuthenticatedUser() {
        return SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();
    }

    private Mono<UserDto> getUserInfo(String email) {
        return webClient.put()
            .uri("api/admin/user/{email}")
            .contentType(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(UserDto.class);
    }
}
