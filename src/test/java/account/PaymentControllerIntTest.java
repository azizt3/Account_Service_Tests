package account;

import account.authority.Role;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
//Load the application context, but use a random port as the web environment.
//Spins up a Tomcat embedded server
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class PaymentControllerIntTest {

    public static final String API_EMPL_PAYMENT = "/api/empl/payment";
    public static final String API_ACCT_PAYMENTS = "/api/acct/payments";
    public static final String NON_EXISTENT_USERNAME = "nonExistentUser@acme.com";
    public static final String ACCOUNTANT_USERNAME = "luffy@acme.com";
    public static final String ADMIN_USER = "tabbish.aziz@acme.com";
    public static final String BASIC_USER = "doffy@acme.com";
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postGres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    //Authentication & Authorization (HTTP403, HTP401)

    @Test
    void apiEmplPayment_notAuthenticated_throwHttp401() throws Exception{
        mockMvc.perform(get(API_EMPL_PAYMENT)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }
    @Test
    void apiAcctPayments_notAuthenticated_throwHttp401() throws Exception{
        mockMvc.perform(put(API_EMPL_PAYMENT)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser(username = ADMIN_USER, roles = {Role.ADMINISTRATOR})
    void apiEmplPayment_adminAuthorization_throwHttp403() throws Exception{
        mockMvc.perform(get(API_EMPL_PAYMENT)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = BASIC_USER, roles = {Role.USER})
    void apiAcctPayments_userNotAccountant_throwHttp403() throws Exception{
        mockMvc.perform(put(API_EMPL_PAYMENT)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    //Invalid requests (HTTP400, HTTP404)
    //GET


    @Test
    @WithMockUser (username = ACCOUNTANT_USERNAME, roles = {Role.ACCOUNTANT})
    void apiAcctPayments_nonExistentPayPeriod_throwHttp404() throws Exception{}

    //PUT
    @Test
    @WithMockUser (username = ACCOUNTANT_USERNAME, roles = {Role.ACCOUNTANT})
    void apiAcctPayments_negativePayment_throwHttp400() throws Exception {}

    @Test
    @WithMockUser (username = ACCOUNTANT_USERNAME, roles = {Role.ACCOUNTANT})
    void apiAcctPayments_invalidDateFormat_throwHttp400() throws Exception {}

    @Test
    @WithMockUser (username = ACCOUNTANT_USERNAME, roles = {Role.ACCOUNTANT})
    void apiAcctPayments_nonExistentPayment_throwHttp404() throws Exception {}

    //POST
    @Test
    @WithMockUser (username = ACCOUNTANT_USERNAME, roles = {Role.ACCOUNTANT})
    void apiAcctPayments_repeatedPayment_throwHttp400() throws Exception {}

    //Valid requests (HTTP200)

    @Test
    @Rollback
    @WithMockUser (username = BASIC_USER, roles = {Role.USER})
    void apiEmplPayment_withPayPeriod_returnHttp200() throws Exception{
        String period = "01-2021";
        mockMvc.perform(get(API_EMPL_PAYMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(period))
            .andDo(print())
            .andExpect(status().isOk());
    }
    @Test
    @Rollback
    @WithMockUser (username = ACCOUNTANT_USERNAME, roles = {Role.ACCOUNTANT})
    void apiAcctPayments_updatingPayment_returnHttp200() throws Exception{}

    @Test
    @Rollback
    @WithMockUser (username = ACCOUNTANT_USERNAME, roles = {Role.ACCOUNTANT})
    void apiEmplPayment_withNoPayPeriod_returnHttp200() throws Exception{
        mockMvc.perform(get(API_EMPL_PAYMENT)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }

    //PUT
    @Test
    @Rollback
    @WithMockUser (username = ACCOUNTANT_USERNAME, roles = {Role.ACCOUNTANT})
    void apiEmplPayment_addingMultiplePayments_returnHttp200() throws Exception{}

}
