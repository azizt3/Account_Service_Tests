package entity;

import com.google.common.collect.Sets;
import exceptions.ErrorMessage;
import exceptions.InsufficientPasswordException;
import exceptions.InvalidChangeException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users" )
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "name is blank")
    private String name;

    @Column(name = "lastname")
    @NotBlank(message = "last name is blank")
    private String lastname;

    @Column(name = "email")
    @NotBlank(message = "email is blank")
    @Email(message = "email address is invalid")
    @Pattern(regexp = "^(.+)@acme.com$")
    private String email;

    @Column(name = "password")
    @NotBlank(message = "password is blank")
    private String password;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
        name="user_authorities",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name ="authority_id"))
    private Set<Authority> authorities = new HashSet<>();

    public User() {
    }

    public User(String name, String lastname, String email, String password, Set<Authority> authorities) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public User(String name, String lastname, String email, String password, Authority authorities) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.authorities = Sets.newHashSet(authorities);
    }

    public User(Long id, String name, String lastname, String email, String password, Set<Authority> authorities) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public List<String> getRoles() {
        return this.authorities
            .stream()
            .map(authority -> authority.getRole())
            .sorted()
            .toList();
    }

    private void validatePasswordLength(String newPassword) {
        if (newPassword.length() < 12) {
            throw new InsufficientPasswordException(ErrorMessage.PASSWORD_TOO_SHORT);
        }
    }

    public void changePassword(String newPassword) {
        validatePasswordLength(newPassword);
        if (new BCryptPasswordEncoder().matches(newPassword, password)) {
            throw new InsufficientPasswordException(ErrorMessage.PASSWORD_NOT_UNIQUE);
        }
        this.password = new BCryptPasswordEncoder().encode(newPassword);
    }

    public void addAuthority(Authority newAuthority){
        validateNoRoleConflict(newAuthority);
        authorities.add(newAuthority);
    }

    private void validateNoRoleConflict(Authority newAuthority) {
        for (Authority authority:this.authorities) {
            if (!authority.getRoleGroup().equalsIgnoreCase(newAuthority.getRoleGroup()))
                throw new InvalidChangeException(ErrorMessage.CONFLICTING_ROLE_ASSIGNMENT);
        }
    }

    public void removeAuthority(Authority authorityToRemove){
        if (authorityToRemove.getRole().equalsIgnoreCase("administrator")) {
            throw new InvalidChangeException(ErrorMessage.REMOVING_ADMIN_ROLE);
        }
        if(this.authorities.contains(authorityToRemove)){
            throw new InvalidChangeException(ErrorMessage.REMOVING_UNASSIGNED_ROLE);
        }
        if (this.authorities.size() < 2) {
            throw new InvalidChangeException(ErrorMessage.REMOVING_ONLY_ROLE);
        }

        this.authorities = authorities.stream()
                .filter(authority -> !authority.equals(authorityToRemove))
                .collect(Collectors.toSet());
    }
}
