package io.github.proxyprint.kitchen.models;

import javax.persistence.*;

/**
 * Created by daniel on 13-04-2016.
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@Table(name = "admin")
public class Admin extends User {

    @Column(name = "email", nullable = true)
    private String email;

    @Column(unique = true)
    private Money balance;

    public Admin() {
        super.addRole(User.Roles.ROLE_ADMIN.name());
        this.balance = new Money();
    }

    public Admin(String username, String password, String email) {
        super(username, password);
        super.addRole(User.Roles.ROLE_ADMIN.name());
        this.email = email;
        this.balance = new Money();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Admin{" + super.toString()
                + "email='" + email + '\''
                + '}';
    }

    public Money getBalance() { return balance; }

    public void setBalance(Money balance) { this.balance = balance; }
}
