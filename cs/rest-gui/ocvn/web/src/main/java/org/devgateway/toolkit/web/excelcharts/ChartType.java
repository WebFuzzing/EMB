package org.devgateway.toolkit.web.excelcharts;

/**
 * @author idobre
 * @since 8/16/16
 */

public enum ChartType {
    bar("bar"),

    barcol("barcol"),

    stackedbar("stackedbar"),

    stackedcol("stackedcol"),

    stackedbarpercentage("stackedbarpercentage"),

    stackedcolpercentage("stackedcolpercentage"),

    line("line"),

    area("area"),

    scatter("scatter"),

    pie("pie"),

    bubble("bubble");

    private final String value;

    ChartType(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
