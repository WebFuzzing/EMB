#!/bin/sh

git clone -b ${local_ref} /root/timbuctoo /root/build &&  
cd /root/build/ &&
rm -r /root/build/timbuctoo-instancev4/src/spec/resources/specrunstate/database &&
mv /root/timbuctoo-prod-db /root/build/timbuctoo-instancev4/src/spec/resources/specrunstate/database &&
mvn ${maventarget}

mavenResult=$?

mkdir -p /root/timbuctoo/timbuctoo-instancev4/target &&
cp -R /root/build/timbuctoo-instancev4/target/concordion /root/timbuctoo/timbuctoo-instancev4/target/concordion && 
echo 'concordion result has been placed in the ./timbuctoo-instancev4/target folder'

exit $mavenResult
