package se.devscout.scoutapi.dao;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import se.devscout.scoutapi.model.Tag;

import java.util.*;

public class TagDao extends AbstractDAO<Tag> {
    public TagDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<Tag> all() {
        return Lists.newArrayList(new HashSet<>(list(namedQuery("Tag.all"))));
    }

    public Tag read(long id) {
        return get(id);
    }

    public Tag read(String group, String name) {
        List<Tag> list = find(group, name, null);
        return list != null && list.size() == 1 ? (Tag) list.get(0) : null;
    }

    public List<Tag> find(String group, String name, Long minActivitiesCount) {
        StringBuilder q = new StringBuilder();
        List<String> qWhereConditions = new ArrayList<>();
        Map<String, Object> qWhereParameters = new HashMap<>();
        q.append("SELECT t FROM Tag t");

        if (name != null) {
            qWhereConditions.add("UPPER(t.name) LIKE :name");
            qWhereParameters.put("name", "%" + name.toUpperCase() + "%");
        }
        if (group != null) {
            qWhereConditions.add("UPPER(t.group) LIKE :group");
            qWhereParameters.put("group", "%" + group.toUpperCase() + "%");
        }
        if (minActivitiesCount != null) {
            qWhereConditions.add("t.derived.activitiesCount >= :minActivitiesCount");
            qWhereParameters.put("minActivitiesCount", minActivitiesCount);
        }
        if (!qWhereConditions.isEmpty()) {
            q.append(" WHERE (" + Joiner.on(") AND (").join(qWhereConditions) + ")");
        }

        q.append(" ORDER BY t.name");

        Query query = currentSession().createQuery(q.toString());
        qWhereParameters.forEach((key, val) -> {
            query.setParameter(key, val);
        });

        List<Tag> tags = Lists.newArrayList(new HashSet<>(query.list()));
        return tags;
    }

    public Tag create(Tag tag) {
        return persist(tag);
    }

    public Tag update(Tag tag) {
        return persist(tag);
    }

    public void delete(Tag tag) {
        currentSession().delete(tag);
    }

}
