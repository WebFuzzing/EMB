package nl.knaw.huygens.timbuctoo.model;

/*
 * #%L
 * Timbuctoo core
 * =======
 * Copyright (C) 2012 - 2015 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.common.base.Objects;
import nl.knaw.huygens.timbuctoo.model.Datable;
import nl.knaw.huygens.timbuctoo.model.EdtfPattern;
import org.junit.Assert;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DatableTest {

  private void assertDate(String expectedAsText, Date date) {
    if (date == null) {
      Assert.assertEquals("open", expectedAsText);
      return;
    }
    try {
      DateFormat format = new SimpleDateFormat("yyyy-MM-dd:HH");
      format.setTimeZone(Datable.CET);
      Date expected = format.parse(expectedAsText);
      Assert.assertEquals(expectedAsText, expected, date);
    } catch (ParseException e) {
      Assert.fail(e.getMessage());
    }
  }

  private void testDatable(String fromDate, String toDate, Datable.Certainty certainty, Datable datable) {
    assertDate(fromDate, datable.getFromDate());
    assertDate(toDate, datable.getToDate());
    Assert.assertEquals(certainty, datable.getCertainty());
  }

  @Test
  public void testTimeZone() {
    Assert.assertEquals(60 * 60 * 1000, Datable.CET.getRawOffset());
  }

  @Test
  public void convertYear() {
    Assert.assertTrue(EdtfPattern.YEAR.matches("2010"));
    Assert.assertFalse(EdtfPattern.YEAR.matches("201?"));
    Assert.assertFalse(EdtfPattern.YEAR.matches("2010-02-13"));
    testDatable("2010-01-01:12", "2010-12-31:12", Datable.Certainty.HIGH, new Datable("2010"));
  }

  @Test
  public void convertYear_q() {
    Assert.assertTrue(EdtfPattern.YEAR_Q.matches("2010?"));
    Assert.assertFalse(EdtfPattern.YEAR_Q.matches("2010"));
    Assert.assertFalse(EdtfPattern.YEAR_Q.matches("2010-02-13?"));
    testDatable("2010-01-01:12", "2010-12-31:12", Datable.Certainty.LOW, new Datable("2010?"));
  }

  @Test
  public void convertYear_a() {
    Assert.assertTrue(EdtfPattern.YEAR_A.matches("2010~"));
    Assert.assertFalse(EdtfPattern.YEAR_A.matches("2010"));
    Assert.assertFalse(EdtfPattern.YEAR_A.matches("2010-02-13~"));
    testDatable("2010-01-01:12", "2010-12-31:12", Datable.Certainty.MEDIUM, new Datable("2010~"));
  }

  @Test
  public void convertYear_range_q1() {
    Assert.assertTrue(EdtfPattern.YEAR_RANGE_Q1.matches("201?"));
    Assert.assertFalse(EdtfPattern.YEAR_RANGE_Q1.matches("2010"));
    Assert.assertFalse(EdtfPattern.YEAR_RANGE_Q1.matches("201?-02-13"));
    testDatable("2010-01-01:12", "2019-12-31:12", Datable.Certainty.LOW, new Datable("201?"));
  }

  @Test
  public void convertYear_range_q2() {
    Assert.assertTrue(EdtfPattern.YEAR_RANGE_Q2.matches("20??"));
    Assert.assertFalse(EdtfPattern.YEAR_RANGE_Q2.matches("2010"));
    Assert.assertFalse(EdtfPattern.YEAR_RANGE_Q2.matches("20??-02-13"));
    testDatable("2000-01-01:12", "2099-12-31:12", Datable.Certainty.LOW, new Datable("20??"));
  }

  @Test
  public void convertYear_range_q3() {
    Assert.assertTrue(EdtfPattern.YEAR_RANGE_Q3.matches("2???"));
    Assert.assertFalse(EdtfPattern.YEAR_RANGE_Q3.matches("2010"));
    Assert.assertFalse(EdtfPattern.YEAR_RANGE_Q3.matches("2???-02-13"));
    testDatable("2000-01-01:12", "2999-12-31:12", Datable.Certainty.LOW, new Datable("2???"));
  }

  @Test
  public void convertYear_month() {
    Assert.assertTrue(EdtfPattern.YEAR_MONTH.matches("2001-01"));
    Assert.assertFalse(EdtfPattern.YEAR_MONTH.matches("2010"));
    Assert.assertFalse(EdtfPattern.YEAR_MONTH.matches("2???-02-13"));
    testDatable("2010-02-01:12", "2010-02-28:12", Datable.Certainty.HIGH, new Datable("2010-02"));
    testDatable("1610-12-01:12", "1610-12-31:12", Datable.Certainty.HIGH, new Datable("1610-12"));
  }

  @Test
  public void convertYear_month_q() {
    Assert.assertTrue(EdtfPattern.YEAR_MONTH_Q.matches("2001-01?"));
    Assert.assertFalse(EdtfPattern.YEAR_MONTH_Q.matches("2010"));
    Assert.assertFalse(EdtfPattern.YEAR_MONTH_Q.matches("2???-02-13"));
    testDatable("2010-03-01:12", "2010-03-31:12", Datable.Certainty.LOW, new Datable("2010-03?"));
  }

  @Test
  public void convertYear_month_a() {
    Assert.assertTrue(EdtfPattern.YEAR_MONTH_A.matches("2001-01~"));
    Assert.assertFalse(EdtfPattern.YEAR_MONTH_A.matches("2010"));
    Assert.assertFalse(EdtfPattern.YEAR_MONTH_A.matches("2???-02-13"));
    testDatable("1000-05-01:12", "1000-05-31:12", Datable.Certainty.MEDIUM, new Datable("1000-05~"));
  }

  @Test
  public void convertYear_month_range() {
    Assert.assertTrue(EdtfPattern.YEAR_MONTH_RANGE.matches("2010-??"));
    Assert.assertFalse(EdtfPattern.YEAR_MONTH_RANGE.matches("201?"));
    Assert.assertFalse(EdtfPattern.YEAR_MONTH_RANGE.matches("2010-02-13"));
    testDatable("2010-01-01:12", "2010-12-31:12", Datable.Certainty.MEDIUM, new Datable("2010-??"));
  }

  @Test
  public void convertDay_month_year() {
    Assert.assertTrue(EdtfPattern.DAY_MONTH_YEAR.matches("06-03-1854"));
    Assert.assertTrue(EdtfPattern.DAY_MONTH_YEAR.matches("6-3-1854"));
    testDatable("1854-03-06:12", "1854-03-06:12", Datable.Certainty.HIGH, new Datable("06-03-1854"));
  }

  @Test
  public void convertMonth_year() {
    Assert.assertTrue(EdtfPattern.MONTH_YEAR_RX.matches("03-1854"));
    Assert.assertTrue(EdtfPattern.MONTH_YEAR_RX.matches("3-1854"));
    testDatable("1854-03-01:12", "1854-03-31:12", Datable.Certainty.MEDIUM, new Datable("03-1854"));
  }

  @Test
  public void convertYear_month_day_h() {
    String edtf = "1648-05-15";
    Assert.assertTrue(EdtfPattern.YEAR_MONTH_DAY_H.matches(edtf));
    testDatable("1648-05-15:12", "1648-05-15:12", Datable.Certainty.HIGH, new Datable(edtf));
  }

  @Test
  public void convertYear_month_day() {
    String edtf = "16480515";
    Assert.assertTrue(EdtfPattern.YEAR_MONTH_DAY.matches(edtf));
    testDatable("1648-05-15:12", "1648-05-15:12", Datable.Certainty.HIGH, new Datable(edtf));
  }

  @Test
  public void convertYear_month_day_hq() {
    String edtf = "1648-05-15?";
    Assert.assertTrue(EdtfPattern.YEAR_MONTH_DAY_HQ.matches(edtf));
    testDatable("1648-05-15:12", "1648-05-15:12", Datable.Certainty.LOW, new Datable(edtf));
  }

  @Test
  public void convertYear_month_day_q() {
    String edtf = "16480515?";
    Assert.assertTrue(EdtfPattern.YEAR_MONTH_DAY_Q.matches(edtf));
    testDatable("1648-05-15:12", "1648-05-15:12", Datable.Certainty.LOW, new Datable(edtf));
  }

  @Test
  public void convertYear_month_day_ha() {
    String edtf = "1648-05-15~";
    Assert.assertTrue(EdtfPattern.YEAR_MONTH_DAY_HA.matches(edtf));
    testDatable("1648-05-15:12", "1648-05-15:12", Datable.Certainty.MEDIUM, new Datable(edtf));
  }

  @Test
  public void convertYear_month_day_a() {
    String edtf = "16480515~";
    Assert.assertTrue(EdtfPattern.YEAR_MONTH_DAY_A.matches(edtf));
    testDatable("1648-05-15:12", "1648-05-15:12", Datable.Certainty.MEDIUM, new Datable(edtf));
  }

  @Test
  public void testYearConversion() throws ParseException {
    Datable datable = new Datable("1709/1710");
    Assert.assertEquals(1709, datable.getFromYear());
    Assert.assertEquals(1710, datable.getToYear());
  }

  @Test
  public void testEqualsEqual() {
    Datable first = new Datable("20131011");
    Datable second = new Datable("20131011");

    assertTrue(first.equals(second));
  }

  @Test
  public void testEqualsNotEqual() {
    Datable first = new Datable("20131011");
    Datable second = new Datable("20131012");

    assertFalse(first.equals(second));
  }

  @Test
  public void testHashCode() {
    String dateString = "20131011";
    assertEquals(Objects.hashCode(dateString), new Datable(dateString).hashCode());
  }

  @Test
  public void testCompareToEqualSameEdtf() {
    Datable first = new Datable("20131011");
    Datable second = new Datable("20131011");

    assertEquals(0, first.compareTo(second));
  }

  @Test
  public void testCompareToSmaller() {
    Datable first = new Datable("20131010");
    Datable second = new Datable("20131011");

    assertEquals(-1, first.compareTo(second));
  }

  @Test
  public void testCompareToBigger() {
    Datable first = new Datable("20131011");
    Datable second = new Datable("20131010");

    assertEquals(1, first.compareTo(second));
  }

  @Test
  public void testGetUpperLimit() {
    Datable datable = new Datable("2013");

    assertThat(datable.getUpperLimit(), equalTo(getStringAsObject("20131231")));
  }

  private Object getStringAsObject(String string) {
    return string;
  }

  @Test
  public void testGetUpperLimitNoEndDateSpecified() {
    Datable rangeWithoutEndDate = new Datable("2013/open");
    assertThat(rangeWithoutEndDate.getUpperLimit(), is(nullValue()));
  }

  @Test
  public void testGetLowerLimit() {
    Datable datable = new Datable("2013");

    assertThat(datable.getLowerLimit(), equalTo(getStringAsObject("20130101")));
  }

  @Test
  public void testGetLowerLimitNoFromDateSpecified() {
    Datable rangeWithoutFromDate = new Datable("open/2013");
    assertThat(rangeWithoutFromDate.getLowerLimit(), is(nullValue()));
  }

  @Test
  public void testIsValidRange() {
    assertThat(new Datable("2013").isValid(), is(true));
  }

  @Test
  public void testIsValidRangeWithUnknownPattern() {
    Datable rangeWithoutUnknown = new Datable("unknown");
    assertThat(rangeWithoutUnknown.isValid(), is(false));
  }

}
