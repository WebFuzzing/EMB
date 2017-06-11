package org.devgateway.toolkit.persistence.dao;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * @author idobre
 * @since 6/22/16
 */
@Entity
@Audited
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AdminSettings extends AbstractAuditableEntity implements Serializable {
    private static final long serialVersionUID = -1051140524022133178L;

    private Integer excelBatchSize;

    private Boolean rebootServer = false;

    @Override
    public AbstractAuditableEntity getParent() {
        return null;
    }

    public Integer getExcelBatchSize() {
        return excelBatchSize;
    }

    public void setExcelBatchSize(final Integer excelBatchSize) {
        this.excelBatchSize = excelBatchSize;
    }

    public Boolean getRebootServer() {
        return rebootServer;
    }

    public void setRebootServer(final Boolean rebootServer) {
        this.rebootServer = rebootServer;
    }
}
