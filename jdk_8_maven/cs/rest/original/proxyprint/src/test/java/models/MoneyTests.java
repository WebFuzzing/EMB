package models;

import io.github.proxyprint.kitchen.models.Money;
import junit.framework.TestCase;

/**
 * Created by daniel on 03-06-2016.
 */
public class MoneyTests extends TestCase {
    protected double double1, double2;
    protected int ip, fp;

    protected void setUp(){
        double1 = 2.88;
        double2 = 3.73;
        fp = 444;
        ip = 2;
    }

    public void testAddDouble1(){
        Money m = new Money(2,11);
        m.addDoubleQuantity(double1);
        assertTrue(m.getIntegerPart()==4 && m.getFractionalPart()==99);
    }

    public void testAddDouble2(){
        Money m = new Money(4,99);
        m.addDoubleQuantity(double2);
        assertTrue(m.getIntegerPart()==8 && m.getFractionalPart()==72);
    }

    public void testSubtractDouble2(){
        Money m = new Money(4,99);
        m.subtractDoubleQuantity(double2);
        assertTrue(m.getIntegerPart()==1 && m.getFractionalPart()==26);
    }

    public void testAddInt() {
        Money m = new Money(3,33);
        m.addQuantity(ip,fp);
        assertTrue(m.getIntegerPart()==9 && m.getFractionalPart()==77);
    }

    public void testSubtractInt() {
        Money m = new Money(16,21);
        m.subtractQuantity(ip,fp);
        assertTrue(m.getIntegerPart()==9 && m.getFractionalPart()==77);
    }

    public void testAddZero() {
        Money m = new Money(3,00);
        m.addQuantity(ip,fp);
        assertTrue(m.getIntegerPart()==9 && m.getFractionalPart()==44);
    }

    public void testString() {
        Money m = new Money(3,1);
        m.addQuantity(ip,fp);
        assertTrue(m.getMoneyAsString(",").equals("9,45"));
        assertTrue(m.getMoneyAsString(".").equals("9.45"));
    }

    public void testDouble() {
        Money m = new Money(3,1);
        m.addQuantity(ip,fp);
        assertTrue(m.getMoneyAsDouble()==9.45);
    }
}
