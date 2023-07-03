# Architecture 📐

![Architecture Overview](https://user-images.githubusercontent.com/1334004/63124264-ddd1b680-bf78-11e9-9122-514330e8fcd8.png)

## Backend

Genome Nexus aggregates variant annotation from various sources. There are two types:

1. Small sized annotations are stored as static data in the mongo database directly, see the [genome-nexus-importer repo](https://github.com/genome-nexus/genome-nexus-importer) if you want to update/change this data.
2. Larger annotation sources are pulled on the fly from other APIs and cached in the mongo database.

For a list of all supported variant annotation sources see this Google Sheet:

https://docs.google.com/spreadsheets/d/1xML949NWzJGcvltjlquwSRIv79o13C_sfrVPAU5ci9Q/edit#gid=258442188

The code for the backend can be found here: https://github.com/genome-nexus/genome-nexus.

## REST API

Genome Nexus provides a [REST API](https://www.genomenexus.org/swagger-ui.html) for variant annotation. See [API](API.md)

## API Clients

Clients in various languages can be generated to access it. See [API](API.md)

## Genome Nexus Website

The frontend of the website is here: https://github.com/genome-nexus/genome-nexus-frontend.

## Other Websites

The main consumer of the Genome Nexus REST API is [cBioPortal](https://cbioportal.org). cBioPortal provides visualization, analysis, and download of large-scale cancer genomics data sets. Variants in cBioPortal are annotated using Genome Nexus. The other examples in the figure are potential other consumers. See also: [Tools](Tools.md)

## Command Line Interface

There is a command line tool to annotate MAF and VCF files, see [API](API.md)
