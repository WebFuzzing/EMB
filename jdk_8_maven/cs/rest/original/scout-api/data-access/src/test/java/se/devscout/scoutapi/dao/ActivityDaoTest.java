package se.devscout.scoutapi.dao;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import se.devscout.scoutapi.model.Activity;
import se.devscout.scoutapi.model.ActivityProperties;
import se.devscout.scoutapi.model.Tag;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

public class ActivityDaoTest extends DaoTest {

    private ActivityDao dao;
    private UserDao userDao;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        dao = new ActivityDao(sessionFactory);
        userDao = new UserDao(sessionFactory);
    }

    @Test
    public void testListAll() throws Exception {
        Transaction tx = beginTransaction();

        List<Activity> activities = dao.all();

        tx.commit();

        assertThat(activities.size(), is(5));
    }

    @Test
    public void testListConditionally_staticData() throws Exception {
        Transaction tx = beginTransaction();

/*
Activity Information----------------    Request Parameters  Activity Is Returned ---
Tag                        0      10    age_1=2             Yes
                                        age_1=2  age_2=20   No, since 20 is outside of 0-10 (it does not matter than 2 is within the range).
                                        age_1=2  age_2=5    Yes
                                        age_1=12            No, since 12 is outside of 0-10.
Cowboys and Indians        5      15    age_1=2             No, since 2 is outside of 5-15.
                                        age_1=5  age_2=10   Yes
                                        age_1=10 age_2=20   No, since 20 is outside of 5-15.
                                        age_1=12            Yes
I Have Never...           15      99    age_1=20            Yes
                                        age_1=100           No, since 100 is outside of 15-99.
*/

        testFilter(
                dao.all(null, false, null, null, null, null, null, Arrays.asList(1L, 2L), 0, null, null, null, null, null),
                new String[]{"Activity 1.3", "Activity 2.0"});
        testFilter(
                dao.all(null, false, null, Arrays.asList(1L, 2L), null, null, null, null, 0, null, null, null, null, null),
                new String[]{"Activity 1.3", "Activity 2.0"});
        testFilter(
                dao.all("tag", true, null, null, null, null, null, null, 0, null, null, null, null, null),
                new String[]{"Tag", "Tag with a twist"});
        testFilter(
                dao.all("tag", false, null, null, null, null, null, null, 0, null, null, null, null, null),
                new String[]{"Tag", "Tag with a twist", "Cowboys and Indians" /* The word "tag" exists in the description but not in the name. */});
        testFilter(
                dao.all("tag -twist", true, null, null, null, null, null, null, 0, null, null, null, null, null),
                new String[]{"Tag"});
        testFilter(
                dao.all("tag DESCRIPTION_MATERIAL", false, null, null, null, null, null, null, 0, null, null, null, null, null),
                new String[]{"Tag", "Tag with a twist"});
        testFilter(
                dao.all(null, false, null, null, Arrays.asList(2), null, null, null, 0, null, null, null, null, null),
                new String[]{"Tag"});
        testFilter(
                dao.all(null, false, null, null, Arrays.asList(2, 20), null, null, null, 0, null, null, null, null, null),
                new String[]{});
        testFilter(
                dao.all(null, false, null, null, Arrays.asList(2, 5), null, null, null, 0, null, null, null, null, null),
                new String[]{"Tag"});
        testFilter(
                dao.all(null, false, null, null, Arrays.asList(12), null, null, null, 0, null, null, null, null, null),
                new String[]{"Cowboys and Indians", "Activity 1.3", "Activity 2.0"});
        testFilter(
                dao.all(null, false, null, null, Arrays.asList(5, 10), null, null, null, 0, null, null, null, null, null),
                new String[]{"Tag", "Cowboys and Indians"});
        testFilter(
                dao.all(null, false, null, null, Arrays.asList(10, 20), null, null, null, 0, null, null, null, null, null),
                new String[]{});
        testFilter(
                dao.all(null, false, null, null, Arrays.asList(20), null, null, null, 0, null, null, null, null, null),
                new String[]{"Tag with a twist"});
        testFilter(
                dao.all(null, false, null, null, Arrays.asList(100), null, null, null, 0, null, null, null, null, null),
                new String[]{});
        testFilter(
                dao.all(null, false, Boolean.TRUE, null, null, null, null, null, 0, null, null, null, null, null),
                new String[]{"Tag", "Tag with a twist", "Cowboys and Indians"});
        testFilter(
                dao.all(null, false, Boolean.FALSE, null, null, null, null, null, 0, null, null, null, null, null),
                new String[]{"Activity 1.3", "Activity 2.0"});

        tx.commit();

    }

    @Test
    public void testListConditionally_userData() throws Exception {
        Transaction tx = beginTransaction();

        // Search for activities favoured by user 1
        testFilter(
                dao.all(null, false, null, null, null, null, null, null, 0, null, null, userDao.readUser(1L), null, null),
                new String[]{"Activity 1.3"});

        // Search for activities with an average rating of at least 4.0
        testFilter(
                dao.all(null, false, null, null, null, null, null, null, 0, null, 4.0, null, null, null),
                new String[]{"Activity 2.0", "Tag"});

        // Search for activities with at least 1 rating
        testFilter(
                dao.all(null, false, null, null, null, null, null, null, 0, 1L, null, null, null, null),
                new String[]{"Activity 1.3", "Activity 2.0", "Tag"});

        tx.commit();

    }

    @Test
    public void testRead() throws Exception {
        Transaction tx = beginTransaction();

        Activity activity = dao.read(1L);

        String actualName = activity.getProperties().getName();
        int actualRevisionsCount = activity.getPropertiesRevisions().size();

        tx.commit();

        assertThat(actualName, is("Activity 1.3"));
        assertThat(actualRevisionsCount, is(3));
    }

    private void testFilter(List<Activity> activities, String[] expectedActivityNames) {
        List<String> activityNames = activities.stream().map(activity -> activity.getProperties().getName()).collect(Collectors.toList());
        assertThat(activityNames.size(), is(expectedActivityNames.length));
        assertTrue(Arrays.asList(expectedActivityNames).stream().allMatch(s -> activityNames.contains(s)));
    }

    @Test
    public void testCreate() throws Exception {
        int numberOfTagsBefore = getEntityCount(Tag.class);
        int numberOfActivitiesBefore = getEntityCount(Activity.class);
        int numberOfActivityRevisionsBefore = getEntityCount(ActivityProperties.class);

        final Activity activity = new Activity("New Activity");

        activity.getProperties().setSource("src");
        activity.getProperties().setTags(Arrays.asList(
                // Add new tag
                new Tag("location", "At Home"),
                // Add existing tag without knowing its id
                new Tag("location", "Indoors"),
                // Add existing tag by id
                new Tag(1L)));

        Transaction tx = beginTransaction();
        Activity newActivity = dao.create(activity, true, true);
        long newId = newActivity.getId();
        tx.commit();

        Activity actualActivity = (Activity) verifySession.load(Activity.class, newId);

        // Check that new tag has been created
        int numberOfTagsAfter = getEntityCount(Tag.class);
        assertThat(numberOfTagsAfter, is(numberOfTagsBefore + 1));
        // Check that new activity has been created
        int numberOfActivitiesAfter = getEntityCount(Activity.class);
        assertThat(numberOfActivitiesAfter, is(numberOfActivitiesBefore + 1));
        // Check that a new entry of activity properties has been created
        int numberOfActivityRevisionsAfter = getEntityCount(ActivityProperties.class);
        assertThat(numberOfActivityRevisionsAfter, is(numberOfActivityRevisionsBefore + 1));
        // Check that all three tags, including the new one, are assigned to the new activity.
        assertThat(actualActivity.getProperties().getTags().size(), is(3));
        // Check name of activity
        assertThat(actualActivity.getProperties().getName(), is("New Activity"));
        assertThat(actualActivity.getProperties().getSource(), is("src"));
    }

    @Test
    public void testUpdate() throws Exception {
        int numberOfActivitiesBefore = getEntityCount(Activity.class);
        int numberOfActivityRevisionsBefore = getEntityCount(ActivityProperties.class);
        ActivityProperties beforeProperties = ((Activity) verifySession.load(Activity.class, 1L)).getProperties();
        assertThat(beforeProperties.getName(), not(is("Updated Name")));
        assertThat(beforeProperties.getName(), not(is("source")));
        // Check that description exists before the update, since it should be removed by the update.
        assertThat(beforeProperties.getDescriptionMaterial(), is("DESCRIPTION_MATERIAL"));

        Transaction tx = beginTransaction();
        Activity activity = dao.read(1L);
        ActivityProperties newValues = new ActivityProperties("Updated Name");
        newValues.setSource("source");
        dao.update(activity, newValues, true, true, false);
        tx.commit();

        ActivityProperties actualProperties = ((Activity) sessionFactory.openSession().load(Activity.class, 1L)).getProperties();
        int numberOfActivitiesAfter = getEntityCount(Activity.class);
        assertThat(numberOfActivitiesAfter, is(numberOfActivitiesBefore));
        int numberOfActivityRevisionsAfter = getEntityCount(ActivityProperties.class);
        assertThat(numberOfActivityRevisionsAfter, is(numberOfActivityRevisionsBefore + 1));
        assertThat(actualProperties.getName(), is("Updated Name"));
        assertThat(actualProperties.getSource(), is("source"));
        // Since this is an update operations instead of a patch operation, check that only the name property has a value after the update.
        assertNull(actualProperties.getDescriptionMaterial());
    }

    @Test
    public void testPatch() throws Exception {
        int numberOfActivitiesBefore = getEntityCount(Activity.class);
        int numberOfActivityRevisionsBefore = getEntityCount(ActivityProperties.class);
        ActivityProperties propertiesBefore = ((Activity) verifySession.load(Activity.class, 1L)).getProperties();
        assertThat(propertiesBefore.getName(), not(is("Updated Name")));
        // Assert that a description exists prior to patching the activity
        assertThat(propertiesBefore.getDescriptionMaterial(), is("DESCRIPTION_MATERIAL"));


        Transaction tx = beginTransaction();
        Activity activity = dao.read(1L);
        ActivityProperties newValues = new ActivityProperties("Updated Name");
        newValues.setSource("SOURCE");
        dao.update(activity, newValues, true, true, true);
        tx.commit();

        ActivityProperties actualProperties = ((Activity) sessionFactory.openSession().load(Activity.class, 1L)).getProperties();
        int numberOfActivitiesAfter = getEntityCount(Activity.class);
        assertThat(numberOfActivitiesAfter, is(numberOfActivitiesBefore));
        int numberOfActivityRevisionsAfter = getEntityCount(ActivityProperties.class);
        assertThat(numberOfActivityRevisionsAfter, is(numberOfActivityRevisionsBefore + 1));
        assertThat(actualProperties.getName(), is("Updated Name"));
        assertThat(actualProperties.getSource(), is("SOURCE"));
        // Assert that the description is still there after patching the activity (the description was not included in the patch data and should therefore be untouched).
        assertThat(actualProperties.getDescriptionMaterial(), is("DESCRIPTION_MATERIAL"));
    }

    @Test
    public void testDelete() throws Exception {
        int numberOfActivitiesBefore = getEntityCount(Activity.class);

        Transaction tx = beginTransaction();
        dao.delete(dao.read(2L));
        tx.commit();

        int numberOfActivitiesAfter = getEntityCount(Activity.class);
        assertThat(numberOfActivitiesAfter, is(numberOfActivitiesBefore - 1));
    }

    @Test
    public void testReadRelations() throws Exception {
        Transaction tx = beginTransaction();

        Activity activity = dao.read(3L);

        assertThat(Stream.of(activity.getRelatedIds()).sorted().toArray(value -> new Long[value]), is(new Long[]{1L, 2L, 4L}));

        tx.rollback();
    }

    @Test
    public void testReadRelationIds() throws Exception {
        Transaction tx = beginTransaction();

        List<Long> ids = dao.getRelatedActivityIds(3L);

        assertThat(ids.stream().sorted().toArray(value -> new Long[value]), is(new Long[]{1L, 2L, 4L}));

        tx.rollback();
    }

    @Test
    public void testSetRelations() throws Exception {
        Transaction tx = beginTransaction();
        dao.setActivityRelations(userDao.readUser(2L), 3L, 4L, 5L);
        tx.commit();

        Activity activity = ((Activity) sessionFactory.openSession().load(Activity.class, 3L));
        assertThat(activity.getRelatedIds().length, is(2));

        Activity actualUpdatedActivity = (Activity) verifySession.load(Activity.class, 3L);
        assertThat(Stream.of(actualUpdatedActivity.getRelatedIds()).sorted().toArray(value -> new Long[value]), is(new Long[]{4L, 5L}));

        Activity actualUnrelatedActivity = (Activity) verifySession.load(Activity.class, 4L);
        assertThat(Stream.of(actualUnrelatedActivity.getRelatedIds()).sorted().toArray(value -> new Long[value]), is(new Long[]{2L, 3L}));
    }
}