#!/bin/bash

mvn clean -P prod package

dockerImageName=i-pay
dockerContainerName=c-pay
dockerContainerPort=8765


docker stop $dockerContainerName
docker rm $dockerContainerName
docker rmi $dockerImageName

docker build -t $dockerImageName .

docker run --net=host --name $dockerContainerName -it -p ${dockerContainerPort}:8765 -d $dockerImageName

