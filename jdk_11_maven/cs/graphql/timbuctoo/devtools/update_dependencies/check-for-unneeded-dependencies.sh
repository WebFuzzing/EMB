#!/bin/bash
cd -P -- "$(dirname -- "$0")" #go to dir of script even if it was called as a symbolic link

cd "$(git rev-parse --show-toplevel)" #go to git root

if [ -z "$1" ]; then
  echo "Please specify the start and end line of the dependency array. (start is line with first dependency end is line with closing ]"
  exit 1
fi
line="$1"

while [ $line -lt "$2" ]; do
  sed ${line}d BUILD.src > BUILD
  echo -en "\r$line" >&2
  if bazel build //:everything &> /dev/null; then
    echo -n "${line}d;"
  fi
  let line=$line+1
done
echo -e "\r" >&2
