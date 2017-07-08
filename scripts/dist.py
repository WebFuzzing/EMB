#!/usr/bin/env python3

import os
import shutil
from subprocess import call

SCRIPT_LOCATION = os.path.dirname(os.path.realpath(__file__))
PROJ_LOCATION = os.path.abspath(os.path.join(SCRIPT_LOCATION, os.pardir))

mvnres = call(["mvn", "-P", "!withEmbedded", "clean", "install", "-DskipTests"], cwd=PROJ_LOCATION)

if mvnres != 0:
    print("Maven command failed")
    exit(1)

dist = os.path.join(PROJ_LOCATION, "dist")

if os.path.exists(dist):
    shutil.rmtree(dist)

os.mkdir(dist)


def cp(target, dist):
    cpres = call(["cp", target, dist], cwd=PROJ_LOCATION)
    if cpres != 0:
        print("Failed to copy " + target + " into " + dist)
        exit(1)


cp("cs/rest/original/features-service/target/features-service.jar", dist)
cp("em/external/rest/features-service/target/features-service-evomaster-runner.jar", dist)

cp("cs/rest/original/scout-api/api/target/scouts-api.jar", dist)
cp("em/external/rest/scout-api/target/scout-api-evomaster-runner.jar", dist)

zipName = "dist.zip"
if os.path.exists(zipName):
    os.remove(zipName)

zipres = call(["zip", zipName, "-r", "dist"], cwd=PROJ_LOCATION)
if zipres != 0:
    print("Failed to zip dist folder")
    exit(1)
