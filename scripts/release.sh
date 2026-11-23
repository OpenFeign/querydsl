#!/usr/bin/env bash
#
# Copyright 2012-2023 The Feign Authors
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
# in compliance with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software distributed under the License
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
# or implied. See the License for the specific language governing permissions and limitations under
# the License.
#

set -e

if [ -n "$(git status --porcelain)" ]; then
  echo "working tree is not clean, aborting release" >&2
  exit 1
fi

function increment() {
  local version=$1
  result=`echo ${version} | awk -F. -v OFS=. 'NF==1{print ++$NF}; NF>1{if(length($NF+1)>length($NF))$(NF-1)++; $NF=sprintf("%0*d", length($NF), ($NF+1)%(10^length($NF))); print}'`
  echo "${result}-SNAPSHOT"
}

# extract the release version from the pom file
version=`./mvnw -B help:evaluate -N -Dexpression=project.version | sed -n '/^[0-9]/p'`
tag=`echo ${version} | cut -d'-' -f 1`

# determine the next snapshot version
snapshot=$(increment ${tag})

echo "release version is: ${tag} and next snapshot is: ${snapshot}"

./mvnw -B versions:set -DremoveSnapshot -DgenerateBackupPoms=false
git commit -a -s -S -m "prepare release ${tag}"
git tag -s -m "release ${tag}" "${tag}"

./mvnw -B versions:set -DnewVersion="${snapshot}" -DgenerateBackupPoms=false
git commit -a -s -S -m "[ci skip] updating versions to next development iteration ${snapshot}"

echo "pushing tag ${tag}"
git push origin "${tag}"
git push origin HEAD
