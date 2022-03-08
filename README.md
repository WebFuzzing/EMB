# EMB

[![DOI](https://zenodo.org/badge/94008854.svg)](https://zenodo.org/badge/latestdoi/94008854)



[EvoMaster](http://evomaster.org) Benchmark (EMB): 
a set of web/enterprise applications for scientific research in Software Engineering.

We collected several different systems, in different programming languages, like
Java, Kotlin, JavaScript and C#.
In this documentation, we will refer to these projects as System Under Test (SUT).
Currently, the SUTs are either _REST_ or _GraphQL_ APIs.

For each SUT, we implemented _driver_ classes, which can programmatically _start_, _stop_ and _reset_ the state of SUT (e.g., data in SQL databases).
As well as enable setting up different properties in a _uniform_ way, like choosing TCP port numbers for the HTTP servers. 
If a SUT uses any external services (e.g., a SQL database), these will be automatically started via Docker in these driver classes. 


This collection of SUTs was originally assembled for easing experimentation with the fuzzer called [EvoMaster](http://evomaster.org).
However, finding this type of applications is not trivial among open-source projects. 
Furthermore, it is not simple to sort out all the technical details on how to set these applications up and start them in a simple, uniform approach. 
Therefore, this repository provides the important contribution of providing all these necessary scripts for researchers that need this kind of case study.   


## License
All the code that is new for this repository (e.g., the driver classes) is released under Apache 2.0 license. 
However, this repository contains as well sources from different open-source 
projects, each one with its own license, as clarified in more details beneath.

## Example

To see an example of using these drivers with EvoMaster to generate test cases, you can look at this [short video](https://youtu.be/3mYxjgnhLEo) (5 minutes).
  
## Current Case Studies

The projects were selected based on searches using keywords on GitHub APIs, using convenience sampling.
Several SUTs were looked at, in which we discarded the ones that would not compile, would crash at startup, would use obscure/unpopular libraries with no documentation to get them started, are too trivial, student projects, etc.
Where possible, we tried to prioritize/sort based on number of _stars_ on GitHub.


Note that some of these open-source projects might be no longer supported, whereas others are still developed and updated.
Once a system is added to EMB, we do not modify nor keep it updated with its current version under development.
The reason is that we want to keep an easy to use, constant set of case studies for experimentation that can be reliably used throughout the years.

The SUTs called _NCS_ (Numerical Case Study) and _SCS_ (String Case study) are artificial, developed by us.
They are based on numerical and string-based functions previously used in the literature of unit test generation.
We just re-implemented in different languages, and put them behind a web service. 



### REST: Java/Kotlin

* Features-Service (Apache), from [https://github.com/JavierMF/features-service](https://github.com/JavierMF/features-service)  

* Scout-API (MIT), from [https://github.com/mikaelsvensson/scout-api](https://github.com/mikaelsvensson/scout-api)

* ProxyPrint (Apache), from [https://github.com/ProxyPrint/proxyprint-kitchen](https://github.com/ProxyPrint/proxyprint-kitchen)

* CatWatch (Apache), from [https://github.com/zalando-incubator/catwatch](https://github.com/zalando-incubator/catwatch)

* OCVN (MIT), from [https://github.com/devgateway/ocvn](https://github.com/devgateway/ocvn)

* News (LGPL), from [https://github.com/arcuri82/testing_security_development_enterprise_systems](https://github.com/arcuri82/testing_security_development_enterprise_systems) 

* Restcountries (MPL), from [https://github.com/apilayer/restcountries](https://github.com/apilayer/restcountries)

* Languagetool (LGPL), from [https://github.com/languagetool-org/languagetool](https://github.com/languagetool-org/languagetool) 

* CWA-Verification-Server (Apache), from [https://github.com/corona-warn-app/cwa-verification-server](https://github.com/corona-warn-app/cwa-verification-server)

* NCS (not-known license, artificial numerical examples coming from different sources)

* SCS (not-known license, artificial string examples coming from different sources)


### REST: JavaScript/TypeScript

* Disease-sh-API (GPL), from [https://github.com/disease-sh/API](https://github.com/disease-sh/API)

* Cyclotron (MIT), from [https://github.com/ExpediaInceCommercePlatform/cyclotron](https://github.com/ExpediaInceCommercePlatform/cyclotron)

* SpaceX-API (Apache-2.0 License), from [https://github.com/r-spacex/SpaceX-API](https://github.com/r-spacex/SpaceX-API)

* Realworld-App (ISC), from [https://github.com/lujakob/nestjs-realworld-example-app](https://github.com/lujakob/nestjs-realworld-example-app)

* NCS (not-known license, artificial numerical examples coming from different sources)

* SCS (not-known license, artificial string examples coming from different sources)


### REST: .NET/C# 

* Menu.API (not-known license), from [https://github.com/chayxana/Restaurant-App](https://github.com/chayxana/Restaurant-App)

* SampleProject (MIT), from [https://github.com/kgrzybek/sample-dotnet-core-cqrs-api](https://github.com/kgrzybek/sample-dotnet-core-cqrs-api)

* NCS (not-known license, artificial numerical examples coming from different sources)

* SCS (not-known license, artificial string examples coming from different sources)


### GraphQL: Java/Kotlin

* Spring-Pet-Clinic (Apache ), from [https://github.com/spring-petclinic/spring-petclinic-graphql]()

* Patio-Api (GPL), from [https://github.com/patio-team/patio-api]()

* Timbuctoo (GPL), from [https://github.com/HuygensING/timbuctoo]()

* NCS (not-known license, artificial numerical examples coming from different sources)

* SCS (not-known license, artificial string examples coming from different sources)


### RPC (Thrift/gRPC): Java

* NCS (not-known license, artificial numerical examples coming from different sources)

* SCS (not-known license, artificial string examples coming from different sources)

## Using This Repository

Due to several reasons, the software in this repository is not published as a library (e.g., on Maven and NPM).
To use EMB, you need to clone this repository:

```
git clone https://github.com/EMResearch/EMB.git
```

There are 2 main use cases for EMB:

* Run experiments with _EvoMaster_

* Run experiments with other tools

Everything can be setup by running the script `scripts/dist.py`.
Note that you will need installed at least JDK 8, JDK 11, NPM and .NET 3.x, as well as Docker.
Also, you will need to setup environment variables like `JAVA_HOME_8` and `JAVA_HOME_11`.
The script will issue error messages if any prerequisite is missing.
Once the script is completed, all the SUTs will be available under the `dist` folder, and a `dist.zip` will be created as well (if `scripts/dist.py` is run with `True` as input).

[//]: # (There is also a Docker file to run `dist.py`, named `build.dockerfile`.)

[//]: # (It can be built with:)

[//]: # ()
[//]: # (```)

[//]: # (docker build -f build.dockerfile -t emb .)

[//]: # (```)

[//]: # ()
[//]: # (The `dist` folder with all SUTs will be under `/emb/dist`. )



Note that here the drivers will be built as well besides the SUTs, and the SUT themselves will also have an instrumented version (for white-box testing heuristics) for _EvoMaster_ (this is for JavaScript and .NET, whereas instrumentation for JVM is done at runtime, via an attached JavaAgent). 

In the built `dist` folder, the files will be organized as follows:

* For JVM: `<name>-sut.jar` will be the non-instrumented SUTs, whereas their executable drivers will be called `<name>-evomaster-runner.jar`.
 Instrumentation can be done at runtime by attaching the `evomaster-agent.jar` JavaAgent. If you are running experiments with EvoMaster, this will be automatically attached when running experiments with `exp.py` (available in the EvoMaster's repository). Or it can be attached manually with JVM option `-Devomaster.instrumentation.jar.path=evomaster-agent.jar` when starting the driver.
* For NodeJS: under the folder `<name>` (for each NodeJS SUT), the SUT is available under `src`, whereas the instrumented version is under `build`.
* For .NET: currently only the instrumented version is available (WORK IN PROGRESS)



For running experiments with EvoMaster, you can also "start" each driver directly from an IDE (e.g., IntelliJ).
Each of these drivers has a "main" method that is running a REST API (binding on default port 40100), where each operation (like start/stop/reset the SUT) can be called via an HTTP message by EvoMaster.
For JavaScript, you need to use the files `em-main.js`.



You can also build (and install) each module separately, based on needs. 
For example, a Maven module can be installed with:

``mvn clean install -DskipTests``

However, it is important to understand how this repository is structured, to be able to effectively navigate through it.
Each folder represents a set of SUTs (and drivers) that can be built using the same tools.
For example, the folder `jdk_8_maven` contains all the SUTs that need JDK 8 and are built with Maven.
On the other hand, the SUTs in the folder `jdk_11_gradle` require JDK 11 and Gradle.

For JVM and .NET, each module has 2 submodules, called `cs` (short for "Case Study") and `em` (short for "EvoMaster").
`cs` contains all the source code of the different SUTs, whereas `em` contains all the drivers.
Note: building a top-module will build as well all of its internal submodules. 

Regarding JavaScript, unfortunately NodeJS does not have a good handling of multi-module projects.
Each SUT has to be built separately.
However, for each SUT, we put its source code under a folder called `src`, whereas all the code related to the drivers is under `em`.

The driver classes for Java and .NET are called `EmbeddedEvoMasterController`.
For JavaScript, they are in a script file called `app-driver.js`.
Note that Java also a different kind of driver called `ExternalEvoMasterController`.
The difference is that in External the SUT is started on a separated process, and not running in the same JVM of the driver itself.



## Old Versions

The release of EMB are linked in version number with the release of EvoMaster, as EvoMaster's libraries are used in the drivers (e.g., to clean databases and configure auth info).
In the Git repository of EMB, we did tag the versions of EMB.
See the [releases](https://github.com/EMResearch/EMB/releases) page.
For example, to use version `X`, you can check out the Git commit
of EMB tagged with version `X`. 
To see the current available tags, from a command-line you can execute:

`git tag`

Then, to switch to a specific tag X (e.g., `v1.0.0`), you can run:

`git checkout tags/v1.0.0`

Finally, if for any reason you need to switch back to the latest snapshot version, you can run:

`git checkout master` 

There is an issue if you try to checkout an old version. 
Not only Java broke backward compatibility with JDK 9, but also Maven...
If you try to build with Maven and get an error regarding
`maven-processor-plugin`, you might have to add manually
the following plugin dependency version:
```
<plugin>
    <groupId>org.bsc.maven</groupId>
    <artifactId>maven-processor-plugin</artifactId>
    <version>3.3.3</version>
</plugin>
```


### Build *develop* Branch

Branch *develop* is using the most recent SNAPSHOT version of _EvoMaster_.
As that is not published online, you need to clone its repository, and build
it locally (see its documentation on how to do it).

To handle JavaScript, unfortunately there is the need for some manual settings.
However, it needs to be done just once. 

You need to create _symbolic_ link inside `EMB\js_npm` that points to the `evomaster-client-js` folder in _EvoMaster_.
How to do this, depends on the Operating System.
Note that in the following, `<some-path>` should be replaced with the actual real paths of where you cloned the _EvoMaster_ and _EMB_ repositories. 

Windows: `mklink /D  C:\<some-path>\EMB\js_npm\evomaster-client-js  C:\<some-path>\EvoMaster\client-js\evomaster-client-js`

Mac: `ln -s /<some-path>/EvoMaster/client-js/evomaster-client-js  /<some-path>/EMB/js_npm/evomaster-client-js`