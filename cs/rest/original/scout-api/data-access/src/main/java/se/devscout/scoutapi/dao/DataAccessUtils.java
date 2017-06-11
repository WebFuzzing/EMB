package se.devscout.scoutapi.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;
import se.devscout.scoutapi.model.User;

import java.util.List;
import java.util.concurrent.Callable;

public class DataAccessUtils {
    public static <T> T runInTransaction(SessionFactory sessionFactory, Callable<T> runnable) throws Exception {
        T result = null;
        Session session = sessionFactory.openSession();
        ManagedSessionContext.bind(session);
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            result = runnable.call();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
            return result;
        }
    }

    public static User getUser(UserDao userDao, String name) {
        List<User> users = userDao.byName(name);
        if (users == null || users.size() != 1) {
            throw new IllegalArgumentException("Could not find user " + name);
        }
        return users.get(0);
    }
}
