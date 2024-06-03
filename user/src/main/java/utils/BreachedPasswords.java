package utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BreachedPasswords {

    private static List<String> breachedPasswords;

    static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(13);
    }

    BreachedPasswords(List<String> breachedPasswords) {
        List<String> passwords = List.of(
            "PasswordForJanuary",
            "PasswordForFebruary",
            "PasswordForMarch",
            "PasswordForApril",
            "PasswordForMay",
            "PasswordForJune",
            "PasswordForJuly",
            "PasswordForAugust",
            "PasswordForSeptember",
            "PasswordForOctober",
            "PasswordForNovember",
            "PasswordForDecember"
        );

        this.breachedPasswords = passwords.stream()
            .map(password -> passwordEncoder().encode(password))
            .toList();
    }

    public static boolean isBreached(String newPassword) {
        for (String pass : breachedPasswords) {
            if (passwordEncoder().matches(newPassword, pass))  return true;
        }
        return false;
    }
}
