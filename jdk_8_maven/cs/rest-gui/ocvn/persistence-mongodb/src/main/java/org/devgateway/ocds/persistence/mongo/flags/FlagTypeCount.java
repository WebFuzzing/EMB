package org.devgateway.ocds.persistence.mongo.flags;

/**
 * Created by mpostelnicu on 3/1/17.
 * Stores flag
 */
public class FlagTypeCount {

    private FlagType type;
    private Integer count;

    /**
     * creates a new instance of {@link FlagTypeCount} and sets the count to 1
     *
     * @param flagType
     * @return
     */
    public static FlagTypeCount newInstance(FlagType flagType) {
        FlagTypeCount ftc = new FlagTypeCount();
        ftc.setCount(1);
        ftc.setType(flagType);
        return ftc;
    }

    /**
     * Counts one more flag
     * returns self
     */
    public FlagTypeCount inc() {
        count++;
        return this;
    }

    public FlagType getType() {
        return type;
    }

    public void setType(FlagType type) {
        this.type = type;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
