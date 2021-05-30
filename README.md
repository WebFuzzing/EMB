# EMB
[EvoMaster](http://evomaster.org) Benchmark (EMB): 
a set of web/enterprise applications for experimentation in automated system testing.

__WARNING__: This repository is going through a major refactoring. 
Most of the documentation is still under construction. 


We collected several different systems, in different programming languages, like
Java, Kotlin, JavaScript and C#.
We also added the drivers for EvoMaster to use those systems. 

Note that some of these open-source projects might be no longer supported, whereas others are still developed and updated.
Once a system is added to EMB, we do not modify nor keep it updated with its current version under development.
The reason is that we want to keep an easy to use, constant set of case studies for experimentation that can be reliably used throughout the years. 


## License
All the code that is new for this repository is released under Apache 2.0 license. 
However, this repository contains as well sources from different open-source 
projects, each one with its own license, as clarified in more details beneath.


## Current Case Studies


### REST: Java/Kotlin

* Features-Service (Apache), from [https://github.com/JavierMF/features-service](https://github.com/JavierMF/features-service)  

* Scout-API (MIT), from [https://github.com/mikaelsvensson/scout-api](https://github.com/mikaelsvensson/scout-api)

* ProxyPrint (Apache), from [https://github.com/ProxyPrint/proxyprint-kitchen](https://github.com/ProxyPrint/proxyprint-kitchen)

* CatWatch (Apache), from [https://github.com/zalando-incubator/catwatch](https://github.com/zalando-incubator/catwatch)

* OCVN (MIT), from [https://github.com/devgateway/ocvn](https://github.com/devgateway/ocvn)

* News (LGPL), from [https://github.com/arcuri82/testing_security_development_enterprise_systems](https://github.com/arcuri82/testing_security_development_enterprise_systems) 

* NCS (not-known license, artificial numerical examples coming from different sources)
 
* SCS (not-known license, artificial string examples coming from different sources)

* Restcountries (MPL), from [https://github.com/apilayer/restcountries](https://github.com/apilayer/restcountries)

* Languagetool (LGPL), from [https://github.com/languagetool-org/languagetool](https://github.com/languagetool-org/languagetool) 


### REST: JavaScript/TypeScript

* Disease-sh-API (GPL), from [https://github.com/disease-sh/API](https://github.com/disease-sh/API)

* Cyclotron (MIT), from [https://github.com/ExpediaInceCommercePlatform/cyclotron](https://github.com/ExpediaInceCommercePlatform/cyclotron)

* NCS (not-known license, artificial numerical examples coming from different sources)
 
* SCS (not-known license, artificial string examples coming from different sources)

### REST: .Net/C# 

* Menu.API (not-known license), from [https://github.com/chayxana/Restaurant-App](https://github.com/chayxana/Restaurant-App)

* SampleProject (MIT), from [https://github.com/kgrzybek/sample-dotnet-core-cqrs-api](https://github.com/kgrzybek/sample-dotnet-core-cqrs-api)

* Library (not-known license), from [https://github.com/KevinDockx/DocumentingAspNetCoreApisWithOpenAPI](https://github.com/KevinDockx/DocumentingAspNetCoreApisWithOpenAPI)


## Build The Systems

### Build JDK_8_MAVEN

The folder `cs` (*case study*) contains the source code of the different 
system under tests (SUT) in this benchmark, for JDK 8 and Maven.

The folder `em` (*EvoMaster*) contains the classes needed to be written to enable
the use of EvoMaster on the SUTs. 
In particular, there are `EmbeddedEvoMasterController` and
`ExternalEvoMasterController` class implementations for each SUT.
Note: usually you would write a EvoMaster controller class in the same module
of the SUTs. 
Here, they are in different modules just to make clear what is needed to implement
to enable the use of EvoMaster.


To compile and generate all the jar files, use the command:

``mvn clean package -DskipTests`` 

Currently, all the case studies do require JDK __8__.
They will not compile with a different version. 

_Note_: the case studies do import EvoMaster as a library. Current SNAPSHOT
versions of the case studies do use the most recent SNAPSHOT version of EvoMaster
(the two versioning numbers are aligned).
We do __NOT__ publish the SNAPSHOT dependencies online.
This means that, if you try to build the project directly, it will fail due to 
missing SNAPSHOT dependencies. 

To use such SNAPSHOT versions, you need first a `mvn install` of EvoMaster on your 
machine (so that the SNAPSHOT jars are created, and put under your `~/.m2` folder).
However, in the Git repository of EMB, we did tag the versions of EMB that are
using the published versions of EvoMaster.
See the [releases](https://github.com/EMResearch/EMB/releases) page.
For example, to use version `X` of EvoMaster, you can check out the Git commit
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

Besides JDK 8, to build from Maven you will also need NPM and NodeJS installed
on your machine (as some of the projects have GUIs built with JS).


### Build DOTNET_3

*Documentation under construction*


### Build JS_NPM

*Documentation under construction*