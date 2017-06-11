package se.devscout.scoutapi.dao;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import se.devscout.scoutapi.model.MediaFile;

import java.util.HashSet;
import java.util.List;

public class MediaFileDao extends AbstractDAO<MediaFile> {
    public MediaFileDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<MediaFile> all() {
        return Lists.newArrayList(new HashSet<>(list(namedQuery("MediaFile.all"))));
    }

    public MediaFile read(long id) {
        return get(id);
    }

    public MediaFile read(String uri) {
        List<MediaFile> list = find(uri);
        return list != null && list.size() == 1 ? (MediaFile) list.get(0) : null;
    }

    public List<MediaFile> find(String uri) {
        Query query;
        if (!Strings.isNullOrEmpty(uri)) {
            query = namedQuery("MediaFile.byUri").setText("uri", "%" + uri + "%");
        } else {
            return all();
        }
        return query.list();
    }

    public boolean isUsed(long id) {
        return !namedQuery("MediaFile.associatedActivityProperties").setLong("id", id).list().isEmpty();
    }

    public List<MediaFile> byKeyword(String keyword) {
        Query query;
        if (!Strings.isNullOrEmpty(keyword)) {
            query = namedQuery("MediaFile.byKeyword").setText("keyword", keyword);
        } else {
            return all();
        }
        return query.list();
    }

    public MediaFile create(MediaFile mediaFile) {
        return persist(mediaFile);
    }

    public MediaFile update(MediaFile mediaFile) {
        return persist(mediaFile);
    }

    public void delete(MediaFile mediaFile) {
        currentSession().delete(mediaFile);
    }

}
