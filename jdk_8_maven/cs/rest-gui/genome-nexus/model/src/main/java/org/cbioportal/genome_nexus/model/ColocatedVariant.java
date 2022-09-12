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

package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class ColocatedVariant
{
    private String gnomad_nfe_maf;

    @Field(value="gnomad_nfe_maf")
    public String getgnomad_nfe_maf()
    {
        return gnomad_nfe_maf;
    }

    public void setgnomad_nfe_maf(String gnomad_nfe_maf)
    {
        this.gnomad_nfe_maf = gnomad_nfe_maf;
    }

    private String gnomad_nfe_allele;

    @Field(value="gnomad_nfe_allele")
    public String getgnomad_nfe_allele()
    {
        return gnomad_nfe_allele;
    }

    public void setgnomad_nfe_allele(String gnomad_nfe_allele)
    {
        this.gnomad_nfe_allele = gnomad_nfe_allele;
    }

    private String gnomad_afr_maf;

    @Field(value="gnomad_afr_maf")
    public String getgnomad_afr_maf()
    {
        return gnomad_afr_maf;
    }

    public void setgnomad_afr_maf(String gnomad_afr_maf)
    {
        this.gnomad_afr_maf = gnomad_afr_maf;
    }

    private String gnomad_afr_allele;

    @Field(value="gnomad_afr_allele")
    public String getgnomad_afr_allele()
    {
        return gnomad_afr_allele;
    }

    public void setgnomad_afr_allele(String gnomad_afr_allele)
    {
        this.gnomad_afr_allele = gnomad_afr_allele;
    }

    private String gnomad_eas_maf;

    @Field(value="gnomad_eas_maf")
    public String getgnomad_eas_maf()
    {
        return gnomad_eas_maf;
    }

    public void setgnomad_eas_maf(String gnomad_eas_maf)
    {
        this.gnomad_eas_maf = gnomad_eas_maf;
    }

    private String gnomad_eas_allele;

    @Field(value="gnomad_eas_allele")
    public String getgnomad_eas_allele()
    {
        return gnomad_eas_allele;
    }

    public void setgnomad_eas_allele(String gnomad_eas_allele)
    {
        this.gnomad_eas_allele = gnomad_eas_allele;
    }

    private String dbSnpId;

    @Field(value="id")
    public String getdbSnpId()
    {
        return dbSnpId;
    }
}
