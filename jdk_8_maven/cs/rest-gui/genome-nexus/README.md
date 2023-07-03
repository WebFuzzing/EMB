# Genome Nexus 🧬

Genome Nexus, a comprehensive one-stop resource for fast, automated and
high-throughput annotation and interpretation of genetic variants in cancer.
Genome Nexus integrates information from a variety of existing resources,
including databases that convert DNA changes to protein changes, predict the
functional effects of protein mutations, and contain information about mutation
frequencies, gene function, variant effects, and clinical actionability.

## Documentation 📖
See the [docs](https://docs.genomenexus.org)

## Run 💻

### Alternative 1 - run genome-nexus, mongoDB and genome-nexus-vep in docker containers
First, set environment variables for Ensembl Release, VEP Assembly, location of VEP Cache, and species (since a mouse instalation is supported). If these are not, the default values from `.env` will be set.

The reference genome and Ensembl release must be consistent with a version in [genome-nexus-importer/data/](https://github.com/genome-nexus/genome-nexus-importer/tree/master/data).
For example `grch37_ensembl92`, `grch38_ensembl92` or `grch38_ensembl95`:
```
export REF_ENSEMBL_VERSION=grch38_ensembl92
```
If you want to setup Genome Nexus for mouse, also set the `SPECIES` variable to 'mus_musculus'. Also see the [docs](https://github.com/genome-nexus/genome-nexus-importer/blob/master/docs/setup-genome-nexus-mouse.md) to create a mouse database.
```bash
export SPECIES=mus_musculus
```

If you would like to do local VEP annotations instead of using the public Ensembl API, please uncomment `# gn_vep.region.url=http://localhost:6060/vep/human/region/VARIANT` in your `application.properties`. This will require you to download the VEP cache files for the preferred Ensembl Release and Reference genome, see our documentation on [downloading the Genome Nexus VEP Cache](https://github.com/genome-nexus/genome-nexus-vep/blob/master/README.md#create-vep-cache). This will take several hours.
```
# Set local cache dir
export VEP_CACHE=<local_vep_cache>

# GRCh38 or GRCh37
export VEP_ASSEMBLY=GRCh38
```

Run docker-compose to create images and containers:
```
docker-compose up --build -d
```

Run without recreating images:
```
docker-compose up -d
```

Run without Genome Nexus VEP:
```
# Start both the Web and DB (dependency of Web) containers
docker-compose up -d web
```

Stop and remove containers:
```
docker-compose down
```

### Alternative 2 - run genome-nexus locally, but mongoDB in docker container
```
# the genomenexus/gn-mongo images comes with all the required tables imported
# change latest to different version if necessary (only need to run this once)
docker run --name=gn-mongo --restart=always -p 27017:27017 -d genomenexus/gn-mongo:latest 
mvn  -DskipTests clean install
java -jar web/target/web-*.war
```

### Alternative 3 - install mongoDB locally and run with local java
Install mongoDB manually. Then follow instructions in
[genome-nexus-importer](https://github.com/genome-nexus/genome-nexus-importer)
to initialize the database.

After that run this:
```
mvn clean install
java -jar web/target/web-*.war
```

## Test Status 👷‍♀️

| branch | master | rc |
| --- | --- | --- |
| status | [![Build Status](https://travis-ci.org/genome-nexus/genome-nexus.svg?branch=master)](https://travis-ci.org/genome-nexus/genome-nexus/branches) | [![Build Status](https://travis-ci.org/genome-nexus/genome-nexus.svg?branch=rc)](https://travis-ci.org/genome-nexus/genome-nexus/branches) |

## Deploy 🚀

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)
