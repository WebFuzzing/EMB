package se.devscout.scoutapi.auth.apikey;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;
import se.devscout.scoutapi.dao.UserDao;

public class AbstractAuthenticator {
    private final SessionFactory sessionFactory;
    private UserDao userDao;

    public AbstractAuthenticator(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected void openSessionIfNecessary() {
        try {
            this.sessionFactory.getCurrentSession();
        } catch (HibernateException e) {
            ManagedSessionContext.bind(this.sessionFactory.openSession());
        }
    }

    protected void closeSessionIfNecessary() {
        Session session = this.sessionFactory.getCurrentSession();
        if (session != null) {
            session.close();
        }
    }

    protected UserDao getUserDao() {
        openSessionIfNecessary();
        if (userDao == null) {
            userDao = new UserDao(this.sessionFactory);
        }
        return userDao;
    }

    protected Transaction createTransaction() {
        openSessionIfNecessary();
        return sessionFactory.getCurrentSession().beginTransaction();
    }
}
