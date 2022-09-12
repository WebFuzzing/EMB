package org.cbioportal.genome_nexus.service;

import java.util.List;

import org.cbioportal.genome_nexus.model.Alteration;
import org.oncokb.client.CancerGene;
import org.oncokb.client.IndicatorQueryResp;

import org.cbioportal.genome_nexus.service.exception.OncokbNotFoundException;
import org.cbioportal.genome_nexus.service.exception.OncokbWebServiceException;

public interface OncokbService
{
    IndicatorQueryResp getOncokb(Alteration alteration, String token)
        throws OncokbNotFoundException, OncokbWebServiceException;
    List<CancerGene>  getOncokbCancerGenesList();
}
