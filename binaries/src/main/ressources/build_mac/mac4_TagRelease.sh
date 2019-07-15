#!/bin/bash
find . -name pom.xml -type f -print0 | xargs -0 sed -i ''  's/<version>2.0.3-SNAPSHOT<\/version>/<version>2.0.4-SNAPSHOT<\/version>/g'
find . -name pom.xml -type f -print0 | xargs -0 sed -i ''  's/<ntt.version>2.0.3-SNAPSHOT<\/ntt.version>/<ntt.version>2.0.4-SNAPSHOT<\/ntt.version>/g'
 