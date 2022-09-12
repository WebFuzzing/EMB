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
 * This file is part of cBioPortal Genome Nexus.
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

package org.cbioportal.genome_nexus.persistence.internal;

import com.mongodb.DBObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.util.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
@Repository
public class JsonMongoRepositoryImpl extends BaseGenericMongoRepository
{
    private static final Log LOG = LogFactory.getLog(JsonMongoRepositoryImpl.class);

    @Autowired
    public JsonMongoRepositoryImpl(MongoTemplate mongoTemplate)
    {
        super(mongoTemplate);
    }

    /**
     * Parses and saves the entire content of the annotation JSON object to the database.
     *
     * @param key  key (used as an id)
     * @param value raw JSON value
     */
    @Override
    public void saveDBObject(String collection, String key, DBObject value)
    {
        // parse the given json string to get a proper object
        List<DBObject> list = Transformer.convertToDbObjectList(value);

        // assuming json contains only a single json object
        if (list.size() != 1) {
            LOG.warn("Unexpected JSON size (!= 1): " + list.size() + "\nSee list:" + list);
        }

        // assuming value contains only a single variant.
        // get the first one, ignore the rest...
        DBObject dbObject = list.get(0);

        // update the _id field to the given variant
        dbObject.put("_id", key);

        // save the object into the correct repository
        this.mongoTemplate.save(dbObject, collection);
    }
}
