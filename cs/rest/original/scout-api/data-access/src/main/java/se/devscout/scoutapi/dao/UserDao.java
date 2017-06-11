package se.devscout.scoutapi.dao;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import se.devscout.scoutapi.model.IdentityType;
import se.devscout.scoutapi.model.User;

import java.util.HashSet;
import java.util.List;

public class UserDao extends AbstractDAO<User> {
    public UserDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<User> all() {
        return Lists.newArrayList(new HashSet<>(list(namedQuery("User.all"))));
    }

    public List<User> byName(String name) {
        return list(namedQuery("User.byName").setText("name", "%" + name + "%"));
    }

    public User readUser(long id) {
        return get(id);
    }

    public User readUserByIdentity(IdentityType type, String value) {
        List list = namedQuery("User.byIdentity").setParameter("type", type).setString("value", value).list();
        if (!list.isEmpty()) {
            User user = (User) list.get(0);
            return user;
        } else {
            return null;
        }
    }

    public User create(User user) {
        return persist(user);
    }

    public User update(User user) {
        return persist(user);
    }

    public void delete(User user) {
        currentSession().delete(user);
    }

}
