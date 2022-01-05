#!/bin/sh

cd -P -- "$(dirname -- "$0")" #go to dir of script even if it was called as a symbolic link

if ! which inotifywait; then
  if [ -f /.dockerenv ]; then
      echo "inotifywait not available, we're running inside a container so we'll install it.";
      dnf install -y inotify-tools
      if ! which inotifywait; then
        echo "inotifywait installation failed"
        exit 1
      fi
  else
      echo "inotifywait not available. Please install it.";
  fi
fi
./build.sh
while inotifywait -r ../../timbuctoo-instancev4/src ../; 
  do ./build.sh
done