# Annotation Sources 🗄️
Genome Nexus aggregates variant annotation from various sources. There are two types:

1. Small sized annotations are stored as static data in the mongo database
directly. See the [genome-nexus-importer repo](https://github.com/genome-nexus/genome-nexus-importer) if you want to
update/change this data.
2. For larger annotation services/databases we either run the full annotation
service ourselves or the program pulls on the fly from other APIs and caches
the result in the mongo database.

For a list of all supported variant annotation sources see this Google Sheet:

https://docs.google.com/spreadsheets/d/1xML949NWzJGcvltjlquwSRIv79o13C_sfrVPAU5ci9Q/edit#gid=258442188

## Versions

The static annotation sources are either stored directly in the mongo database
or we run the annotation service ourselves. In both cases the version is
guaranteed. For the dynamic annotion sources an external API is called and
responses are cached on a per request basis. Given that the external API is
outside of our control, no particular version is guaranteed, but we provide
links to documentation of those APIs to find the currently live version.

### Static annotation sources

Algorithm | URL | Version
--- | --- | ---
VEP | https://grch37.ensembl.org/info/docs/tools/vep/index.html | grch37
Cancer Hotspots | https://www.cancerhotspots.org | From paper
3D Hotspots | https://www.3dhotspots.org/#/home | From paper


VEP includes various other annotations:

Source | Version (GRCh38) | Version (GRCh37)
--- | --- | ---
Ensembl database version | 103 | 103
Genome assembly | GRCh38.p12 | GRCh37.p13
GENCODE | 37 | 19
RefSeq | 2020-09-29 (GCF_000001405.39_GRCh38.p13_genomic.gff) | 2019-11-01 (GCF_000001405.25_GRCh37.p13_genomic.gff)
Regulatory build | 1.0 | 1.0
PolyPhen | 2.2.2 | 2.2.2
SIFT | 5.2.2 | 5.2.2
dbSNP | 154 | 153
COSMIC | 92 | 90
HGMD-PUBLIC | 2019.4 | 2019.4
ClinVar | 	2020-08/2020-09 | 	2019-12
1000 Genomes | Phase 3 (remapped) | Phase 3
NHLBI-ESP | V2-SSA137 (remapped) | V2-SSA137
gnomAD | r2.1, exomes only (remapped) | r2.1, exomes only

### Dynamic annotation sources
These annotation sources are pulled on the fly. The version number is not
guaranteed given that the data is cached in the mongo database.

API | URL | Note
--- | --- | ---
My Variant Info | https://myvariant.info | Includes many annotation sources, see https://docs.myvariant.info/en/latest/doc/data.html
Mutation Assessor | http://mutationassessor.org/r3/
