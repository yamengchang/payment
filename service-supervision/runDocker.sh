#!/bin/bash

mvn clean -P prod package

dockerImageName=i-supervision
dockerContainerName=c-supervision
dockerContainerPort=8109


docker stop $dockerContainerName
docker rm $dockerContainerName
docker rmi $dockerImageName

docker build -t $dockerImageName .

docker run --net=host --name $dockerContainerName -it -p ${dockerContainerPort}:8109 -d $dockerImageName

