package se.devscout.scoutapi.dao;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import se.devscout.scoutapi.model.IdentityType;
import se.devscout.scoutapi.model.User;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class UserDaoTest extends DaoTest {

    protected UserDao dao;

    static final long ALICE_ID = 1L;
    private static final String ALICE_NAME = "Alice";

    private static final String BOB_NAME = "Bob";

    private static final String CAROL_NAME = "Carol";
    static final long CAROL_ID = 2L;

    private static final String DAVE_NAME = "Dave";

    static final long EMILY_ID = 3L;
    private static final String EMILY_NAME = "Emily";

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        dao = new UserDao(sessionFactory);
    }

    @Test
    public void testAll() throws Exception {
        Transaction tx = beginTransaction();

        List<User> users = dao.all();

        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertTrue(users.stream().anyMatch(user -> ALICE_NAME.equals(user.getName())));
        assertTrue(users.stream().anyMatch(user -> CAROL_NAME.equals(user.getName())));
        assertTrue(users.stream().anyMatch(user -> CAROL_NAME.equals(user.getName()) && user.getIdentities().stream().anyMatch(userIdentity -> "carol-key-2".equals(userIdentity.getValue()))));

        tx.rollback();
    }

    @Test
    public void readUserByIdentity() throws Exception {
        Transaction tx = beginTransaction();

        User user = dao.readUserByIdentity(IdentityType.API, "carol-key-1");

        tx.rollback();

        assertNotNull(user);
        assertThat(user.getName(), is(CAROL_NAME));
    }

    @Test
    public void readUser() throws Exception {
        Transaction tx = beginTransaction();

        User user = dao.readUser(ALICE_ID);

        tx.rollback();

        assertNotNull(user);
        assertThat(user.getName(), is(ALICE_NAME));
    }

    @Test
    public void testCreate() throws Exception {
        assertFalse(isUserSaved(BOB_NAME));

        Transaction tx = beginTransaction();

        User bob = new User(BOB_NAME);
        bob.setEmailAddress("bob@example.com");
        bob.addIdentity(IdentityType.API, "bob-key");
        User createdUser = dao.create(bob);

        tx.commit();

        User actualUser = (User) verifySession.load(User.class, createdUser.getId());
        assertThat(actualUser.getName(), is(BOB_NAME));
        assertThat(actualUser.getEmailAddress(), is("bob@example.com"));
        assertTrue(isUserSaved(BOB_NAME));
    }

    @Test
    public void testUpdate() throws Exception {
        assertFalse(isUserSaved(DAVE_NAME));
        assertTrue(isUserSaved(ALICE_NAME));

        Transaction tx = beginTransaction();

        User alice = dao.readUser(ALICE_ID);
        alice.setName(DAVE_NAME);
        dao.update(alice);

        tx.commit();

        assertFalse(isUserSaved(ALICE_NAME));
        assertTrue(isUserSaved(DAVE_NAME));
    }

    @Test
    public void testDelete() throws Exception {
        assertTrue(isUserSaved(EMILY_NAME));

        Transaction tx = beginTransaction();

        User carol = dao.readUser(EMILY_ID);
        dao.delete(carol);

        tx.commit();

        assertFalse(isUserSaved(EMILY_NAME));
    }

    private boolean isUserSaved(String name) {
        Query query = verifySession.createQuery("SELECT u FROM User u WHERE name = :name");
        query.setString("name", name);
        return !query.list().isEmpty();
    }
}