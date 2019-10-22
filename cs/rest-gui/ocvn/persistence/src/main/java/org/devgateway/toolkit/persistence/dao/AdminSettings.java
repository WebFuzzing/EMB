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

    private String adminEmail = null;

    private Boolean enableDailyAutomatedImport = false;

    private String importFilesPath = null;

    /**
     * This disables the security of /api/ endpoints, should be used for demo purposes only
     */
    private Boolean disableApiSecurity = false;

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

    public Boolean getDisableApiSecurity() {
        return disableApiSecurity;
    }

    public void setDisableApiSecurity(Boolean disableApiSecurity) {
        this.disableApiSecurity = disableApiSecurity;
    }


    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public Boolean getEnableDailyAutomatedImport() {
        return enableDailyAutomatedImport;
    }

    public void setEnableDailyAutomatedImport(Boolean enableDailyAutomatedImport) {
        this.enableDailyAutomatedImport = enableDailyAutomatedImport;
    }

    public String getImportFilesPath() {
        return importFilesPath;
    }

    public void setImportFilesPath(String importFilesPath) {
        this.importFilesPath = importFilesPath;
    }
}
