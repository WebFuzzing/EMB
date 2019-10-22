# OCVN web module

This module provides REST endpoints for the services needed, as well as basic security. It depends on the **persistence** module and on the **persitence-mongodb**.

It also provides full authentication and security using Spring Security.
The module is packaged as a jar and can be deployed as a [fat jar](http://docs.spring.io/spring-boot/docs/current/reference/html/howto-build.html).

# Building

The web module is part of its larger dg-toolkit parent build, so you need to build on the parent first.

# Starting the app

Because it gets packaged as a fat jar, starting it is piece of cake:

`java -jar target/web-0.0.1-SNAPSHOT.jar`

This will start everything, including an embedded Tomcat Web server and all the services attached it.

# Endpoints

## A word about format for filters

There are 4 main types of filters, all of them are multivalue
- year - this is just the year. Example: 2014
- bidTypeId - this is the _dd_ of the Bid Type object, all entities are available here `/api/ocds/bidType/all`. Example: 10
- procuringEntityId - this is the _id_ of the procuring entity, all procuring entities are available here `/api/ocds/organization/procuringEntity/all`
For endpoints that may return a lot of data there are two additional filter options
- pageSize - this is the number of items one page will return - default is 100. Max is 1000.
- pageNumber - this is the page number to display, first page is 0.

## Selectors

- Bid Types - `/api/ocds/bidType/all`
- Display Organization details, by Id - `/api/ocds/organization/id/[organizationId]`
- Procuring Entity Search - `/api/ocds/organization/procuringEntity/all?text=[serachText]&pageNumber=[pageNo]&pageSize=[pageSize]`. All parameters are optional.

## Access to raw OCDS data

### Display all OCDS data, paginated

`/api/ocds/release/all?bidTypeId=[bid1]&bidTypeId=[bid2]...&procuringEntityId=[proc1]&procuringEntityId=[proc2]....&year=[year1]&year=[year2]...&pageNumber=[pageNo]&pageSize=[pageSize]`

Example: `/api/ocds/release/all?procuringEntityId=Z002131&pageNumber=0&pageSize=10`

* pageNumber is the page number, zero indexed. if you omit it, it defaults to page=0.
* pageSize is the page size (in number of Release elements) - if you omit it it defaults to 100. The maximum value for size is 1000.`

### Show OCVN Release by projectId

`/api/ocds/release/budgetProjectId/[projectId]`
Example: `/api/ocds/release/budgetProjectId/41067`

### Show OCVN Release by planning bid no

`/api/ocds/release/planningBidNo/[planningBidNo]`
Example: `/api/ocds/release/planningBidNo/20100300191`


## Visualization Endpoints

### Visualization 1 - Cost Effectiveness

Shows the difference between the average tender amount and the average winning bid price annually. 

#### Endpoint 1 - Cost Effectiveness Award Amount

`/api/costEffectivenessAwardAmount?bidTypeId=[bid1]&bidTypeId=[bid2]...&procuringEntityId=[proc1]&procuringEntityId=[proc2]....`


#### Endpoint 2 - Cost Effectiveness Tender Amount

`/api/costEffectivenessTenderAmount?bidTypeId=[bid1]&bidTypeId=[bid2]...&procuringEntityId=[proc1]&procuringEntityId=[proc2]....`


### Visualization 3 - Bidding Period

Visualization of distribution of bidding period (e.g. bid end date - bid start date). Visualization is be a box and whisker plot with: min, quartile 1, median, quartile 3, max

`/api/tenderBidPeriodPercentiles?year=[year1]&year=[year2]...&bidTypeId=[bid1]&bidTypeId=[bid2]...&procuringEntityId=[proc1]&procuringEntityId=[proc2]....`

### Visualization 4 - Funding by Bid Type

A visualization of distribution of funding by type of bid used in tendering process; clustered bar chart by year. 

#### Endpoint 1 - Tender Price By OCDS Types

`/api/tenderPriceByProcurementMethod?bidTypeId=[bid1]&bidTypeId=[bid2]...&procuringEntityId=[proc1]&procuringEntityId=[proc2]....`

#### Endpoint 2 - Tender Price By Vietnam Types

`/api/tenderPriceByBidSelectionMethod?bidTypeId=[bid1]&bidTypeId=[bid2]...&procuringEntityId=[proc1]&procuringEntityId=[proc2]....`


### Visualization 5 - Counts for Tenders, Awards, Bid Plans Per Year

This is a line chart displaying (by year) the COUNT of:
1) Bid plans
2) Tenders
3) Awards


#### Endpoint 1 - Count of Bid Plans Per Year

`/api/countBidPlansByYear?bidTypeId=[bid1]&bidTypeId=[bid2]...&procuringEntityId=[proc1]&procuringEntityId=[proc2]....`

#### Endpoint 2 - Count of Bid Plans Per Year

`/api/countTendersByYear?bidTypeId=[bid1]&bidTypeId=[bid2]...&procuringEntityId=[proc1]&procuringEntityId=[proc2]....`

#### Endpoint 3 - Count of Awards Per Year

`/api/countAwardsByYear?bidTypeId=[bid1]&bidTypeId=[bid2]...&procuringEntityId=[proc1]&procuringEntityId=[proc2]....`

### Visualization 6 - Timeline Chart

Shows the average length of bid period and award period; displays averages by year and is responsive to all filters.

#### Endpoint 1 - Average Tender Period

`/api/averageTenderPeriod?bidTypeId=[bid1]&bidTypeId=[bid2]...&procuringEntityId=[proc1]&procuringEntityId=[proc2]....`

#### Endpoint 2 - Average Award Period

`/api/averageAwardPeriod?bidTypeId=[bid1]&bidTypeId=[bid2]...&procuringEntityId=[proc1]&procuringEntityId=[proc2]....`

## Visualization 7 Largest Tenders/Awards

Table showing the top 10 largest tenders and the 10 largest awards, including amount, tender/award number, supplier and date.

### Endpoint 1 - Top 10 largest tenders

`/api/topTenLargestTenders?bidTypeId=[bid1]&bidTypeId=[bid2]...&procuringEntityId=[proc1]&procuringEntityId=[proc2]....&year=[year1]...`

### Endpoint 2 - Top 10 largest awards

`/api/topTenLargestAwards?bidTypeId=[bid1]&bidTypeId=[bid2]...&procuringEntityId=[proc1]&procuringEntityId=[proc2]....&year=[year1]...`

## Special Use of Cost Effectiveness Tender Amount Endpoint to derive the ordering of comparison charts

It is used to calculate the largest values in the given category. You can use the same endpoint, but with an additional parameter (see last one)

`/api/costEffectivenessTenderAmount?bidTypeId=[bid1]&bidTypeId=[bid2]...&procuringEntityId=[proc1]&procuringEntityId=[proc2]....&groupByCategory=[category]`

When `groupByCategory` is used, the behavior of the endpoint will change, in that it will group results by the values of the category specified by groupByCategory. The categories are among the filter types: "bidTypeId", "procuringEntityId". 

A valid use of the filter would then be 
`/api/costEffectivenessTenderAmount?groupByCategory=bidTypeId` - this would return the tender amounts grouped by bidTypeId and ordered descending. If you just want to get the top 3, you can pass the pageSize parameter.

`/api/costEffectivenessTenderAmount?groupByCategory=bidTypeId&pageSize=3`

We re-used the Endpoint 2 to get this info to ensure the same filtering criteria and grouping calculations used by the Endpoint 2 are used also by the grouping. 
