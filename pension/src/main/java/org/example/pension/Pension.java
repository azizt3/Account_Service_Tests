package org.example.pension;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.lang.annotation.Repeatable;
import java.time.LocalDateTime;

@Entity
@Table (name = "pension")
public class Pension {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name ="id")
    private Long id;

    @NotBlank
    @Column(name = "email")
    private String email;


    @NotBlank
    @Column(name ="pensionType")
    private String pensionType;



    @Column(name="balance")
    private Long balance;

    @Column(name = "pensionAge")
    private Long pensionAge;

    public Pension(){};
    public Pension(String email, String pensionType, Long balance, Long pensionAge){
        this.email = email;
        this.pensionType = pensionType;
        this.balance = balance;
        this.pensionAge = pensionAge;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPensionType() {
        return pensionType;
    }

    public void setPensionType(String pensionType) {
        this.pensionType = pensionType;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long getPensionAge() {
        return pensionAge;
    }

    public void setPensionAge(Long pensionAge) {
        this.pensionAge = pensionAge;
    }
}
