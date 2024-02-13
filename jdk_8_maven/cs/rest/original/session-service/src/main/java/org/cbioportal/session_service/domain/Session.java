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

package org.cbioportal.session_service.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.bson.Document;
import org.springframework.data.annotation.Id;
import org.springframework.util.DigestUtils;

/**
 * @author Manda Wilson
 */
@JsonInclude(Include.NON_NULL)
public class Session {
    @Id
    private String id;
    @NotNull
    private String checksum;
    @NotNull
    private Object data;
    @NotNull
    @Size(min=3, message="source has a minimum length of 3")
    private String source;
    @NotNull
    private SessionType type;


    @JsonView(Session.Views.IdOnly.class)
    public String getId() {
        return id;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setData(Object data) {
        if(data instanceof String) {
            this.data = Document.parse((String)data);
        } else {
            this.data = data;
        }
        this.checksum = DigestUtils.md5DigestAsHex(this.data.toString().getBytes());
    }

    @JsonView(Session.Views.Full.class)
    public Object getData() {
        return data;
    }

    public void setType(SessionType type) {
        this.type = type;
    }

    @JsonView(Session.Views.Full.class)
    public SessionType getType() {
        return type;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @JsonView(Session.Views.Full.class)
    public String getSource() {
        return source;
    }

    public static final class Views {
        // show only id
        public interface IdOnly {}

        // show all data
        public interface Full extends IdOnly {}
    }
}
