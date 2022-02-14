#!/bin/bash

cd -P -- "$(dirname -- "$0")" #go to dir of script even if it was called as a symbolic link

cd "$(git rev-parse --show-toplevel)" #go to git root

DEBUG_SUSPEND="n"
REBUILD=""
RUN_TESTS=""

OPTIND=1
while getopts ":bCd:hlprt" opt; do
    case "$opt" in
  b)
    USE_BAZEL=true
    ;;
  C)
    CLEAN=true
    ;;
  d)
    DEBUG_PORT=${OPTARG}
    ;;
  h)
    echo -e "$0: run the timbuctoo command with some easier to remember switches\n\n\
    -C    clean state (throw away database and auth files)\n\
    -d    specify debug port (default is 5005)\n\
    -D    do not expose a debugging port\n\
    -p    pause until debugger attaches\n" >&2
    exit 0
    ;;
  l)
    CONTAINER_LOCAL_STATE=y
    ;;
  p)
    DEBUG_SUSPEND=y
    ;;
  r)
    REBUILD=1
    ;;
  t)
    RUN_TESTS=1
    ;;
  \?)
    echo "Invalid option: -$OPTARG use -h for an overview of valid options" >&2
    exit 1
    ;;
  :)
    if [ $OPTARG = "d" ]; then
      echo "please specify a port for the debugger (the usual port is 5005, so -d 5005)" >&2
      exit 1
    elif [ $OPTARG = "f" ]; then
      echo "please specify a file to store the flight recording (e.g. -f recording.jfr)" >&2
      exit 1
    fi
    ;;
  esac
done

docker-compose stop timbuctoo

if [ -n "$USE_BAZEL" ]; then
  bazel build //timbuctoo-instancev4:everything_deploy.jar || exit $?
else
  if [ "$REBUILD" = 1 ]; then
    echo "Rebuilding"
    if [ -z "$RUN_TESTS" ]; then
      skipTests="-DskipTests"
    fi
    mvn package $skipTests -q
    if [ $? != 0 ]; then
      xmllint --xpath //file[error] timbuctoo-instancev4/target/checkstyle-result.xml
      exit 1
    fi
  fi
fi

timbuctoo_dataPath="./timbuctoo-instancev4/temp_for_debugrun"

if [ "$CLEAN" = "true" ]; then
  echo "Removing database and auth dirs"
  [ -d "$timbuctoo_dataPath"/auth/authorizations ] && rm -r "$timbuctoo_dataPath"/auth/authorizations
  [ -d "$timbuctoo_dataPath"/datasets ] && rm -r "$timbuctoo_dataPath"/datasets
  [ -d "$timbuctoo_dataPath"/neo4j ] && rm -r "$timbuctoo_dataPath"/neo4j
  [ -e "$timbuctoo_dataPath"/auth/logins.json ] && rm "$timbuctoo_dataPath"/auth/logins.json
  [ -e "$timbuctoo_dataPath"/auth/users.json ] && rm "$timbuctoo_dataPath"/auth/users.json
fi

[ -d "$timbuctoo_dataPath"/auth/authorizations ] || mkdir -p "$timbuctoo_dataPath"/auth/authorizations
[ -d "$timbuctoo_dataPath"/neo4j ] || mkdir -p "$timbuctoo_dataPath"/neo4j
[ -e "$timbuctoo_dataPath"/auth/logins.json ] || echo "[]" > "$timbuctoo_dataPath"/auth/logins.json
[ -e "$timbuctoo_dataPath"/auth/users.json ] || echo "[]" > "$timbuctoo_dataPath"/auth/users.json

if [ -n "$DEBUG_PORT" ]; then
	JAVA_OPTS="$JAVA_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=${DEBUG_SUSPEND},address=5005"
fi

export JAVA_OPTS
echo "JAVA_OPTS=\"$JAVA_OPTS\""

if [ -n "$USE_BAZEL" ]; then
  export CMD='sh -c "java $JAVA_OPTS -jar /app/bazel-bin/timbuctoo-instancev4/everything_deploy.jar server /app/example_config.yaml"'
fi

echo $PWD
if [ -n "$CONTAINER_LOCAL_STATE" ]; then
  export timbuctoo_dataPath="/root/data"
  export timbuctoo_authPath="/root/data/auth"
fi
docker-compose up -d
docker-compose logs -f

echo -e "\nContainers are still running in the background. Use \n\n    docker-compose stop\n\nto shut them down. (not required for a recompile)\n"
