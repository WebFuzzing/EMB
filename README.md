# EMB
[EvoMaster](http://evomaster.org) Benchmark (EMB): 
a set of web/enterprise applications for experimentation in automated system testing.

The folder `cs` (*case study*) contains the source code of the different 
system under tests (SUT) in this benchmark.

The folder `em` (*EvoMaster*) contains the classes needed to be written to enable
the use of EvoMaster on the SUTs. 
In particular, there are `EmbeddedEvoMasterController` and
`ExternalEvoMasterController` class implementations for each SUT.
Note: usually you would write a EvoMaster controller class in the same module
of the SUTs. 
Here, they are in different modules just to make clear what is needed to implement
to enable the use of EvoMaster.


## License
All the code that is new for this repository is released under Apache 2.0 license. 
However, this repository contains as well sources from different open-source 
projects, each one with its own license, as clarified in more details beneath.


## Build The Projects 

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

## Current Case Studies


### REST

* Features-Service (Apache 2.0), from [https://github.com/JavierMF/features-service](https://github.com/JavierMF/features-service)  

* Scout-API (MIT), from [https://github.com/mikaelsvensson/scout-api](https://github.com/mikaelsvensson/scout-api)

* ProxyPrint (Apache-2.0), from [https://github.com/ProxyPrint/proxyprint-kitchen](https://github.com/ProxyPrint/proxyprint-kitchen)

* CatWatch (Apache-2.0), from [https://github.com/zalando-incubator/catwatch](https://github.com/zalando-incubator/catwatch)

* OCVN (MIT), from [https://github.com/devgateway/ocvn](https://github.com/devgateway/ocvn)

* News (LGPL), from [https://github.com/arcuri82/testing_security_development_enterprise_systems](https://github.com/arcuri82/testing_security_development_enterprise_systems) 

* NCS (not-known license, artificial numerical examples coming from different sources)
 
* SCS (not-known license, artificial string examples coming from different sources)

 
