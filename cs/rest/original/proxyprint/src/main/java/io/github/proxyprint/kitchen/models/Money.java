package io.github.proxyprint.kitchen.models;

import javax.persistence.*;

/**
 * Created by daniel on 03-06-2016.
 */
@Embeddable
public class Money {
    private static int MONEY_CEIL = 100;

    @Column(name = "balance_currency", nullable = false)
    private String currency;

    @Column(name = "balance_integer_part", nullable = false)
    private int integerPart;

    @Column(name = "balance_fractional_part", nullable = false)
    private int fractionalPart;

    public Money() {
        this.currency="EUR";
        this.integerPart=0;
        this.fractionalPart=0;
    }

    public Money(int integerPart, int fractionalPart) {
        this.currency="EUR";
        this.integerPart = integerPart;
        this.fractionalPart = fractionalPart;
    }

    public Money(String currency, int integerPart, int fractionalPart) {
        this.currency = currency;
        this.integerPart = integerPart;
        this.fractionalPart = fractionalPart;
    }

    public String getCurrency() { return currency; }

    public void setCurrency(String currency) { this.currency = currency; }

    public int getIntegerPart() { return integerPart; }

    public void setIntegerPart(int integerPart) { this.integerPart = integerPart; }

    public int getFractionalPart() { return fractionalPart; }

    public void setFractionalPart(int fractionalPart) { this.fractionalPart = fractionalPart; }

    public void addMoney(Money m) {
        this.integerPart += m.getIntegerPart();
        int tmp = this.fractionalPart + m.getFractionalPart();

        if(tmp >= MONEY_CEIL) {
            while(tmp >= MONEY_CEIL) {
                this.integerPart++;
                this.fractionalPart = tmp - MONEY_CEIL;
                tmp -= MONEY_CEIL;
            }
        } else {
            this.fractionalPart = tmp;
        }
    }

    public void subtractMoney(Money m) {
        this.integerPart -= m.getIntegerPart();
        int tmp = this.fractionalPart - m.getFractionalPart();

        if(tmp <= 0) {
            while(tmp <= 0) {
                this.integerPart--;
                this.fractionalPart = tmp + MONEY_CEIL;
                tmp += MONEY_CEIL;
            }
        } else {
            this.fractionalPart = tmp;
        }
    }

    public void addQuantity(int ip, int fp) {
        this.addMoney(new Money(ip,fp));
    }

    public void subtractQuantity(int ip, int fp) {
        this.subtractMoney(new Money(ip,fp));
    }

    public void addDoubleQuantity(double value) {
        value = round(value,2);
        String tmp[] = String.valueOf(value).split("\\.");
        this.addMoney(new Money(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1])));
    }

    public void subtractDoubleQuantity(double value) {
        value = round(value,2);
        String tmp[] = String.valueOf(value).split("\\.");
        this.subtractMoney(new Money(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1])));
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public String getMoneyAsString(String separator) {
        if(separator!=null) {
            return (this.integerPart+separator.trim()+this.fractionalPart);
        } else {
            return "";
        }
    }

    public double getMoneyAsDouble() {
        return Double.parseDouble(String.valueOf(this.integerPart)+"."+String.valueOf(this.getFractionalPart()));
    }

    public boolean isValid() {
        return (this.fractionalPart < MONEY_CEIL);
    }
}
