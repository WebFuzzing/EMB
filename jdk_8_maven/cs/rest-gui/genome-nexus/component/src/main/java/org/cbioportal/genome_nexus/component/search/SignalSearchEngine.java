package org.cbioportal.genome_nexus.component.search;

import org.cbioportal.genome_nexus.model.*;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class SignalSearchEngine
{
    private static final Comparator<SignalQuery> QUERY_COMPARATOR =
        (o1, o2) -> o1.getMatchType().ordinal() - o2.getMatchType().ordinal();

    public List<SignalQuery> searchByHugoSymbol(
        List<SignalQuery> signalIndex,
        String queryString
    ) {
        return signalIndex
            .stream()
            .filter(m -> filterByHugoSymbol(m, queryString))
            .map(SignalQuery::getHugoSymbol)
            .distinct()
            .map(gene -> this.mapHugoSymbolToQuery(gene, queryString, signalIndex))
            .sorted(QUERY_COMPARATOR)
            .collect(Collectors.toList());
    }

    public List<SignalQuery> searchByRegion(
        List<SignalQuery> signalIndex,
        String queryString
    ) {
        return signalIndex
            .stream()
            .filter(m -> this.filterByRegion(m, queryString))
            .map(m -> this.mapToVariantQuery(m, SignalQueryType.REGION, queryString))
            .sorted(QUERY_COMPARATOR)
            .collect(Collectors.toList());
    }

    public List<SignalQuery> searchByVariant(
        List<SignalQuery> signalIndex,
        String queryString
    ) {
        return signalIndex
            .stream()
            .filter(m -> this.filterByVariant(m, queryString))
            .map(m -> this.mapToVariantQuery(m, SignalQueryType.VARIANT, queryString))
            .sorted(QUERY_COMPARATOR)
            .collect(Collectors.toList());
    }

    public List<SignalQuery> searchByAlteration(
        List<SignalQuery> signalIndex,
        String queryString
    ) {
        // special case: we allow hugo gene symbol as a prefix
        // in case of multiple keywords assume that first one is hugo symbol
        String[] parts = queryString.split("\\s+");
        String geneKeyword = parts.length > 1 ? parts[0]: null;
        String alterationKeyword = parts.length > 1 ? parts[1]: queryString;

        // filter the index by hugo symbol (if keyword exists)
        List<SignalQuery> filteredIndex = geneKeyword == null ? signalIndex:
            signalIndex
                .stream()
                .filter(m -> filterByHugoSymbol(m, geneKeyword))
                .collect(Collectors.toList());

        return filteredIndex
            .stream()
            .filter(m -> this.filterByAlteration(m, alterationKeyword))
            .map(m -> this.mapToVariantQuery(m, SignalQueryType.ALTERATION, alterationKeyword))
            .sorted(QUERY_COMPARATOR)
            .collect(Collectors.toList());
    }

    public SignalQuery mapToVariantQuery(
        SignalQuery indexedQuery,
        SignalQueryType queryType,
        String queryString
    ) {
        String actualString = null;

        if (queryType.equals(SignalQueryType.REGION)) {
            actualString = indexedQuery.getRegion();
        }
        else if (queryType.equals(SignalQueryType.VARIANT)) {
            actualString = indexedQuery.getVariant();
        }
        else if (queryType.equals(SignalQueryType.ALTERATION)) {
            actualString = indexedQuery.getAlteration();
        }

        SignalQuery query = new SignalQuery();

        query.setHugoSymbol(indexedQuery.getHugoSymbol());
        query.setRegion(indexedQuery.getRegion());
        query.setVariant(indexedQuery.getVariant());
        query.setAlteration(indexedQuery.getAlteration());
        query.setQueryType(queryType);
        query.setMatchType(this.findMatchType(actualString, queryString));

        return query;
    }

    public SignalQuery mapHugoSymbolToQuery(
        String hugoSymbol,
        String queryString,
        List<SignalQuery> signalIndex
    ) {
        long mutationCount = signalIndex.stream().filter(m -> this.filterByHugoSymbol(m, hugoSymbol)).count();

        SignalQuery query = new SignalQuery();
        query.setQueryType(SignalQueryType.GENE);
        query.setMatchType(this.findMatchType(hugoSymbol, queryString));
        query.setHugoSymbol(hugoSymbol);
        query.setDescription(mutationCount + " unique mutations");

        return query;
    }

    public boolean filterByHugoSymbol(
        SignalQuery query,
        String queryString
    ) {
        return (
            query.getHugoSymbol() != null &&
                query.getHugoSymbol().contains(queryString.toUpperCase())
        );
    }

    public boolean filterByRegion(
        SignalQuery query,
        String queryString
    ) {
        return query.getRegion().contains(queryString);
    }

    public boolean filterByVariant(
        SignalQuery query,
        String queryString
    ) {
        return query.getVariant() != null && query.getVariant().toLowerCase().contains(queryString.toLowerCase());
    }

    public boolean filterByAlteration(
        SignalQuery query,
        String queryString
    ) {
        return query.getAlteration() != null && query.getAlteration().toLowerCase().contains(queryString.toLowerCase());
    }

    public SignalMatchType findMatchType(
        String actualString,
        String queryString
    ) {
        if (actualString == null) {
            return SignalMatchType.NO_MATCH;
        }

        String actualLowerCase = actualString.toLowerCase();
        String queryLowerCase = queryString.toLowerCase();

        if (actualLowerCase.equals(queryLowerCase)) {
            return SignalMatchType.EXACT;
        }
        else if (actualLowerCase.startsWith(queryLowerCase)) {
            return SignalMatchType.STARTS_WITH;
        }
        else if (actualLowerCase.contains(queryLowerCase)) {
            return SignalMatchType.PARTIAL;
        }
        else {
            return SignalMatchType.NO_MATCH;
        }
    }
}
