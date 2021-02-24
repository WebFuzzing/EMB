package se.devscout.scoutapi.model;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MediaFileTest {

    @Test
    public void testKeywordSimplify1() throws Exception {
        String multipleWhiteSpacesAndAccentedCharacters = " \u00c5\u00c4\u00d6\u00e5\u00e4\u00f6   \u00fc\u00fb\u00fb\u00fc\n\t\n.\n";
        String expectedTrimmedLowerCaseAccentedCharacters = "\u00e5\u00e4\u00f6\u00e5\u00e4\u00f6 \u00fc\u00fb\u00fb\u00fc";
        assertThat(MediaFile.getSimplifiedKeyword(multipleWhiteSpacesAndAccentedCharacters), is(expectedTrimmedLowerCaseAccentedCharacters));
    }

    @Test
    public void testKeywordSimplify2() throws Exception {
        String input = "Ficklampsvett p\u00e5 scoutm\u00f6te -   .A";
        String expected = "ficklampsvett p\u00e5 scoutm\u00f6te a";
        String actual = MediaFile.getSimplifiedKeyword(input);
        assertThat(actual, is(expected));
    }

}