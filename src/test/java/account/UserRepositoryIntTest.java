package account;

import account.authority.Authority;
import account.user.User;
import account.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration
public class UserRepositoryIntTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postGres =new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    UserRepository userRepository;

    Authority admin = new Authority("ROLE_ADMINISTRATOR", "ADMINISTRATIVE");
    Authority accountant = new Authority("ROLE_ACCOUNTANT", "BUSINESS_USER");
    Authority user = new Authority("ROLE_USER", "BUSINESS_USER");
    Set<Authority> adminAuthority = Set.of(admin);
    Set<Authority> accountantAuthority = Set.of(accountant);
    Set<Authority> userAuthority = Set.of(user);
    List<User> users = List.of(
            new User("donquixote", "doflamingo", "doffy@acme.com", "Dressrosa2024!!", userAuthority),
            new User("Monkey", "D-Luffy", "luffy@acme.com", "Laughtale2024!!", accountantAuthority),
            new User("tabbish", "aziz", "tabbish.aziz@acme.com", "Canada2024!", adminAuthority)
    );

    @Test
    void connectionEstablished(){
        assertThat(postGres.isCreated()).isTrue();
        assertThat(postGres.isRunning()).isTrue();
    }

    @BeforeEach
    void setUp(){
        userRepository.saveAll(users);
    }

    @Test
    void shouldReturnUserByEmail(){
        User user = userRepository.findByEmail("luffy@acme.com")
                .orElseThrow();
        assertEquals("Monkey", user.getName());
        assertEquals("D-Luffy", user.getLastname());
        assertEquals("luffy@acme.com", user.getEmail());
        assertThat(user).isNotNull();
    }

    @Test
    void shouldReturnAllUsers(){
        List<User> persistedUsers = userRepository.findAll();
        assertEquals(users.get(0), persistedUsers.get(0));
        assertEquals(users.get(1), persistedUsers.get(1));
        assertEquals(users.get(2), persistedUsers.get(2));
    }
}
