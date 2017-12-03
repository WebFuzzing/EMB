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

## Current Case Studies

To compile and generate all the jar files, use the command:

``mvn  -P '!withEmbedded' clean package -DskipTests`` 

Depending on which shell/commandline you use, you might need to remove the
 `''` from around `'!withEmbedded'`.

### REST

* Features-Service (Apache 2.0), from [https://github.com/JavierMF/features-service]()  

* Scout-API (MIT), from [https://github.com/mikaelsvensson/scout-api]()

* ProxyPrint (Apache-2.0), from [https://github.com/ProxyPrint/proxyprint-kitchen]()

* CatWatch (Apache-2.0), from [https://github.com/zalando-incubator/catwatch]()

<!---
Currently issues with MongoDB handling
* OCVN (MIT), from [https://github.com/devgateway/ocvn]()
-->

* News (LGPL), from [https://github.com/arcuri82/testing_security_development_enterprise_systems]() 

* NCS (not-known license, artificial numerical examples coming from different sources)
 
* SCS (not-known license, artificial string examples coming from different sources)

 
