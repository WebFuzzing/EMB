#!/bin/bash

cd -P -- "$(dirname -- "$0")" #go to dir of script even if it was called as a symbolic link

cd "$(git rev-parse --show-toplevel)" #go to git root

cd timbuctoo-instancev4

COMMAND="server"
YAML="./example_config.yaml"
DEBUG_PORT="5005"
DEBUG_HALT="n"
FLIGHT_CONTROL=""

OPTIND=1
while getopts ":d:pDf:c:y:hrtC" opt; do
    case "$opt" in
  C)
    CLEAN=true
    ;;
  c)
    COMMAND=$OPTARG
    ;;
  d)
    DEBUG_PORT=$OPTARG
    ;;
  D)
    NO_DEBUG="y"
    ;;
  f)
    FLIGHT_CONTROL="$OPTARG"
    ;;
  h)
    echo -e "$0: run the timbuctoo command with some easier to remember switches\n\n\
    -c    command to run (defaults to server)\n\
    -C    clean state (throw away database and auth files)\n\
    -d    specify debug port\n\
    -D    do not expose a debugging port\n\
    -f    make flight recording\n\
    -p    pause until debugger attaches\n\
    -r    rebuild before launching (skips tests, add -t to run them)\n\
    -t    run tests when rebuilding\n\
    -y    YAML config to load (defaults to $PWD/example_config.yaml)" >&2
    exit 0
    ;;
  p)
    DEBUG_HALT="y"
    ;;
  r)
    REBUILD=1
    ;;
  t)
    RUN_TESTS=1
    ;;
  y)
    YAML=$OPTARG
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

if [ "$REBUILD" = 1 ]; then
  cd ..
  echo "Rebuilding"
  if [ -z "$RUN_TESTS" ]; then
    skipTests="-DskipTests"
  fi
  mvn package $skipTests -q
  if [ $? != 0 ]; then
    xmllint --xpath //file[error] timbuctoo-instancev4/target/checkstyle-result.xml
    exit 1
  fi
  cd -
fi

export timbuctoo_dataPath="./temp_for_debugrun"
export timbuctoo_authPath="./temp_for_debugrun"
export timbuctoo_port="8080"
export timbuctoo_adminPort="8081"
export base_uri="http://localhost:0"
export timbuctoo_elasticsearch_host="localhost"
export timbuctoo_elasticsearch_port="9200"
export timbuctoo_elasticsearch_user="elastic"
export timbuctoo_elasticsearch_password="changeme"
export timbuctoo_search_url=""
export timbuctoo_indexer_url="http://localhost:3000"


if [ "$CLEAN" = "true" ]; then
  echo "Removing database and auth dirs"
  [ -d ./"$timbuctoo_dataPath"/authorizations ] && rm -r ./"$timbuctoo_dataPath"/authorizations
  [ -d ./"$timbuctoo_dataPath"/datasets ] && rm -r ./"$timbuctoo_dataPath"/datasets
  [ -d ./"$timbuctoo_dataPath"/neo4j ] && rm -r ./"$timbuctoo_dataPath"/neo4j
  [ -e ./"$timbuctoo_dataPath"/logins.json ] && rm ./"$timbuctoo_dataPath"/logins.json
  [ -e ./"$timbuctoo_dataPath"/users.json ] && rm ./"$timbuctoo_dataPath"/users.json
fi

[ -d ./"$timbuctoo_dataPath"/authorizations ] || mkdir -p ./"$timbuctoo_dataPath"/authorizations
[ -d ./"$timbuctoo_dataPath"/neo4j ] || mkdir -p ./"$timbuctoo_dataPath"/neo4j

[ -e ./"$timbuctoo_dataPath"/logins.json ] || echo "[]" > ./"$timbuctoo_dataPath"/logins.json
[ -e ./"$timbuctoo_dataPath"/users.json ] || echo "[]" > ./"$timbuctoo_dataPath"/users.json



if [ -z "$NO_DEBUG" ]; then
	JAVA_OPTS="$JAVA_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=${DEBUG_HALT},address=${DEBUG_PORT}"
fi
if [ -n "$FLIGHT_CONTROL" ]; then
	JAVA_OPTS="$JAVA_OPTS -XX:+UnlockCommercialFeatures -XX:+FlightRecorder -XX:StartFlightRecording=filename=\"$FLIGHT_CONTROL\",dumponexit=true"
fi

export JAVA_OPTS
CMD="./target/appassembler/bin/timbuctoo $COMMAND $YAML"

echo "Changed directory to: $PWD"
echo "JAVA_OPTS=\"$JAVA_OPTS\""
echo running "$CMD"
echo ""
$CMD

result="$?"
if [ "$result" = 130 ]; then
	exit 0 #ctrl-c is also a successful shutdown
else
	exit $result
fi
