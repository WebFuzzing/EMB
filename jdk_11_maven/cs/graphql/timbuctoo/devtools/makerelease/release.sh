#!/usr/bin/env bash

cd -P -- "$(dirname -- "$0")" #go to dir of script even if it was called as a symbolic link

cd ../..
set -o errexit

currentversion=$(grep "<\!--marker for release.sh-->" pom.xml | grep -o '[^>]\+-SNAPSHOT')
currentversionNoSnapshot=${currentversion%-SNAPSHOT}

read -p "The current version is '$currentversionNoSnapshot' What should the next version be? e.g. 4.1.0 " newversion

if [ -z "$newversion" ]; then
  exit 1
fi

cat pom.xml | sed "s/$currentversion<\/version><\!--marker for release.sh-->/$currentversionNoSnapshot<\/version><\!--marker for release.sh-->/" > pom.changed
mv pom.changed pom.xml

git add pom.xml
git commit -m "Release $currentversionNoSnapshot"

git checkout stable
git merge -q --ff-only master

tagname=v$currentversionNoSnapshot
git tag $tagname

git checkout master

cat pom.xml | sed "s/$currentversionNoSnapshot<\/version><\!--marker for release.sh-->/${newversion}-SNAPSHOT<\/version><\!--marker for release.sh-->/" > pom.changed
mv pom.changed pom.xml

git add pom.xml
git commit -m "Start developing $newversion"

git diff HEAD^^ HEAD^
git diff HEAD^ HEAD

read -p "The above changes were made by this script. Can I push it? [yn]" PUSH_IT

if [ "$PUSH_IT" = y ]; then
  git push origin master
  git push origin stable
  git push origin $tagname
fi
