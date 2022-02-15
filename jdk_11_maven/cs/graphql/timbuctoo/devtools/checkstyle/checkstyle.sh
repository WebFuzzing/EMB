#!/usr/bin/env bash

cd -P -- "$(dirname -- "$0")" #go to dir of script even if it was called as a symbolic link

cd ../../timbuctoo-instancev4/src

find "$PWD/main" "$PWD/test" -name *.java | xargs bazel run //timbuctoo-instancev4:checkstyle -- -c "$PWD/main/resources/checkstyle_config.xml"
