package io.github.proxyprint.kitchen.models.printshops;

import io.github.proxyprint.kitchen.models.User;

import javax.persistence.*;

/**
 * Created by daniel on 09-04-2016.
 */
@Entity
@Table(name = "employees")
public class Employee extends User {

    @Column(name = "name", nullable = false)
    private String name;
    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "printshop_id")
    private PrintShop printShop;

    public Employee() {
        super.addRole(User.Roles.ROLE_EMPLOYEE.name());
    }

    public Employee(String username, String password, String name, PrintShop pshop) {
        super(username, password);
        super.addRole(User.Roles.ROLE_EMPLOYEE.name());
        this.name = name;
        this.printShop = pshop;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PrintShop getPrintShop() { return printShop; }

    public void setPrintShop(PrintShop printShop) { this.printShop = printShop; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Employee{");
        sb.append("name='" + name + '\'');
        sb.append("username='" + username + '\'');
        sb.append("password='" + password + '\'');
        if(printShop!=null) {
            sb.append(", printShop=" + printShop.getName());
        }

        return sb.toString();
    }
}
