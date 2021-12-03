#!/usr/bin/env python3

EVOMASTER_VERSION = "1.3.0"

import os
import shutil
import platform
from shutil import copy
from shutil import copytree
from subprocess import run
from os.path import expanduser


### Environment variables ###

HOME = expanduser("~")
SCRIPT_LOCATION = os.path.dirname(os.path.realpath(__file__))
PROJ_LOCATION = os.path.abspath(os.path.join(SCRIPT_LOCATION, os.pardir))


JAVA_HOME_8 = os.environ.get('JAVA_HOME_8', '')
if JAVA_HOME_8 == '':
    print("\nERROR: JAVA_HOME_8 environment variable is not defined")
    exit(1)

JAVA_HOME_11 = os.environ.get('JAVA_HOME_11', '')
if JAVA_HOME_11 == '':
    print("\nERROR: JAVA_HOME_11 environment variable is not defined")
    exit(1)


SHELL = platform.system() == 'Windows'


### Prepare "dist" folder ###
dist = os.path.join(PROJ_LOCATION, "dist")

if os.path.exists(dist):
    shutil.rmtree(dist)

os.mkdir(dist)


def callMaven(folder, jdk_home):
    env_vars = os.environ.copy()
    env_vars["JAVA_HOME"] = jdk_home

    mvnres = run(["mvn", "clean", "install", "-DskipTests"], shell=SHELL, cwd=os.path.join(PROJ_LOCATION,folder), env=env_vars)
    mvnres = mvnres.returncode

    if mvnres != 0:
        print("\nERROR: Maven command failed")
        exit(1)

### Building Maven JDK 8 projects ###
def build_jdk_8_maven() :

    folder = "jdk_8_maven"
    callMaven(folder, JAVA_HOME_8)

    # Copy JAR files
    copy(folder+"/cs/rest/original/features-service/target/features-service-sut.jar", dist)
    copy(folder+"/em/external/rest/features-service/target/features-service-evomaster-runner.jar", dist)

    copy(folder+"/cs/rest/original/scout-api/api/target/scout-api-sut.jar", dist)
    copy(folder+"/em/external/rest/scout-api/target/scout-api-evomaster-runner.jar", dist)

    copy(folder+"/cs/rest/original/proxyprint/target/proxyprint-sut.jar", dist)
    copy(folder+"/em/external/rest/proxyprint/target/proxyprint-evomaster-runner.jar", dist)

    copy(folder+"/cs/rest/original/catwatch/catwatch-backend/target/catwatch-sut.jar",dist)
    copy(folder+"/em/external/rest/catwatch/target/catwatch-evomaster-runner.jar", dist)

    copy(folder+"/cs/rest/artificial/ncs/target/rest-ncs-sut.jar", dist)
    copy(folder+"/em/external/rest/ncs/target/rest-ncs-evomaster-runner.jar", dist)

    copy(folder+"/cs/rest/artificial/scs/target/rest-scs-sut.jar", dist)
    copy(folder+"/em/external/rest/scs/target/rest-scs-evomaster-runner.jar", dist)

    copy(folder+"/cs/rest/artificial/news/target/rest-news-sut.jar", dist)
    copy(folder+"/em/external/rest/news/target/rest-news-evomaster-runner.jar", dist)

    copy(folder+"/cs/rest-gui/ocvn/web/target/ocvn-rest-sut.jar", dist)
    copy(folder+"/em/external/rest/ocvn/target/ocvn-rest-evomaster-runner.jar", dist)

    copy(folder+"/cs/rest/original/languagetool/languagetool-server/target/languagetool-sut.jar",dist)
    copy(folder+"/em/external/rest/languagetool/target/languagetool-evomaster-runner.jar", dist)

    copy(folder+"/cs/rest/original/restcountries/target/restcountries-sut.jar",dist)
    copy(folder+"/em/external/rest/restcountries/target/restcountries-evomaster-runner.jar", dist)

    copy(folder+"/cs/graphql/spring-petclinic-graphql/target/petclinic-sut.jar",dist)
    copy(folder+"/em/external/graphql/spring-petclinic-graphql/target/petclinic-evomaster-runner.jar", dist)



    ind0 = os.environ.get('SUT_LOCATION_IND0', '')
    if ind0 == '':
        print("\nWARN: SUT_LOCATION_IND0 env variable is not defined")
    else:
        copy(ind0, os.path.join(dist, "ind0-sut.jar"))
        copy(folder+"/em/external/rest/ind0/target/ind0-evomaster-runner.jar", dist)



####################
def build_jdk_11_maven() :

    folder = "jdk_11_maven"
    callMaven(folder, JAVA_HOME_11)

    copy(folder+"/cs/rest/cwa-verification-server/target/cwa-verification-sut.jar", dist)
    copy(folder+"/em/external/rest/cwa-verification/target/cwa-verification-evomaster-runner.jar", dist)


####################
def build_jdk_11_gradle() :

    env_vars = os.environ.copy()
    env_vars["JAVA_HOME"] = JAVA_HOME_11
    folder = "jdk_11_gradle"

    gradleres = run(["gradlew", "build", "-x", "test"], shell=SHELL, cwd=os.path.join(PROJ_LOCATION,folder), env=env_vars)
    gradleres = gradleres.returncode

    if gradleres != 0:
        print("\nERROR: Gradle command failed")
        exit(1)


    # Copy JAR files
    copy(folder+"/cs/graphql/patio-api/build/libs/patio-api-sut.jar", dist)
    copy(folder+"/em/external/graphql/patio-api/build/libs/patio-api-evomaster-runner.jar", dist)




# Building JavaScript projects
def buildJS(path, name):
    print("Building '"+name+"' from " + path)
    res = run(["npm", "install"], shell=SHELL, cwd=path).returncode
    if res != 0:
        print("\nERROR installing packages with NPM in " + path)
        exit(1)
    res = run(["npm", "run", "build"], shell=SHELL, cwd=path).returncode
    if res != 0:
        print("\nERROR when building " + path)
        exit(1)

    target = os.path.join(dist, name+"-js")
    # shutil.make_archive(base_name=target, format='zip', root_dir=path+"/..", base_dir=name)
    copytree(path, target)



### Due to the insanity of node_modules, those are off by default
# buildJS(os.path.abspath(os.path.join(PROJ_LOCATION, "js","rest","ncs")), "ncs")
# buildJS(os.path.abspath(os.path.join(PROJ_LOCATION, "js","rest","scs")), "scs")
# buildJS(os.path.abspath(os.path.join(PROJ_LOCATION, "js","rest","cyclotron")), "cyclotron")
# buildJS(os.path.abspath(os.path.join(PROJ_LOCATION, "js","rest","disease-sh-api")), "disease-sh-api")


build_jdk_8_maven()
build_jdk_11_maven()
build_jdk_11_gradle()

### TODO .Net

### Copy JavaAgent library ###
copy(HOME + "/.m2/repository/org/evomaster/evomaster-client-java-instrumentation/"
   + EVOMASTER_VERSION + "/evomaster-client-java-instrumentation-"
   + EVOMASTER_VERSION + ".jar",
   os.path.join(dist, "evomaster-agent.jar"))


zipName = "dist.zip"
if os.path.exists(zipName):
    os.remove(zipName)

print("Creating " + zipName)
shutil.make_archive(base_name=dist, format='zip', root_dir=dist+"/..", base_dir='dist')



print("\n\nSUCCESS\n\n")
