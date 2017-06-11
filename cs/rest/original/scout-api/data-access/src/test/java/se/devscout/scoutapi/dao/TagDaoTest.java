package se.devscout.scoutapi.dao;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import se.devscout.scoutapi.model.Tag;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/*
INSERT INTO TAG (ID, GROUP, NAME) VALUES (1, 'grp', 'Fun');
INSERT INTO TAG (ID, GROUP, NAME) VALUES (2, 'grp', 'Adventure');
INSERT INTO TAG (ID, GROUP, NAME) VALUES (3, 'grp', 'Relaxing');
INSERT INTO TAG (ID, GROUP, NAME) VALUES (4, 'location', 'Outdoors');
INSERT INTO TAG (ID, GROUP, NAME) VALUES (5, 'location', 'Indoors');
*/
public class TagDaoTest extends DaoTest {

    protected TagDao dao;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        dao = new TagDao(sessionFactory);
    }

    @Test
    public void testAll() throws Exception {
        Transaction tx = beginTransaction();

        List<Tag> tags = dao.all();

        assertNotNull(tags);
        assertFalse(tags.isEmpty());
        assertTrue(tags.stream().anyMatch(tag -> "grp".equals(tag.getGroup()) && "Fun".equals(tag.getName())));
        assertTrue(tags.stream().anyMatch(tag -> "location".equals(tag.getGroup()) && "Outdoors".equals(tag.getName())));

        // Verify that media file is fetched for tag.
        assertTrue(tags.stream().anyMatch(tag -> true &&
                "grp".equals(tag.getGroup()) &&
                "Fun".equals(tag.getName()) &&
                tag.getMediaFile().getName().equals("Cat Poster")));

        tx.rollback();
    }

    @Test
    public void testDerivedProperties() throws Exception {
        Transaction tx = beginTransaction();

        List<Tag> tags = dao.all();

        // Verify that tags assigned to non-published activity revisions are not counted (tag 1 has been assigned to two activity revisions but only one is published)
        assertTrue(tags.stream().anyMatch(tag -> tag.getId() == 1L && tag.getActivitiesCount() == 1L));

        // Verify that tags can be reported to have multiple activities assigned to them.
        assertTrue(tags.stream().anyMatch(tag -> tag.getId() == 2L && tag.getActivitiesCount() == 2L));

        // Verify that tags assigned to non-published activity revisions are not counted (tag 5 has only been assigned to an unpublished activity revision)
        assertTrue(tags.stream().anyMatch(tag -> tag.getId() == 5L && tag.getActivitiesCount() == 0L));

        // Verify that tags assigned to no activity revisions are not counted
        assertTrue(tags.stream().anyMatch(tag -> tag.getId() == 6L && tag.getActivitiesCount() == 0L));

        tx.rollback();
    }

    @Test
    public void readTagByName() throws Exception {
        Transaction tx = beginTransaction();

        List<Tag> tags = dao.find(null, "Indoors", null);

        tx.rollback();

        assertTrue(tags.stream().allMatch(tag -> "Indoors".equals(tag.getName())));
    }

    @Test
    public void readTagByGroup() throws Exception {
        Transaction tx = beginTransaction();

        List<Tag> tags = dao.find("location", null, null);

        tx.rollback();

        assertTrue(tags.stream().allMatch(tag -> "location".equals(tag.getGroup())));
    }

    @Test
    public void readTagByActivitiesCount() throws Exception {
        Transaction tx = beginTransaction();

        long minimumNumberOfActivitiesAssignedToEachCategory = 2L;
        List<Tag> tags = dao.find(null, null, minimumNumberOfActivitiesAssignedToEachCategory);

        tx.rollback();

        List<Long> actualTagIds = tags.stream().map(Tag::getId).collect(Collectors.toList());
        Long[] expectedTagIds = {2L, 3L};
        assertTrue(Stream.of(expectedTagIds).allMatch(actualTagIds::contains));
    }

    @Test
    public void testCreate() throws Exception {
        assertFalse(isTagSaved("newgrp", "New Tag"));

        Transaction tx = beginTransaction();

        Tag tag = new Tag("newgrp", "New Tag");
        dao.create(tag);

        tx.commit();

        assertTrue(isTagSaved("newgrp", "New Tag"));
    }

    @Test
    public void testUpdate() throws Exception {
        assertFalse(isTagSaved("grp", "Funny"));
        assertTrue(isTagSaved("grp", "Fun"));

        Transaction tx = beginTransaction();

        Tag tag = dao.read(1L);
        tag.setName("Funny");
        dao.update(tag);

        tx.commit();

        assertFalse(isTagSaved("grp", "Fun"));
        assertTrue(isTagSaved("grp", "Funny"));
    }

    @Test
    public void testDelete() throws Exception {
        assertTrue(isTagSaved("grp", "Indoors"));

        Transaction tx = beginTransaction();

        Tag carol = dao.read(6L);
        dao.delete(carol);

        tx.commit();

        assertFalse(isTagSaved("grp", "Indoors"));
    }

    private boolean isTagSaved(String group, String name) {
        Query query = verifySession.createQuery("SELECT t FROM Tag t WHERE name = :name AND grp = :group");
        query.setString("name", name);
        query.setString("group", group);
        return !query.list().isEmpty();
    }
}