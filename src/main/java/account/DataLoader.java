package account;

import account.authority.Role;
import account.authority.Authority;
import account.payment.Payment;
import account.user.User;
import account.authority.AuthorityRepository;
import account.payment.PaymentRepository;
import account.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataLoader {

    private AuthorityRepository authorityRepository;
    private UserRepository userRepository;
    private PaymentRepository paymentRepository;


    @Autowired
    public DataLoader (AuthorityRepository authorityRepository, UserRepository userRepository, PaymentRepository paymentRepository) {
        this.authorityRepository = authorityRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;

    }

    public void createAuthorities(){

            try{
                authorityRepository.save(new Authority(Role.ADMINISTRATOR, "ADMINISTRATIVE"));
                authorityRepository.save(new Authority(Role.ACCOUNTANT, "BUSINESS_USER"));
                authorityRepository.save(new Authority(Role.USER, "BUSINESS_USER"));
            } catch (Exception e) {}
    }
    public void createUsers(){
            try{
                userRepository.save(new User(
                        "tabbish",
                        "aziz",
                        "tabbish.aziz@acme.com",
                        new BCryptPasswordEncoder(13).encode("Canada2024!!"),
                    null
                ));
                userRepository.save(new User(
                        "Monkey",
                        "D-Luffy",
                        "luffy@acme.com",
                    new BCryptPasswordEncoder(13).encode("Laughtale2024!!"),
                    null
                ));
                userRepository.save(new User(
                        "donquixote",
                        "doflamingo",
                        "doffy@acme.com",
                    new BCryptPasswordEncoder(13).encode("Dressrosa2024!!"),
                    null
                ));
            } catch (Exception e) {}
    }

    public void createPayments(){
        try{
            paymentRepository.save(new Payment("tabbish.aziz@acme.com", "01-2021", 123456L));
            paymentRepository.save(new Payment("tabbish.aziz@acme.com", "02-2021", 123456L));
            paymentRepository.save(new Payment("tabbish.aziz@acme.com", "03-2021", 123456L));
            paymentRepository.save(new Payment("tabbish.aziz@acme.com", "04-2021", 123456L));
            paymentRepository.save(new Payment("tabbish.aziz@acme.com", "05-2021", 123456L));
            paymentRepository.save(new Payment("tabbish.aziz@acme.com", "06-2021", 123456L));
            paymentRepository.save(new Payment("tabbish.aziz@acme.com", "07-2021", 123456L));
            paymentRepository.save(new Payment("doffy@acme.com", "01-2021", 123456L));
            paymentRepository.save(new Payment("doffy@acme.com", "02-2021", 123456L));
            paymentRepository.save(new Payment("luffy@acme.com", "03-2021", 123456L));
            paymentRepository.save(new Payment("luffy@acme.com", "04-2021", 123456L));
            paymentRepository.save(new Payment("luffy@acme.com", "05-2021", 123456L));
        }

        catch(Exception e){}
    }

    public void assignAuthorities(){
        User adminUser = userRepository.findByEmail("tabbish.aziz@acme.com")
            .orElseThrow();

        User accountantUser = userRepository.findByEmail("luffy@acme.com")
            .orElseThrow();

        User user = userRepository.findByEmail("doffy@acme.com")
            .orElseThrow();

        Authority adminAuthority = authorityRepository.findByRole(Role.ADMINISTRATOR)
            .orElseThrow();


        Authority accountantAuthority = authorityRepository.findByRole(Role.ACCOUNTANT)
            .orElseThrow();

        Authority userAuthority = authorityRepository.findByRole(Role.USER)
            .orElseThrow();


        adminUser.setAuthorities(Set.of(adminAuthority));
        userRepository.save(adminUser);
        accountantUser.setAuthorities(Set.of(accountantAuthority));
        userRepository.save(accountantUser);
        user.setAuthorities(Set.of(userAuthority));
        userRepository.save(user);
    }

    @Bean
    public CommandLineRunner loadData(UserRepository userRepository)    {
        return (args) -> {
            createAuthorities();
            createUsers();
            createPayments();
            assignAuthorities();
        };
    }

    /*@Override
    public void run(String... args) throws Exception {
        createAuthorities();
        createUsers();
    }*/
}
