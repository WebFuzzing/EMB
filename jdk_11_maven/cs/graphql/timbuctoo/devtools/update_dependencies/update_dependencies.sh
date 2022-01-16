#!/bin/bash
cd -P -- "$(dirname -- "$0")" #go to dir of script even if it was called as a symbolic link

cd "$(git rev-parse --show-toplevel)" #go to git root

if sha1sum -c ./third_party/dependenciesGeneratedFor && (( $(find . -name pom.xml | wc -l) + 1 == $(cat ./third_party/dependenciesGeneratedFor | wc -l) )) ; then
  echo "Dependencies up to date"
  exit 0
fi

mvn dependency:tree -Dverbose -DoutputFile=dependencies && \
  find . -name dependencies | xargs node ./devtools/update_dependencies/dependencyparser.js && \
  find . -name dependencies | xargs rm -- && \
  find . -name pom.xml | xargs sha1sum > ./third_party/dependenciesGeneratedFor && \
  sha1sum ./devtools/update_dependencies/dependencyparser.js >> ./third_party/dependenciesGeneratedFor
