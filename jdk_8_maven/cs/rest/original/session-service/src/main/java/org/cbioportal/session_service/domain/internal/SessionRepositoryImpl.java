/*
 * Copyright (c) 2016 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal Session Service.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.cbioportal.session_service.domain.internal;

import org.bson.Document;
import org.cbioportal.session_service.domain.Session;
import org.cbioportal.session_service.domain.SessionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

/**
 * This is necessary because we are saving objects from one domain
 * class to different collections.
 *
 * @author Manda Wilson 
 */
public class SessionRepositoryImpl implements SessionRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public void saveSession(Session session) {
        if (!this.mongoTemplate.collectionExists(session.getType().toString())) {
            this.mongoTemplate.createCollection(session.getType().toString());
            Document indexKeys = new Document();
            indexKeys.append("source", 1);
            indexKeys.append("type", 1);
            indexKeys.append("checksum", 1);
            this.mongoTemplate.indexOps(session.getType().toString()).ensureIndex(
                new CompoundIndexDefinition(indexKeys).unique());
        } 
        this.mongoTemplate.save(session, session.getType().toString());
    }

    public Session findOneBySourceAndTypeAndData(String source, SessionType type, Object data) {
        Query query = new Query(Criteria.where("source").is(source).and("type").is(type).and("data").is(data));
        return this.mongoTemplate.findOne(query, Session.class, type.toString());
    }

    public Session findOneBySourceAndTypeAndChecksum(String source, SessionType type, String checksum) {
        Query query = new Query(Criteria.where("source").is(source).and("type").is(type).and("checksum").is(checksum));
        return this.mongoTemplate.findOne(query, Session.class, type.toString());
    }

    public Session findOneBySourceAndTypeAndId(String source, SessionType type, String id) {
        return this.mongoTemplate.findOne(
            new Query(Criteria.where("source").is(source).and("type").is(type).and("id").is(id)), 
            Session.class, type.toString());
    }

    public List<Session> findBySourceAndType(String source, SessionType type) {
        return this.mongoTemplate.find(
            new Query(Criteria.where("source").is(source).and("type").is(type)), 
            Session.class, type.toString());
    }

    public long deleteBySourceAndTypeAndId(String source, SessionType type, String id) {
        return this.mongoTemplate.remove(
            new Query(Criteria.where("source").is(source).and("type").is(type).and("id").is(id)),
            Session.class, type.toString()).getDeletedCount();
    }

    public List<Session> findBySourceAndTypeAndQuery(String source, SessionType type, String query) {
        BasicQuery basicQuery = new BasicQuery(query);
        basicQuery.addCriteria(Criteria.where("source").is(source));
        return this.mongoTemplate.find(basicQuery, Session.class, type.toString());
    }
}
