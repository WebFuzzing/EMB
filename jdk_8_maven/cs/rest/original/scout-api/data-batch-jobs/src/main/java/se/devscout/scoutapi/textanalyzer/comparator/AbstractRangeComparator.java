package se.devscout.scoutapi.textanalyzer.comparator;

abstract class AbstractRangeComparator implements ActivityMetadataComparator {
    double compare(Integer age1Min, Integer age1Max, Integer age2Min, Integer age2Max, int lowestMin, int highestMax) {
        age1Max = age1Max == null || age1Max > highestMax ? highestMax : age1Max;
        age2Max = age2Max == null || age2Max > highestMax ? highestMax : age2Max;
        age1Min = age1Min == null || age1Min < lowestMin ? lowestMin : age1Min;
        age2Min = age2Min == null || age2Min < lowestMin ? lowestMin : age2Min;
        double rangeUnion = Math.max(age1Max, age2Max) - Math.min(age1Min, age2Min);
        double rangeIntersection = Math.max(0, Math.min(age1Max, age2Max) - Math.max(age1Min, age2Min));
        return rangeUnion > 0 ? rangeIntersection / rangeUnion : 0;
    }
}
