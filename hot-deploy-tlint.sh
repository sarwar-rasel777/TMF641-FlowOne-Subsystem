#!/bin/bash

set -e
set -x

REPOSITORY=repository.int.compax.at:5001
IMAGE=tl-tmf641-flowone-subsystem
TAG=$(id -un)
TIME=$(date -u +'%Y%m%d%H%M%S')
DEPLOYMENT_CONTAINER=aax-tmf641-flowone-subsystem
DEPLOYMENT_POD=aax-jobs
KUBE_CONTEXT=dev-vie
KUBE_NAMESPACE=tlint

mvn clean package -DskipTests=true

docker build -t $REPOSITORY/$IMAGE:$TAG-$TIME .
docker push $REPOSITORY/$IMAGE:$TAG-$TIME

kubectl --context $KUBE_CONTEXT --namespace $KUBE_NAMESPACE set image deployment/$DEPLOYMENT_CONTAINER $DEPLOYMENT_CONTAINER=$REPOSITORY/$IMAGE:$TAG-$TIME

kubectl --context $KUBE_CONTEXT --namespace $KUBE_NAMESPACE rollout restart deployment $DEPLOYMENT_CONTAINER

kubectl --context $KUBE_CONTEXT --namespace $KUBE_NAMESPACE get pods --watch | grep --line-buffered $DEPLOYMENT_CONTAINER
