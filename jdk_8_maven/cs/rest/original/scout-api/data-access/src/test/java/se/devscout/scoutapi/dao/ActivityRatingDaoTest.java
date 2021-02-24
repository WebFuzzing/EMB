package se.devscout.scoutapi.dao;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import se.devscout.scoutapi.model.ActivityRating;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ActivityRatingDaoTest extends DaoTest {

    protected ActivityRatingDao dao;
    private ActivityDao activityDao;
    private UserDao userDao;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        dao = new ActivityRatingDao(sessionFactory);
        activityDao = new ActivityDao(sessionFactory);
        userDao = new UserDao(sessionFactory);
    }

    @Test
    public void testAll() throws Exception {
        Transaction tx = beginTransaction();

        List<ActivityRating> activityRatings = dao.all();

        assertNotNull(activityRatings);
        assertFalse(activityRatings.isEmpty());
        tx.rollback();
    }

    @Test
    public void readActivityRatingByUser() throws Exception {
        Transaction tx = beginTransaction();

        List<ActivityRating> activityRatings = dao.all(userDao.readUser(UserDaoTest.ALICE_ID));

        tx.rollback();

        assertThat(activityRatings.size(), is(3));
        assertTrue(activityRatings.stream().allMatch(activityRating -> activityRating.getUser().getId() == UserDaoTest.ALICE_ID));
    }

    @Test
    public void readActivityRatingByActivity() throws Exception {
        Transaction tx = beginTransaction();

        List<ActivityRating> activityRatings = dao.all(activityDao.read(2L));

        tx.rollback();

        assertThat(activityRatings.size(), is(2));
        assertTrue(activityRatings.stream().allMatch(activityRating -> activityRating.getActivity().getId() == 2L));
    }

    @Test
    public void testCreateAndDelete() throws Exception {
        assertFalse(isActivityRatingSaved(5L, UserDaoTest.EMILY_ID));

        Transaction tx1 = beginTransaction();

        dao.create(new ActivityRating(activityDao.read(5L), userDao.readUser(UserDaoTest.EMILY_ID)));

        tx1.commit();

        assertTrue(isActivityRatingSaved(5L, UserDaoTest.EMILY_ID));

        Transaction tx2 = beginTransaction();

        dao.delete(dao.read(activityDao.read(5L), userDao.readUser(UserDaoTest.EMILY_ID)));

        tx2.commit();

        assertFalse(isActivityRatingSaved(5L, UserDaoTest.EMILY_ID));
    }

    @Test
    public void testUpdate() throws Exception {
        ActivityRating before = (ActivityRating) sessionFactory.openSession().load(ActivityRating.class, new ActivityRating.Key(2L, UserDaoTest.CAROL_ID));
        assertThat(before.getRating(), is(5));
        Transaction tx = beginTransaction();

        ActivityRating expected = dao.read(activityDao.read(2L), userDao.readUser(UserDaoTest.CAROL_ID));
        expected.setRating(1);
        dao.update(expected);

        tx.commit();


        ActivityRating actual = (ActivityRating) sessionFactory.openSession().load(ActivityRating.class, new ActivityRating.Key(2L, UserDaoTest.CAROL_ID));

        assertThat(actual.getRating(), is(expected.getRating()));
    }

    private boolean isActivityRatingSaved(long activityId, long userId) {
        Query query = verifySession.createSQLQuery("SELECT * FROM ACTIVITY_RATING aur WHERE aur.activity_Id = :activityId AND aur.user_Id = :userId");
        query.setParameter("activityId", activityId);
        query.setParameter("userId", userId);
        return !query.list().isEmpty();
    }
}