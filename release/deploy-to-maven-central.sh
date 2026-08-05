#!/bin/bash

set -eu

if [ $# -lt 2 ]; then
  echo "usage $0 <ssl-key> <version-name> [<param> ...]"
  exit 1;
fi
key=$1
version_name=$2
shift 2

if [[ "$version_name" =~ " " ]]; then
  echo "Version name must not have any spaces"
  exit 3
fi

bazelisk test //...

bash $(dirname $0)/execute-deploy.sh \
  "central-portal" \
  "$version_name" \
  "-Dgpg.keyname=${key}" \
  "$@"
