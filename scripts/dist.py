#!/usr/bin/env python3

import os
import shutil
from subprocess import call
from os.path import expanduser

EVOMASTER_VERSION = "0.3.0"

HOME = expanduser("~")
SCRIPT_LOCATION = os.path.dirname(os.path.realpath(__file__))
PROJ_LOCATION = os.path.abspath(os.path.join(SCRIPT_LOCATION, os.pardir))

mvnres = call(["mvn", "clean", "install", "-DskipTests"], cwd=PROJ_LOCATION)

if mvnres != 0:
    print("\nERROR: Maven command failed")
    exit(1)

dist = os.path.join(PROJ_LOCATION, "dist")

if os.path.exists(dist):
    shutil.rmtree(dist)

os.mkdir(dist)


def cp(target, dist):
    cpres = call(["cp", target, dist], cwd=PROJ_LOCATION)
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

zipres = call(["zip", zipName, "-r", "dist"], cwd=PROJ_LOCATION)
if zipres != 0:
    print("\nERROR: Failed to zip dist folder")
    exit(1)

print("\n\nSUCCESS\n\n")
