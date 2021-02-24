package io.github.proxyprint.kitchen.models.printshops;

import io.github.proxyprint.kitchen.models.User;

import javax.persistence.*;

/**
 * Created by daniel on 18-04-2016.
 */
@Entity
@Table(name = "managers")
public class Manager extends User {

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "email", nullable = false)
    private String email;
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "printshop_id")
    private PrintShop printShop;

    public Manager() {
        super.addRole(User.Roles.ROLE_MANAGER.name());
    }

    public Manager(String username, String password, String name, String email) {
        super(username, password);
        super.addRole(User.Roles.ROLE_MANAGER.name());
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PrintShop getPrintShop() {
        return printShop;
    }

    public void setPrintShop(PrintShop printShop) {
        this.printShop = printShop;
    }

    @Override
    public String toString() {
        return "Manager{" + super.toString()
                + "name='" + name + '\''
                + ", email='" + email + '\''
                + ", printShopID='" + printShop.toString() + '\''
                + '}';
    }
}
