package se.devscout.scoutapi.dao;

import com.google.common.base.Splitter;
import com.google.common.io.Resources;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

public class DaoTest {
    protected Session verifySession;
    protected SessionFactory sessionFactory;

    @Before
    public void setUp() throws Exception {
        sessionFactory = HibernateUtil.getSessionFactory();

        executeSQLFileForClass();

        verifySession = sessionFactory.openSession();
    }

    @After
    public void tearDown() throws Exception {
        verifySession.close();
        sessionFactory.close();
        HibernateUtil.shutdown();
    }

    protected Transaction beginTransaction() {
        return sessionFactory.getCurrentSession().beginTransaction();
    }

    protected int getEntityCount(Class entityClass) {
        List list = verifySession.createQuery("SELECT COUNT(a) FROM " + entityClass.getName() + " a").list();
        Long result = (Long) list.get(0);
        return result != null ? result.intValue() : 0;
    }

    protected void executeSQLFileForClass() throws IOException {
        String resourceName = getClass().getSimpleName() + ".sql";

        final Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            URL resource = Resources.getResource(resourceName);

            List<String> queries = Splitter.on(';').splitToList(Resources.readLines(resource, Charset.forName("UTF-8")).stream()
                    .filter(s -> !s.startsWith("--"))
                    .map(String::trim)
                    .collect(Collectors.joining("")));
            queries.forEach(s -> {
                if (s.length() > 0) {
                    int rowsAffected = session.createSQLQuery(s).executeUpdate();
                    System.out.println(rowsAffected + " rows affected by this query: " + s.replace('\n', ' '));
                }
            });
            tx.commit();
        } catch (IllegalArgumentException e) {
            // Resource not found
            LoggerFactory.getLogger(getClass()).info("Could not find SQL script " + resourceName, e);
            tx.rollback();
        } catch (Throwable throwable) {
            tx.rollback();
            throw throwable;
        } finally {
            session.close();
        }
    }
}
