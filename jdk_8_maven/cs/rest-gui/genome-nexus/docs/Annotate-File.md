# Annotate MAF File 📄

You can use either the [web interface](https://www.cbioportal.org/mutation_mapper) or the command line client to annotate a MAF

## Annotate a MAF file using the command line client
We will be using the [genome-nexus-annotation-pipeline](https://github.com/genome-nexus/genome-nexus-annotation-pipeline/pulls) for this.

### Minimal Example
Create a MAF file:
```
(echo -e "Chromosome\tStart_Position\tEnd_Position\tReference_Allele\tTumor_Seq_Allele1"; echo -e "7\t55220240\t55220240\tG\tT") > input.txt;
```

Run the annotator using docker:
```
docker run -v ${PWD}:/wd genomenexus/gn-annotation-pipeline:master --filename /wd/input.txt  --output-filename /wd/output.txt --isoform-override uniprot
```

Output can be found in `output.txt`
