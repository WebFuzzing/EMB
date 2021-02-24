package se.devscout.scoutapi.dao;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import se.devscout.scoutapi.model.SystemMessage;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class SystemMessageDao extends AbstractDAO<SystemMessage> {
    public SystemMessageDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<SystemMessage> all() {
        return Lists.newArrayList(new HashSet<>(list(namedQuery("SystemMessage.all"))));
    }

    public SystemMessage read(long id) {
        return get(id);
    }

    public List<SystemMessage> find(Date validDate, boolean includeMessagesStartingAfterValidDate) {
        if (validDate == null) {
            return all();
        }
        return namedQuery(includeMessagesStartingAfterValidDate ? "SystemMessage.validAtOrEventuallyAfterTime" : "SystemMessage.validAtTime").setTimestamp("date", validDate).list();
    }

    public SystemMessage create(SystemMessage systemMessage) {
        return persist(systemMessage);
    }

    public SystemMessage update(SystemMessage systemMessage) {
        return persist(systemMessage);
    }

    public void delete(SystemMessage systemMessage) {
        currentSession().delete(systemMessage);
    }

}
