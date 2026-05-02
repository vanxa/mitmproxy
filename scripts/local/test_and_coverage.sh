#!/bin/bash

dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
root="${dir}"/../..
${root}/gradlew --console=plain clean build jacocoTestReport testCodeCoverageReport

diff-cover --src-root ${root}/app/src/main/java --fail-under=80 --compare-branch "origin/main" ${root}/app/build/reports/jacoco/testCodeCoverageReport/testCodeCoverageReport.xml
