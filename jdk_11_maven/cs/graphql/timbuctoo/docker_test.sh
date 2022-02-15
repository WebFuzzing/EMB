#!/bin/sh

docker network create --driver=bridge cluster
echo "create instance1"
docker run -p 7080:80 --name=instance1 --hostname=instance1 --detach --net=cluster timbuctoo:resourcesynctest
echo "create instance2"
docker run -p 8080:80 --name=instance2 --hostname=instance2 --detach --net=cluster --env=base_uri=instance2 timbuctoo:resourcesynctest



docker run -p 7080:80 --env=base_uri=http://localhost:7080 timbuctoo:resourcesynctest

