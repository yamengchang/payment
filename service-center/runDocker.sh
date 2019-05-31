#!/bin/bash

mvn clean -P prod package

dockerImageName=i-center
dockerContainerName=c-center
dockerContainerPort=8001


docker stop $dockerContainerName
docker rm $dockerContainerName
docker rmi $dockerImageName

docker build -t $dockerImageName .

docker run  --net=host --name $dockerContainerName -it -p ${dockerContainerPort}:8001 -d $dockerImageName

