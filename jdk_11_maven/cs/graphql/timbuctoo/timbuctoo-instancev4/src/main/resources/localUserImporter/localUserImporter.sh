#!/usr/bin/env bash

URL="${2}/tasks/addusers"
FILE=$1
while read LINE;
do
  echo $LINE
  if [[ $LINE != --* ]] && [[ $LINE != "" ]];
  then
    VALUES=($(echo $LINE | tr ";" "\n"))

    if [[ -z $VALUES[0] ]]  && [[ -z $VALUES[1] ]] && [[ -z $VALUES[2] ]];
    then
      echo "Ignore line $LINE"
      continue
    fi

    REQUEST="userPid=${VALUES[0]}&userName=${VALUES[1]}&password=${VALUES[2]}&givenName=${VALUES[3]}&surname=${VALUES[4]}&emailAddress=${VALUES[5]}&organization=${VALUES[6]}&vreId=${VALUES[7]}&vreRole=${VALUES[8]}"

    echo -e ${REQUEST}
    RESPONSE=$(curl -X POST --data "$REQUEST" -H "Content-Type:application/x-www-form-urlencoded" ${URL})

    echo -e ${RESPONSE}
  fi
done < ${FILE}
