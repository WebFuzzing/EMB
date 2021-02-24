package se.devscout.scoutapi.util;

import org.junit.Test;

import java.io.File;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class MediaFileUtilsTest {

    @Test
    public void testIsResizedMediaFile() throws Exception {
        assertNull(MediaFileUtils.getOriginalMediaFile(new File("a.jpg")));
        assertNull(MediaFileUtils.getOriginalMediaFile(new File("a_100_100.jpg")));
        assertNull(MediaFileUtils.getOriginalMediaFile(new File("a_100.jpg")));
        assertNull(MediaFileUtils.getOriginalMediaFile(new File("a.jpg_a.jpg")));
        assertNull(MediaFileUtils.getOriginalMediaFile(new File("a.jpg_.jpg")));
        assertThat(MediaFileUtils.getOriginalMediaFile(new File("a.jpg_100.jpg")), is(new File("a.jpg")));
        assertThat(MediaFileUtils.getOriginalMediaFile(new File("a_100.jpg_100.jpg")), is(new File("a_100.jpg")));
    }
}