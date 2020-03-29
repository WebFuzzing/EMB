#!/usr/bin/env python3

import sys
import re

if len(sys.argv) != 2:
    print("Usage:\n<nameOfScript>.py <version-number>")
    exit(1)


version = sys.argv[1].strip()

versionRegex = re.compile(r"^(\d)+\.(\d)+\.(\d)+(-SNAPSHOT)?$")

if versionRegex.match(version) == None:
    print("Invalid version format")
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


replaceInPom("pom.xml")
replaceInPom("cs/rest/original/scout-api/api/pom.xml")
replaceInPom("cs/rest/original/catwatch/catwatch-backend/pom.xml")
replaceInPom("cs/rest/artificial/news/pom.xml")
replaceInPom("cs/rest/original/features-service/pom.xml")
replaceInPom("cs/rest/original/proxyprint/pom.xml")
replaceInPom("cs/rest-gui/ocvn/web/pom.xml")
