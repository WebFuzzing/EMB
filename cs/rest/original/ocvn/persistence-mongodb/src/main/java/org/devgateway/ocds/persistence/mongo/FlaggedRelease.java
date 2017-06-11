/**
 * 
 */
package org.devgateway.ocds.persistence.mongo;

import com.fasterxml.jackson.annotation.JsonView;
import org.devgateway.ocds.persistence.mongo.flags.Flaggable;
import org.devgateway.ocds.persistence.mongo.flags.ReleaseFlags;
import org.devgateway.ocds.persistence.mongo.spring.json.Views;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author mpostelnicu
 *
 */
@Document(collection = "release")
public class FlaggedRelease extends Release implements Flaggable {

    @JsonView(Views.Internal.class)
    private ReleaseFlags flags;

    @Override
    public ReleaseFlags getFlags() {
        return flags;
    }

    public void setFlags(ReleaseFlags flags) {
        this.flags = flags;
    }
    
    

}
