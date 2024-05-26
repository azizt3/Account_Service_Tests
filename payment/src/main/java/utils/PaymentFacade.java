package utils;


import org.springframework.stereotype.Component;

@Component
public class PaymentFacade {

    private static UserHelper userHelper;

    public boolean userExists(String email) {
        return userHelper.userExists(email);
    }

}
