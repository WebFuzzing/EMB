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

import java.util.regex.Pattern;

/**
 * Enumeration of patterns for recognizing the Extended Date/Time Format (EDTF).
 * See: <code>http://www.loc.gov/standards/datetime/examples.html</code>.
 */
public enum EdtfPattern {
  // year: 2004
  YEAR("^\\d{4}$"),
  // year, questionable: 2004?
  YEAR_Q("^\\d{4}\\?$"),
  // year, approximate: 2004~
  YEAR_A("^\\d{4}\\~$"),

  // some year between 2000 and 2009: 200?
  YEAR_RANGE_Q1("^\\d{3}\\?$"),
  // some year between 2000 and 2099: 20??
  YEAR_RANGE_Q2("^\\d{2}\\?{2}$"),
  // some year between 2000 and 2999: 2???
  YEAR_RANGE_Q3("^\\d{1}\\?{3}$"),

  // year month, hyphenated : 2004-06
  YEAR_MONTH("^\\d{4}-\\d{2}$"),
  // year month, hyphenated, questionable: 2004-06?
  YEAR_MONTH_Q("^\\d{4}-\\d{2}\\?$"),
  // year month, hyphenated, approximate: 2004-06~
  YEAR_MONTH_A("^\\d{4}-\\d{2}\\~$"),

  // some month in 2004: 2004-??
  YEAR_MONTH_RANGE("^\\d{4}-\\?{2}$"),

  // TODO: remove not an EDTF date
  // day month year, hyphenated (for backward compatibility): 11-6-2004
  DAY_MONTH_YEAR("^\\d{1,2}-\\d{1,2}-\\d{4}$"),
  // month year, hyphenated (for backward compatibility): 6-2004
  MONTH_YEAR_RX("^\\d{1,2}-\\d{4}$"),

  // year month day, hyphenated: 2004-06-11
  YEAR_MONTH_DAY_H("^\\d{4}-\\d{2}-\\d{2}$"),
  // year month day, hyphenated, questionable: 2004-06-11?
  YEAR_MONTH_DAY_HQ("^\\d{4}-\\d{2}-\\d{2}\\?$"),
  // year month day, hyphenated, approximate: 2004-06-11~
  YEAR_MONTH_DAY_HA("^\\d{4}-\\d{2}-\\d{2}\\~$"),

  // year month day, not hyphenated: 20040611
  YEAR_MONTH_DAY("^\\d{8}$"),
  // year month day, not hyphenated, questionable: 20040611?
  YEAR_MONTH_DAY_Q("^\\d{8}\\?$"),
  // year month day, not hyphenated, approximate: 20040611~
  YEAR_MONTH_DAY_A("^\\d{8}\\~$"),

  // Some day in the month 06/2004: 200406??
  // date and time: 20040611T121212
  // range, years: 2004/2006
  YEAR_RANGE("^\\d{4}/\\d{4}(\\?)?$"),
  // range, year-month/year-month: 2004-06/2006-08
  // range, unknown start: unknown/2006
  // range, unknown end: 2004/unknown
  // range, open end:2004/open
  // xs:date:
  // 2008-02-03 (February 3, 2008)
  // 10000-01-01 (January 1 of the year 10,000)
  // -10000-01-01 ( January 1 of the year 10,000 BC)
  // xs:dateTime: 2004-06-04T12:12:12
  // Time Zone:
  // 2004-01-01T10:10:10Z
  // 2004-01-01T10:10:10+01:00
  // BC date: -1000-01-01
  // More extensive support for ranges (added September 2009).
  // Previously, range support was limited to date and month,
  // thus you could express "January through March of 2007" (200701/200703),
  // but not "January 1 through March 3 of 2007". This is now supported
  // (20070101/20070303) and in addition, times, including timezone indicator,
  // may be included at either end of the range. So the following now validate:
  // range, year-month-day/year-month-day: 20050705/20050706
  // range, year-month-day-time/year-month-day-time: 20050705T0715/20050705T0720
  // range, time zone indicated: 20050705T0715-0500/20050705T0720-0500
  // range, with hyphens: 2005-07-05/2005-07-05
  HYPHENATED_RANGE("^\\d{4}-\\d{2}-\\d{2}/\\d{4}-\\d{2}-\\d{2}$"),
  ;
  // range, with hyphens and date: 2005-07-05T07:15-05:00/2005-07-05T07:20-05:00
  // Z for time zone: 2004-01-01T10:10:10Z/2004-01-01T10:10:10+01:00

  private final Pattern pattern;

  EdtfPattern(String regex) {
    pattern = Pattern.compile(regex);
  }

  public boolean matches(String text) {
    return pattern.matcher(text).matches();
  }

  public static EdtfPattern matchingPattern(String text) {
    for (EdtfPattern edtf : EdtfPattern.values()) {
      if (edtf.matches(text)) {
        return edtf;
      }
    }
    return null;
  }

  public static boolean isValidEdtf(String text) {
    return matchingPattern(text) != null;
  }

}
