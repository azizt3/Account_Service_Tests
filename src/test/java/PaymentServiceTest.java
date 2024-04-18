import account.businesslayer.PaymentService;
import account.businesslayer.UserService;
import account.businesslayer.dto.PaymentDto;
import account.businesslayer.dto.UserAdapter;
import account.businesslayer.dto.UserDto;
import account.businesslayer.entity.Payment;
import account.businesslayer.entity.User;
import account.persistencelayer.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.print.DocFlavor;
import java.text.ParseException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    public PaymentService paymentService;

    @Mock
    public PaymentRepository paymentRepository;

    @Mock
    public UserService userService;

    @BeforeEach
    void setUp() { paymentService = new PaymentService(paymentRepository, userService);
    }


    UserDto userA = new UserDto(
            1L,
            "tabbish",
            "aziz",
            "tabbish.aziz@acme.com",
            null
    );

    UserDto userB = new UserDto(
            2L,
            "Monkey",
            "D-Luffy",
            "luffy@acme.com",
            null
    );

    UserDto userC = new UserDto(
            3L,
            "donquixote",
            "doflamingo",
            "doffy@acme.com",
            null
    );

    User user1 = new User(1L, userA.name(), userA.lastname(), userA.email(), "Canada2024!!", null);
    User user2 = new User(2L, userB.name(), userB.lastname(), userC.email(), "Laughtale2024!!", null );
    User user3 = new User(3L, userC.name(), userC.lastname(), userC.email(), "Dressrosa2024!!", null);


    //public PaymentDto handleGetPayment(String period, UserAdapter user) throws ParseException {

    /*@Test
    void givenUserDetailsAndPeriod_whenGettingPayments_thenVerifyUserAndPaymentRepoInvokedOnce() throws Exception {
        String period = "01-2021";
        UserAdapter user = new UserAdapter(user1, null );
        Payment payment = new Payment("luffy@acme.com", "01-2021", 123456L);

        doNothing().when(userService).validateUserExists(user.getEmail());
        when(paymentRepository.findByEmployeeAndPeriod(user.getEmail(), period)).thenReturn(Optional.of(payment));
        PaymentDto actualPayment = paymentService.handleGetPayment(period, user);
        verify(paymentRepository, times(1))
                .findByEmployeeAndPeriod(any(String.class), any(String.class));
    }*/

    /*@Test
    void givenUserDetailsAndPeriod_whenGettingPayments_thenReturnPaymentDetails() throws Exception{
        String period = "01-2021";
        UserAdapter user = new UserAdapter(user2, null );
        Payment payment = new Payment("luffy@acme.com", "01-2021", 123456L);

        doNothing().when(userService).validateUserExists(user.getEmail());
        when(paymentRepository.findByEmployeeAndPeriod(user.getEmail(), period)).thenReturn(Optional.of(payment));
        PaymentDto actualPayment = paymentService.handleGetPayment(period, user);

        assertEquals(user.getName(), actualPayment.name());
        assertEquals(user.getLastName(), actualPayment.lastname());
        assertEquals("January-2021", actualPayment.period());
        assertEquals("1234 dollar(s) 56 cent(s)", actualPayment.salary());
    }*/

    @Test
    void givenNonExistentUserDetailsAndPeriod_whenValidatingUserExists_thenThrowException(){}

    @Test
    void givenNonUserDetailsAndNonExistentPayPeriod_whenGettingPayments_thenThrowException(){}

    //public PaymentDto[] handleGetAllPayments(UserAdapter user) throws ParseException{

    @Test
    void givenUserDetailsOnly_whenGettingPayments_thenVerifyUserAndPaymentRepoInvokedOnce(){}

    @Test
    void givenUserDetailsOnly_whenGettingPayments_thenReturnPaymentDetailsForAllPayments(){}

    @Test
    void givenNonExistentUserDetails_whenValidatingUserExists_thenThrowException(){}


    //public ResponseEntity<PaymentPostedDto> updatePayment (PaymentRequest payment)
    @Test
    void givenPaymentRequest_whenUpdatingPayments_thenVerifyUserAndPaymentRepoInvokedOnce(){}

    @Test
    void givenPaymentRequest_whenUpdatingPayments_thenNotifyUserOfChange(){}

    @Test
    void givenPaymentRequestWithNonExistentUserEmail_whenValidatingUserExists_thenThrowException(){}

    @Test
    void givenPaymentRequestWithNegativePay_whenValidatingPaymentUpdateRequest_thenThrowException(){}

    @Test
    void givenPaymentRequestWithNonExistentPayPeriod_whenValidatingPaymentRequest_thenThrowException(){}

    //public PaymentPostedDto postPayment (PaymentRequest payments)

    @Test
    void givenPaymentRequest_whenUpdatingPayment_thenNotifyUserOfChange(){}

    @Test
    void givenPaymentRequest_whenUpdatingPayment_thenVerifyPaymentPosted(){}

    @Test
    void givenPaymentRequestWithNegativePay_whenValidatingPaymentAddRequest_thenThrowException(){}

    @Test
    void givenNonUniquePaymentRequest_whenValidatingPaymentAddRequest_thenThrowException(){}




}
