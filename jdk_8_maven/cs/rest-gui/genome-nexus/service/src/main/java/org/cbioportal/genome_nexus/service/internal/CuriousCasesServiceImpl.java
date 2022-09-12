package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.CuriousCases;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.CuriousCasesService;
import org.cbioportal.genome_nexus.service.GenomicLocationAnnotationService;
import org.cbioportal.genome_nexus.service.exception.CuriousCasesNotFoundException;
import org.cbioportal.genome_nexus.service.exception.CuriousCasesWebServiceException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.cbioportal.genome_nexus.util.GenomicLocationUtil;
import org.cbioportal.genome_nexus.util.GenomicLocationUtil.MutationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.*;

/**
 * @author Xiang Li
 */
@Service
public class CuriousCasesServiceImpl implements CuriousCasesService {
    private final GenomicLocationAnnotationService genomicLocationAnnotationService;

    @Autowired
    public CuriousCasesServiceImpl(GenomicLocationAnnotationService genomicLocationAnnotationServiceImpl) {
        this.genomicLocationAnnotationService = genomicLocationAnnotationServiceImpl;
    }

    @Override
    public CuriousCases getCuriousCases(String genomicLocation)
            throws CuriousCasesNotFoundException, CuriousCasesWebServiceException {
        CuriousCases CuriousCases = null;

        try {
            VariantAnnotation annotation = genomicLocationAnnotationService.getAnnotation(genomicLocation, null, null, Arrays.asList("annotation_summary"));
            CuriousCases = generateCuriousCases(annotation, genomicLocation);
            if (CuriousCases != null) {
                CuriousCases.setGenomicLocation(genomicLocation);
                return CuriousCases;
            } else {
                return null;
            }
        } catch (HttpClientErrorException e) {
            // in case of web service error, throw an exception to indicate that there is a
            // problem with the service.
            throw new CuriousCasesWebServiceException(genomicLocation, e.getResponseBodyAsString(), e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new CuriousCasesWebServiceException(genomicLocation, e.getMessage());
        } catch (VariantAnnotationNotFoundException e) {
            throw new CuriousCasesNotFoundException(genomicLocation);
        } catch (VariantAnnotationWebServiceException e) {
            throw new CuriousCasesNotFoundException(genomicLocation);
        }
    }

    private CuriousCases generateCuriousCases(VariantAnnotation annotation, String genomicLocation) {
        CuriousCases CuriousCases = null;
        if (annotation != null) {
            String comment = null;
            List<Integer> pubmedIds = null;
            String hugoGeneSymbol = annotation.getAnnotationSummary().getTranscriptConsequenceSummary().getHugoGeneSymbol();
            String chr = annotation.getSeqRegionName();
            int start = annotation.getStart();
            int end = annotation.getEnd();
            String ref = annotation.getAlleleString().split("/")[0];
            String var = annotation.getAlleleString().split("/")[1];
            MutationType variantType = GenomicLocationUtil.getTypeFromGenomicLocation(genomicLocation);

            // Potential KIT Exon 11 inframe deletion
            if (chr.equals("4") && start <= 55593580 && end >= 55593606 && variantType.equals(MutationType.DEL)) {
                comment = "Potential in-frame deletion event at intron10-exon 11 boundary, commmon in Gastrointestinal Stromal Tumors";
                pubmedIds = new ArrayList<>(Arrays.asList(15507676, 27600498, 32697050));
            }
            // Potential KIT Exon 11 inframe deletion
            else if (chr.equals("4") && start == 55593580 && end == 55593580 && variantType.equals(MutationType.SNP) && ref.equals("A") && var.equals("T")) {
                comment = "Potential in-frame deletion event at intron10-exon 11 boundary, commmon in Gastrointestinal Stromal Tumors";
                pubmedIds = new ArrayList<>(Arrays.asList(15507676, 27600498, 32697050));
            }
            // Potential APC Exon 9 intro of cryptic splice acceptor site 
            else if (chr.equals("5") && start == 112151184 && end == 112151184 && variantType.equals(MutationType.SNP) && ref.equals("A") && var.equals("G")) {
                comment = "Potential introduction of donor splice site, common in colorectal cancers";
                pubmedIds = new ArrayList<>(Arrays.asList(29316426));
            }
            // Potential CTNNB1 Exon 3 inframe deletion
            else if (chr.equals("3") && start <= 41266015 && end >= 41266016 && variantType.equals(MutationType.DEL)) {
                comment = "Potential Exon Skip Event common in Colorectal Cancers";
                pubmedIds = new ArrayList<>(Arrays.asList(29316426));
            }
            // Potential CTNNB1 Exon 3 inframe deletion
            else if (chr.equals("3") && start <= 41266245 && end >= 41266246 && variantType.equals(MutationType.DEL)) {
                comment = "Potential Exon Skip Event common in Colorectal Cancers";
                pubmedIds = new ArrayList<>(Arrays.asList(29316426));
            }
            // Potential MET Exon 14 inframe deletion
            else if (chr.equals("7") && variantType.equals(MutationType.SNP) && (start == 116411901 && end == 116411901 ||
                start == 116411902 && end == 116411902 ||
                start == 116412044 && end == 116412044 ||
                start == 116412045 && end == 116412045)) {
                comment = "Potential Exon Skip Event common in Non-small Cell Lung Cancer";
            }
            // Potential MET Exon 14 inframe deletion
            else if (chr.equals("7") && start <= 116411901 && end >= 116411902 && variantType.equals(MutationType.DEL)) {
                comment = "Potential Exon Skip Event common in Non-small Cell Lung Cancer";
            }
            // Potential MET Exon 14 inframe deletion
            else if (chr.equals("7") && start <= 116412044 && end >= 116412045 && variantType.equals(MutationType.DEL)) {
                comment = "Potential Exon Skip Event common in Non-small Cell Lung Cancer";
            }
            // Potential PIK3R1 Exon 14 inframe deletions
            else if (chr.equals("5") && start == 67591246 && end == 67591246 && variantType.equals(MutationType.SNP) && ref.equals("A") && var.equals("T")) {
                comment = "Potential PIK3R1 Exon 14 inframe deletion";
            }
            // Potential PIK3R1 Exon 14 inframe deletions
            else if (chr.equals("5") && start <= 67591231 && end >= 67591258 && variantType.equals(MutationType.DEL)) {
                comment = "Potential PIK3R1 Exon 14 inframe deletion";
            }
            // Potential PIK3R1 Exon 14 inframe deletions
            else if (chr.equals("5") && start <= 67591153 && end >= 67591154 && variantType.equals(MutationType.DEL)) {
                comment = "Likely not a PIK3R1 Exon 14 inframe deletion";
            }
            // Potential PIK3R1 Exon 14 inframe deletions
            else if (chr.equals("5") && start == 67591153 && end == 67591154 && variantType.equals(MutationType.INDEL) && ref.equals("GT") && var.equals("C")) {
                comment = "Likely not a PIK3R1 Exon 14 inframe deletion";
            }
            // Potential PIK3R1 Exon 11 inframe deletions
            else if (chr.equals("5") && start == 68293836 && end == 68293836 && variantType.equals(MutationType.SNP) && ref.equals("T") && var.equals("G")) {
                comment = "Potential PIK3R1 Exon 11 inframe deletion";
            }
            // Potential PIK3R1 Exon 11 inframe deletions
            else if (chr.equals("5") && start == 68293835 && end == 68293835 && variantType.equals(MutationType.SNP) && ref.equals("G") && var.equals("A")) {
                comment = "Potential PIK3R1 Exon 11 inframe deletion";
            }

            if (comment != null || pubmedIds != null) {
                CuriousCases = new CuriousCases();
                CuriousCases.setHugoGeneSymbol(hugoGeneSymbol);
                if (comment != null) {
                    CuriousCases.setComment(comment);
                }
                if (pubmedIds != null) {
                    CuriousCases.setPubmedIds(pubmedIds);
                }
            }

        }
        return CuriousCases;
    }
}
