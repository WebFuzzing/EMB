package se.devscout.scoutapi.textanalyzer.comparator;

public class MaterialTextComparator extends AbstractWordsHistogramComparator {
    public MaterialTextComparator() {
        super(activity -> activity.descr_material_words);
    }

    @Override
    public String toString() {
        return "equip";
    }
}
