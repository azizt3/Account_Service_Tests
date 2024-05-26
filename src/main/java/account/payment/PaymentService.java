package account.payment;

import account.exceptionhandling.InvalidPaymentException;
import account.exceptionhandling.NotFoundException;
import account.exceptionhandling.PaymentExistsException;
import account.payment.dto.PaymentDto;
import account.payment.dto.PaymentPostedDto;
import account.payment.dto.PaymentUpdatedDto;
import account.payment.dto.PensionContributionDto;
import account.payment.request.PaymentRequest;
import account.user.User;
import account.user.UserAdapter;
import account.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@Service
public class PaymentService {

    PaymentRepository paymentRepository;
    UserService userService;
    WebClient webClient;

    @Autowired
    public PaymentService (PaymentRepository paymentRepository, UserService userService) {
        this.paymentRepository = paymentRepository;
        this.userService = userService;
        this.webClient = WebClient.builder()
                .filter(basicAuthentication())
                .baseUrl("http://localhost:8081").build();
    }

    //Business logic
    public PaymentDto handleGetPayment(String period) throws ParseException {

        User user = getAuthenticatedUser();
        userService.validateUserExists(user.getEmail());
        Payment payment = paymentRepository.findByEmployeeAndPeriod(user.getEmail().toLowerCase(), period)
                .orElseThrow(() -> new NotFoundException("Payment does not exist for this pay period"));
        return buildPaymentDto(payment, new UserAdapter(user));
    }

    private User getAuthenticatedUser() {
        String userName = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();
        User user =  userService.loadUser((userName));
        return user;
    }

    public PaymentDto[] handleGetAllPayments() throws ParseException{
        User user = getAuthenticatedUser();
        userService.validateUserExists(user.getEmail());
        List<Payment> allPayments = paymentRepository.findByEmployeeOrderByPeriodDesc(user.getEmail().toLowerCase());
        return allPayments.stream()
                .map(payment -> buildPaymentDto(payment, new UserAdapter(user)))
                .toList().toArray(new PaymentDto[0]);
    }

    @Transactional
    public PaymentPostedDto postPayment (PaymentRequest payments) {
        validatePaymentAdd(payments);
        Payment payment = new Payment(payments.employee(), payments.period(), payments.salary());
        paymentRepository.save(payment);
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

    @Transactional
    public PaymentUpdatedDto handlePaymentUpdate(PaymentRequest payment) {
        userService.validateUserExists(payment.employee());
        validatePaymentPositive(payment.salary());
        Payment updatedPayment = paymentRepository.findByEmployeeAndPeriod(payment.employee(), payment.period())
                .orElseThrow(() -> new NotFoundException("Payment does not exist for this pay period"));
        updatedPayment.setSalary(payment.salary());
        paymentRepository.save(updatedPayment);
        return new PaymentUpdatedDto("Added successfully!");
    }

    public PaymentDto buildPaymentDto(Payment payment, UserAdapter user) {
        return new PaymentDto(
                user.getName(),
                user.getLastName(),
                formatPeriod(payment.getPeriod()),
                formatSalary(payment.getSalary())
        );
    }

    //Formatting Methods

    public String formatSalary(Long cents) {
        Long change = cents%100;
        Long dollars = (cents - change)/100;
        return dollars + " dollar(s) " + change + " cent(s)";
    }

    public String formatPeriod (String period) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("MM-yyyy");
        SimpleDateFormat targetFormat = new SimpleDateFormat("MMMM-yyyy");
        try {
            Date date = originalFormat.parse(period);
            return targetFormat.format(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    //Validation Methods

    public void validatePaymentAdd(PaymentRequest payment){
        validatePaymentPositive(payment.salary());
        validateUniquePayment(payment);
        userService.validateUserExists(payment.employee());
    }

    public void validateUniquePayment(PaymentRequest payment) {
        if (paymentRepository.existsByEmployeeAndPeriod(payment.employee(), payment.period())) {
            throw new PaymentExistsException("Cannot add duplicate payment");
        }
    }
    public void validatePaymentPositive(Long payment) {
        if (payment < 0) throw new InvalidPaymentException("Salary cannot be negative!");
    }
}
