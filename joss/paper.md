---
title: 'EMB: A Curated Corpus of Web/Enterprise Applications for Scientific Research in Software Engineering'
tags:
  - JVM
  - Java
  - Kotlin
  - NodeJS
  - JavaScript
  - TypeScript
  - .Net
  - C#
  - SBST
  - search-based software engineering
  - test generation
  - system testing
  - fuzzing
  - REST
  - GraphQL 
  - benchmark

authors:
  - name: Andrea Arcuri
    orcid: 0000-0003-0799-2930
    affiliation: 1
  - name: Man Zhang
    orcid: 0000-0003-1204-9322
    affiliation: 1
  - name: Amid Golmohammadi
    orcid: 0000-0002-2324-5794
    affiliation: 1
  - name: Asma Belhadi
    orcid: 0000-0002-7103-2179
    affiliation: 1
  - name: Juan Pablo Galeotti
    orcid: 0000-0002-0747-8205
    affiliation: 2
affiliations:
  - name: Kristiania University College, Department of Technology, Oslo, Norway
    index: 1
  - name: FCEyN-UBA, and ICC, CONICET-UBA, Depto. de Computaci\'on, Buenos Aires, Argentina
    index: 2
date:  February 2022

bibliography: paper.bib
---

# Summary

In this repository,
we collected several different systems, in different programming languages, like
Java, Kotlin, JavaScript and C#.
In this documentation, we will refer to these projects as Systems Under Test (SUTs).
Currently, the SUTs are either _REST_ or _GraphQL_ APIs.

For each SUT, we implemented _driver_ classes, which can programmatically _start_, _stop_ and _reset_ the state of SUT (e.g., data in SQL databases).
As well as enable setting up different properties in a _uniform_ way, like choosing TCP port numbers for the HTTP servers.
If a SUT uses any external services (e.g., a SQL database), these will be automatically started via Docker in these driver classes.

# Statement of Need

This collection of SUTs was originally assembled for easing experimentation with the fuzzer called _EvoMaster_ [@arcuri2021evomaster].
However, finding this type of applications is not trivial among open-source projects.
Furthermore, it is not simple to sort out all the technical details on how to set these applications up and start them in a simple, uniform approach.

Therefore, this repository provides the important contribution of providing all these necessary scripts and software libraries for researchers that need this kind of case study.


# Software Details

The projects were selected based on searches using keywords on GitHub APIs, using convenience sampling.
Several SUTs were looked at, in which we discarded the ones that would not compile, would crash at startup, would use obscure/unpopular libraries with no documentation to get them started, are too trivial, student projects, etc.
Where possible, we tried to prioritize/sort based on number of _stars_ on GitHub.


Note that some of these open-source projects might be no longer supported, whereas others are still developed and updated.
Once a system is added to EMB, we do not modify nor keep it updated with its current version under development.
The reason is that we want to keep an easy to use, constant set of case studies for experimentation that can be reliably used throughout the years.

The SUTs called _NCS_ (Numerical Case Study) and _SCS_ (String Case Study) are artificial, written by the authors.
They are based on numerical and string-based functions previously used in the literature of unit test generation.
We just re-implemented in different languages, and put them behind a web service.


There are 2 main use cases for EMB:

* Run experiments with _EvoMaster_

* Run experiments with other tools

Everything can be setup by running the script `scripts/dist.py`.
Note that you will need installed at least JDK 8, JDK 11, NPM and .Net 3.x, as well as Docker.
Also, you will need to setup environment variables like `JAVA_HOME_8` and `JAVA_HOME_11`.
The script will issue error messages if any prerequisite is missing.
Once the script is completed, all the SUTs will be available under the `dist` folder, and a `dist.zip` will be created as well (if `scripts/dist.py` is run with `True` as input).

Note that here the drivers will be built as well besides the SUTs, and the SUT themselves will also have an instrumented version (for white-box testing heuristics) for _EvoMaster_ (this is for JavaScript and .Net, whereas instrumentation for JVM is done at runtime, via an attached JavaAgent).


For running experiments with _EvoMaster_, you can also "start" each driver directly from an IDE (e.g., IntelliJ).
Each of these drivers has a "main" method that is running a REST API (binding on default port 40100), where each operation (like start/stop/reset the SUT) can be called via an HTTP message by _EvoMaster_.
For JavaScript, you need to use the files `em-main.js`.


You can also build (and install) each module separately, based on needs.
For example, a Maven module can be installed with:

``mvn clean install -DskipTests``

However, it is important to understand how this repository is structured, to be able to effectively navigate through it.
Each folder represents a set of SUTs (and drivers) that can be built using the same tools.
For example, the folder `jdk_8_maven` contains all the SUTs that need JDK 8 and are built with Maven.
On the other hand, the SUTs in the folder `jdk_11_gradle` require JDK 11 and Gradle.

For JVM and .Net, each module has 2 submodules, called `cs` (short for "Case Study") and `em` (short for "EvoMaster").
`cs` contains all the source code of the different SUTs, whereas `em` contains all the drivers.
Note: building a top-module will build as well all of its internal submodules.

Regarding JavaScript, unfortunately NodeJS does not have a good handling of multi-module projects.
Each SUT has to be built separately.
However, for each SUT, we put its source code under a folder called `src`, whereas all the code related to the drivers is under `em`.

The driver classes for Java and .Net are called `EmbeddedEvoMasterController`.
For JavaScript, they are in a script file called `app-driver.js`.
Note that Java also a different kind of driver called `ExternalEvoMasterController`.
The difference is that in External the SUT is started on a separated process, and not running in the same JVM of the driver itself.


# Published Results

The software implemented for this repository has been already used for some years when carrying out scientific research with the _EvoMaster_ fuzzer (e.g., [@arcuri2019restful; @arcuri2021tt]).
Other research groups have started to use EMB as well for their experiments (e.g., [@stallenberg2021improving]).

# Related Work

As far as we know, there is no existing corpus of web/enterprise applications where scripts/libraries have been provided to easily handle them (e.g., to start/stop/reset them in a programmatic way, including running necessary dependencies like databases via Docker).   


# Acknowledgements

This project has received funding from the European Research Council (ERC) under the European Union’s Horizon 2020 research and innovation programme (grant agreement No 864972), and
partially by UBACyT 2020 20020190100233BA, PICT-2019-01793.

# References

