package account.businesslayer.dto;

import account.businesslayer.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserAdapter implements UserDetails {
    private User user;
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private Collection <? extends GrantedAuthority> authority;

    public UserAdapter() {
    }

    public UserAdapter(User user) {
        this.user = user;
        this.id = user.getId();
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.authority = getUserAuthorities(user);
    }

    public List<String> getRoles(User user) {
        return user.getAuthorities()
                .stream()
                .map(authority -> authority.getRole())
                .sorted()
                .toList();
    }

    private Collection<? extends GrantedAuthority> getUserAuthorities(User user) {
        return getRoles(user).stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authority;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Collection<? extends GrantedAuthority> getAuthority() {
        return authority;
    }

    public void setAuthority(Collection<? extends GrantedAuthority> authority) {
        this.authority = authority;
    }
}
