package se.devscout.scoutapi.dao;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import se.devscout.scoutapi.model.ActivityPropertiesMediaFile;
import se.devscout.scoutapi.model.MediaFile;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class MediaFileDaoTest extends DaoTest {

    protected MediaFileDao dao;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        dao = new MediaFileDao(sessionFactory);
    }

    @Test
    public void testAll() throws Exception {
        Transaction tx = beginTransaction();

        List<MediaFile> mediaFiles = dao.all();

        assertNotNull(mediaFiles);
        assertFalse(mediaFiles.isEmpty());

        assertTrue(mediaFiles.stream().anyMatch(mediaFile -> "Albatros".equals(mediaFile.getName()) && "http://example.com/images/albatros.jpg".equals(mediaFile.getUri())));
        assertTrue(mediaFiles.stream().anyMatch(mediaFile -> "Bear".equals(mediaFile.getName()) && "image/png".equals(mediaFile.getMimeType())));
        tx.rollback();
    }

    @Test
    public void readMediaFileByURI() throws Exception {
        Transaction tx = beginTransaction();

        List<MediaFile> mediaFiles = dao.find("http://example.com/images/albatros.jpg");

        tx.rollback();

        assertThat(mediaFiles.size(), is(1));
        assertThat(mediaFiles.get(0).getName(), is("Albatros"));
    }

    @Test
    public void readMediaFileByKeyword() throws Exception {
        Transaction tx = beginTransaction();

        List<MediaFile> mediaFiles = dao.byKeyword("trunk");

        tx.rollback();

        assertThat(mediaFiles.size(), is(1));
        assertThat(mediaFiles.get(0).getName(), is("Elephant"));
    }

    @Test
    public void testCreate() throws Exception {
        assertFalse(isMediaFileSaved("Dog"));

        Transaction tx = beginTransaction();

        MediaFile mediaFile = new MediaFile("file:/photos/dog.jpg", "image/jpeg", "Dog");
        mediaFile.getKeywords().add("dog");
        mediaFile.getKeywords().add("mutt");
        mediaFile.getKeywords().add("hound");
        dao.create(mediaFile);

        tx.commit();

        Query query = verifySession.createQuery("SELECT t FROM MediaFile t WHERE t.name = :name");
        query.setString("name", "Dog");
        List<MediaFile> list = (List<MediaFile>) query.list();
        assertThat(list.size(), is(1));
        assertThat(list.get(0).getKeywords().size(), is(3));
    }

    @Test
    public void testUpdate() throws Exception {
        assertFalse(isMediaFileSaved("Funny Cat Poster"));
        assertTrue(isMediaFileSaved("Cat Poster"));

        Transaction tx = beginTransaction();

        MediaFile mediaFile = dao.read(3L);
        mediaFile.setName("Funny Cat Poster");
        dao.update(mediaFile);

        tx.commit();

        assertFalse(isMediaFileSaved("Cat Poster"));
        assertTrue(isMediaFileSaved("Funny Cat Poster"));
    }

    @Test
    public void testDeleteMediaFileWhichIsUsed() throws Exception {
        int mediaFileUsagesBefore = getEntityCount(ActivityPropertiesMediaFile.class);
        assertTrue(isMediaFileSaved("Elephant"));

        Transaction tx = beginTransaction();

        MediaFile carol = dao.read(4L);
        dao.delete(carol);

        tx.commit();

        assertFalse(isMediaFileSaved("Elephant"));

        int mediaFileUsagesAfter = getEntityCount(ActivityPropertiesMediaFile.class);
        assertThat(mediaFileUsagesAfter, is(mediaFileUsagesBefore - 1));
    }

    @Test
    public void testIsUsed_no_happyPath() throws Exception {
        Transaction tx = beginTransaction();
        assertFalse(dao.isUsed(1L));
        tx.rollback();
    }

    @Test
    public void testIsUsed_yes_happyPath() throws Exception {
        Transaction tx = beginTransaction();
        assertTrue(dao.isUsed(4L));
        tx.rollback();
    }

    private boolean isMediaFileSaved(String name) {
        Query query = verifySession.createQuery("SELECT t FROM MediaFile t WHERE name = :name");
        query.setString("name", name);
        return !query.list().isEmpty();
    }
}