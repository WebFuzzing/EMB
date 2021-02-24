package se.devscout.scoutapi.dao;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import se.devscout.scoutapi.model.SystemMessage;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class SystemMessageDaoTest extends DaoTest {

    protected SystemMessageDao dao;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        dao = new SystemMessageDao(sessionFactory);
    }

    @Test
    public void testAll() throws Exception {
        Transaction tx = beginTransaction();

        List<SystemMessage> systemMessages = dao.all();

        assertNotNull(systemMessages);
        assertFalse(systemMessages.isEmpty());
        assertTrue(systemMessages.stream().anyMatch(systemMessage -> systemMessage.equals(new SystemMessage(1, "key.a", "value.a", null, null))));
        tx.rollback();
    }

    @Test
    public void findSystemMessage1a() throws Exception {
        testFindSystemMessages(
                LocalDateTime.of(2015, Month.JANUARY, 4, 0, 0),
                false,
                "key.a", "key.c", "key.e", "key.f");
    }

    @Test
    public void findSystemMessage1b() throws Exception {
        testFindSystemMessages(
                LocalDateTime.of(2015, Month.JANUARY, 4, 0, 0),
                true,
                "key.a", "key.c", "key.e", "key.f", "key.g");
    }

    @Test
    public void findSystemMessage2a() throws Exception {
        testFindSystemMessages(
                LocalDateTime.of(2014, Month.DECEMBER, 31, 0, 0),
                false,
                "key.a", "key.b");
    }

    @Test
    public void findSystemMessage2b() throws Exception {
        testFindSystemMessages(
                LocalDateTime.of(2015, Month.JANUARY, 10, 0, 0),
                true,
                "key.a", "key.c");
    }

    private void testFindSystemMessages(LocalDateTime dateTime, boolean includeMessagesStartingAfterValidDate, String... expectedKeys) {
        Transaction tx = beginTransaction();

        List<SystemMessage> systemMessages = dao.find(
                Date.from(dateTime.toInstant(ZoneOffset.ofHours(0))),
                includeMessagesStartingAfterValidDate);

        tx.rollback();

        assertKeys(systemMessages, expectedKeys);
    }

    private void assertKeys(List<SystemMessage> actualSystemMessages, String... expectedKeys) {
        List<String> actualKeys = actualSystemMessages.stream().map(systemMessage1 -> systemMessage1.getKey()).collect(Collectors.toList());

        assertThat(actualSystemMessages.size(), is(expectedKeys.length));
        Stream.of(expectedKeys).forEach(s -> {
            assertTrue(actualKeys.contains(s));
        });
    }

    @Test
    public void testCreate() throws Exception {
        assertFalse(isSystemMessageSaved("key.new"));

        Transaction tx = beginTransaction();

        SystemMessage systemMessage = new SystemMessage("key.new", "value.new");
        dao.create(systemMessage);

        tx.commit();

        assertTrue(isSystemMessageSaved("key.new"));
    }

    @Test
    public void testUpdate() throws Exception {
        assertFalse(isSystemMessageSaved("key.upd-updated"));
        assertTrue(isSystemMessageSaved("key.upd"));

        Transaction tx = beginTransaction();

        SystemMessage systemMessage = dao.read(8L);
        systemMessage.setKey("key.upd-updated");
        systemMessage.setValue("value.upd-updated");
        dao.update(systemMessage);

        tx.commit();

        assertFalse(isSystemMessageSaved("key.upd"));
        assertTrue(isSystemMessageSaved("key.upd-updated"));
    }

    @Test
    public void testDelete() throws Exception {
        assertTrue(isSystemMessageSaved("key.del"));

        Transaction tx = beginTransaction();

        SystemMessage carol = dao.read(9L);
        dao.delete(carol);

        tx.commit();

        assertFalse(isSystemMessageSaved("key.del"));
    }

    private boolean isSystemMessageSaved(String key) {
        Query query = verifySession.createQuery("SELECT t FROM SystemMessage t WHERE key = :key");
        query.setString("key", key);
        return !query.list().isEmpty();
    }
}