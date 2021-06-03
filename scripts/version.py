#!/usr/bin/env python3

import sys
import re
import platform
import os
from subprocess import run

if len(sys.argv) != 2:
    print("Usage:\n<nameOfScript>.py <version-number>")
    exit(1)

version = sys.argv[1].strip()

versionRegex = re.compile(r"^(\d)+\.(\d)+\.(\d)+(-SNAPSHOT)?$")

if versionRegex.match(version) == None:
    print("Invalid version format")
    exit(1)


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


def replaceInPom(file):
    regex = re.compile(r'.*<evomaster-version>.*</evomaster-version>.*')
    replacement = '        <evomaster-version>'+version+'</evomaster-version>\n'
    replace(file, regex, replacement)

def replace(file, regex, replacement):

    with open(file, "r") as sources:
        lines = sources.readlines()
    with open(file, "w") as sources:
        for line in lines:

            if regex.match(line):
                sources.write(replacement)
            else:
                sources.write(line)

def replaceInDist():
    regex = re.compile(r'.*EVOMASTER_VERSION.*=.*".*".*')
    replacement = 'EVOMASTER_VERSION = "'+version+'"\n'
    replace("scripts/dist.py", regex, replacement)

def replaceInProperty(file):
    regex = re.compile(r'.*EVOMASTER_VERSION.*=.*')
    replacement = 'EVOMASTER_VERSION='+version+'\n'
    replace(file, regex, replacement)

def replaceInKotlinGradle(file):
    regex = re.compile(r'.*val.*EVOMASTER_VERSION.*=.*')
    replacement = "val EVOMASTER_VERSION = "+version+'\n'
    replace(file, regex, replacement)

# TODO these will be removed once we get rid off of P6Spy
replaceInPom("jdk_8_maven/pom.xml")
replaceInPom("jdk_8_maven/cs/rest/original/scout-api/api/pom.xml")
replaceInPom("jdk_8_maven/cs/rest/original/catwatch/catwatch-backend/pom.xml")
replaceInPom("jdk_8_maven/cs/rest/artificial/news/pom.xml")
replaceInPom("jdk_8_maven/cs/rest/original/features-service/pom.xml")
replaceInPom("jdk_8_maven/cs/rest/original/proxyprint/pom.xml")
replaceInPom("jdk_8_maven/cs/rest-gui/ocvn/web/pom.xml")
replaceInPom("jdk_8_maven/cs/graphql/spring-petclinic-graphql/pom.xml")
replaceInProperty("jdk_11_gradle/cs/graphql/patio-api/gradle.properties")

# is there any easier way for Gradle?
replaceInKotlinGradle("jdk_11_gradle/em/embedded/graphql/patio-api/build.gradle.kts")
replaceInKotlinGradle("jdk_11_gradle/em/external/graphql/patio-api/build.gradle.kts")

replaceInDist()


SHELL = platform.system() == 'Windows'

env_vars = os.environ.copy()
env_vars["JAVA_HOME"] = JAVA_HOME_8


mvnres = run(["mvn", "versions:set", "-DnewVersion="+version], shell=SHELL, cwd=PROJ_LOCATION+"/jdk_8_maven", env=env_vars)
mvnres = mvnres.returncode

if mvnres != 0:
    print("\nERROR: Maven command failed")
    exit(1)