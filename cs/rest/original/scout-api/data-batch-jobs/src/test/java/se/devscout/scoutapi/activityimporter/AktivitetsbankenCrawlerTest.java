package se.devscout.scoutapi.activityimporter;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.jsoup.Jsoup;
import org.junit.Test;
import se.devscout.scoutapi.model.ActivityProperties;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class AktivitetsbankenCrawlerTest {

    private AktivitetsbankenCrawler crawler;

    @Test
    public void testReadAllActivities() throws Exception {
        WebPageLoader pageLoader = url -> Jsoup.parse(
                Resources.toString(
                        Resources.getResource("crawler/" + url.replaceAll("[^a-z0-9]", "")),
                        Charsets.UTF_8),
                Charsets.UTF_8.name());

        crawler = new AktivitetsbankenCrawler(pageLoader);

        List<ActivityProperties> actual = crawler.readAllActivities();

        assertForlangdaArmen(actual.get(0));
        assertLikaArbeteLikaLonStafett(actual.get(1));
        assertRottLjus(actual.get(2));
        assert100MeterEld(actual.get(3));
        assert20LiterVatten(actual.get(4));
    }

    private void assertForlangdaArmen(ActivityProperties properties) {
        // Extract activity name correctly
        assertThat(properties.getName(), is("\u201dF\u00f6rl\u00e4ngda armen\u201d-\u00f6vning"));

        // Get introduction
        assertTrue(properties.getDescriptionIntroduction().contains("Livr\u00e4ddning \u00e4r en livsviktig kunskap d\u00e5 du kan r\u00e4dda liv."));

        // Sub-header
        assertTrue(properties.getDescriptionMain().contains("## Inledande vad \u00e4r f\u00f6rl\u00e4ngda armen-\u00f6vning\n"));

        // Image without ALT inside DIV
        assertTrue(properties.getDescriptionMain().contains("![](http://www.scouterna.se/wikifiler/platt/Livsraddning.JPG)"));

        assertThat(properties.getAgeMin(), is(10));
        assertThat(properties.getAgeMax(), is(19));
        assertThat(properties.getParticipantsMin(), is(1));
        assertThat(properties.getParticipantsMax(), is(15));
        assertThat(properties.getTimeMin(), is(15));
        assertThat(properties.getTimeMax(), is(60));

        // Inlined formatting
        assertTrue(properties.getDescriptionMain().contains("F\u00f6rl\u00e4ngda armen \u00e4r ett livr\u00e4ddningsknep."));

        // Don't include introduction in main description
        assertFalse(properties.getDescriptionMain().contains("\u00d6vningarna kan ni g\u00f6ra i simhallen, som en badaktivitet p\u00e5 ett l\u00e4ger, i ishallen eller p\u00e5 frusen sj\u00f6is."));
    }

    private void assertLikaArbeteLikaLonStafett(ActivityProperties properties) {
        // Extract activity name correctly
        assertThat(properties.getName(), is("\u201dLika arbete \u2013 lika l\u00f6n?\u201d-stafett"));

        // Get introduction
        assertThat(properties.getDescriptionIntroduction(), is("I v\u00e4rlden \u00e4r de flesta kvinnol\u00f6ner betydligt l\u00e4gre \u00e4n mansl\u00f6nerna d\u00e5 b\u00e5da k\u00f6nen g\u00f6r exakt samma jobb. Detta \u00e4r inte j\u00e4mst\u00e4lldhet och i denna \u00f6vning f\u00e5r scouterna en chans att k\u00e4nna p\u00e5 det, oavsett k\u00f6n."));

        // Sub-header
        assertTrue(properties.getDescriptionMain().contains("## Reflektera\n"));

        // Bullet list
        assertTrue(properties.getDescriptionMain().contains("Till exempel:\n" +
                "\n" +
                "* Patrull 1: 4 knop\u00f6vningar och 2 chiffer\u00f6vningar.\n" +
                "* Patrull 2: 2 knop\u00f6vningar och 4 knop\u00f6vningar.\n" +
                "\n" +
                "N\u00e4r de har l\u00f6st uppgifterna f\u00e5r de ocks\u00e5 po\u00e4ng."));

        assertThat(properties.getAgeMin(), is(8));
        assertThat(properties.getAgeMax(), is(19));
        assertThat(properties.getParticipantsMin(), is(1));
        assertThat(properties.getParticipantsMax(), is(99));
        assertThat(properties.getTimeMin(), is(15));
        assertThat(properties.getTimeMax(), is(60));
    }

    private void assertRottLjus(ActivityProperties properties) {
        // Extract activity name correctly
        assertThat(properties.getName(), is("1, 2, 3 r\u00f6tt ljus"));

        // NO sub-headers
        assertFalse(properties.getDescriptionMain().contains("## "));

        // Safety instructions are found by looking for a string with accented (Swedish) characters.
        assertThat(properties.getDescriptionSafety(), is("S\u00e4g \u00e5t deltagarna att de m\u00e5ste vara f\u00f6rsiktiga n\u00e4r de kommer fram. detta f\u00f6r att inte den som st\u00e5r ska puttas h\u00e5rt och p\u00e5 s\u00e5 s\u00e4tt skada sig,"));

        assertThat(properties.getAgeMin(), is(8));
        assertThat(properties.getAgeMax(), is(10));
        assertThat(properties.getParticipantsMin(), is(1));
        assertThat(properties.getParticipantsMax(), is(15));
        assertThat(properties.getTimeMin(), is(5));
        assertThat(properties.getTimeMax(), is(15));
    }

    private void assert100MeterEld(ActivityProperties properties) {
        // Extract activity name correctly
        assertThat(properties.getName(), is("100 meter eld"));

        // Specific sub-header NOT in main description
        assertFalse(properties.getDescriptionMain().contains("## Scoutmetoden"));

        // Specific sub-headers in main description
        assertTrue(properties.getDescriptionNotes().contains("## Scoutmetoden"));
        assertTrue(properties.getDescriptionNotes().contains("* Ved"));
        assertTrue(properties.getDescriptionNotes().contains("## Aktiviteten \u00e4r gjord av"));
        assertTrue(properties.getDescriptionNotes().contains("Oskar Birkne"));

        assertThat(properties.getAgeMin(), is(12));
        assertThat(properties.getAgeMax(), is(19));
        assertThat(properties.getParticipantsMin(), is(1));
        assertThat(properties.getParticipantsMax(), is(15));
        assertThat(properties.getTimeMin(), is(30));
        assertThat(properties.getTimeMax(), is(180));
    }

    private void assert20LiterVatten(ActivityProperties properties) {
        // Extract activity name correctly
        assertThat(properties.getName(), is("20 liter vatten"));

        // Each numbered list item should have the number 1.
        assertTrue(properties.getDescriptionMain().contains("1. mat och dryck"));
        assertTrue(properties.getDescriptionMain().contains("1. toalettspolning"));
        assertTrue(properties.getDescriptionMain().contains("1. st\u00e4dning"));

        // Make sure the "Material" heading is recognized
        assertTrue(properties.getDescriptionMaterial().equals("6 hinkar, 6 skyltar, vattendunk/hink, 20 liter vatten, k\u00e5sa eller annat m\u00e5tt"));

        // Find sub-header
        assertTrue(properties.getDescriptionNotes().contains("## Referenser"));

        // Find links
        assertTrue(properties.getDescriptionNotes().contains("[http://www.nbv.se](http://www.nbv.se)"));

        assertThat(properties.getAgeMin(), is(10));
        assertThat(properties.getAgeMax(), is(25));
        assertThat(properties.getParticipantsMin(), is(8));
        assertThat(properties.getParticipantsMax(), is(15));
        assertThat(properties.getTimeMin(), is(15));
        assertThat(properties.getTimeMax(), is(30));
    }
}