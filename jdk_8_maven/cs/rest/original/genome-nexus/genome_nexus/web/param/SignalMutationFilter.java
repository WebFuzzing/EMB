package org.cbioportal.genome_nexus.web.param;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class SignalMutationFilter
{
    @ApiModelProperty(value = EnsemblFilter.HUGO_SYMBOL_DESC)
    private List<String> hugoSymbols;

    public List<String> getHugoSymbols() {
        return hugoSymbols;
    }

    public void setHugoSymbols(List<String> hugoSymbols) {
        this.hugoSymbols = hugoSymbols;
    }
}
