#!/usr/bin/env python3

EVOMASTER_VERSION = "0.4.0"

import os
import shutil
from subprocess import call
from os.path import expanduser


HOME = expanduser("~")
SCRIPT_LOCATION = os.path.dirname(os.path.realpath(__file__))
PROJ_LOCATION = os.path.abspath(os.path.join(SCRIPT_LOCATION, os.pardir))

mvnres = call(["mvn", "clean", "install", "-DskipTests"], cwd=PROJ_LOCATION, shell=True)

if mvnres != 0:
    print("\nERROR: Maven command failed")
    exit(1)

dist = os.path.join(PROJ_LOCATION, "dist")

if os.path.exists(dist):
    shutil.rmtree(dist)

os.mkdir(dist)


def cp(target, dist):
    print("Copying " + target)
    cpres = call(["cp", target, dist], cwd=PROJ_LOCATION, shell=True)
    if cpres != 0:
        print("\nERROR: Failed to copy " + target + " into " + dist)
        exit(1)


cp("cs/rest/original/features-service/target/features-service-sut.jar", dist)
cp("em/external/rest/features-service/target/features-service-evomaster-runner.jar", dist)

cp("cs/rest/original/scout-api/api/target/scout-api-sut.jar", dist)
cp("em/external/rest/scout-api/target/scout-api-evomaster-runner.jar", dist)

cp("cs/rest/original/proxyprint/target/proxyprint-sut.jar", dist)
cp("em/external/rest/proxyprint/target/proxyprint-evomaster-runner.jar", dist)

cp("cs/rest/original/catwatch/catwatch-backend/target/catwatch-sut.jar",dist)
cp("em/external/rest/catwatch/target/catwatch-evomaster-runner.jar", dist)

cp("cs/rest/artificial/ncs/target/rest-ncs-sut.jar", dist)
cp("em/external/rest/ncs/target/rest-ncs-evomaster-runner.jar", dist)

cp("cs/rest/artificial/scs/target/rest-scs-sut.jar", dist)
cp("em/external/rest/scs/target/rest-scs-evomaster-runner.jar", dist)

cp("cs/rest/artificial/news/target/rest-news-sut.jar", dist)
cp("em/external/rest/news/target/rest-news-evomaster-runner.jar", dist)




cp(HOME + "/.m2/repository/org/evomaster/evomaster-client-java-instrumentation/"
   + EVOMASTER_VERSION + "/evomaster-client-java-instrumentation-"
   + EVOMASTER_VERSION + ".jar",
   os.path.join(dist, "evomaster-agent.jar"))

zipName = "dist.zip"
if os.path.exists(zipName):
    os.remove(zipName)

print("Creating " + zipName)
shutil.make_archive("dist", 'zip', "dist")


#### For some SUTs depending on Docker, we do not run them on cluster yet, so no point
#### to be in the zip file, although we still want to put on same folder to run experiments
#### locally.


cp("cs/rest-gui/ocvn/web/target/ocvn-rest-sut.jar", dist)
cp("em/external/rest/ocvn/target/ocvn-rest-evomaster-runner.jar", dist)


ind0 = os.environ.get('SUT_LOCATION_IND0', '')
if ind0 == '':
    print("\nWARN: SUT_LOCATION_IND0 env variable is not defined")
else:
    cp(ind0, os.path.join(dist, "ind0-sut.jar"))
    cp("em/external/rest/ind0/target/ind0-evomaster-runner.jar", dist)

print("\n\nSUCCESS\n\n")
