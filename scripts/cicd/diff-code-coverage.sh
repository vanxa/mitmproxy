#!/bin/bash
#
# /*
#  * Copyright (c) VMware, Inc, 2020. All rights reserved. VMware Confidential.
#  */
#


### Parse commandline options
##############################################################################

# Commandline options. This defines the usage page, and is used to parse cli
# opts & defaults from. The parsing is unforgiving so be precise in your syntax
# - A short option must be preset for every long option; but every short option
#   need not have a long option
# - `--` is respected as the separator between options and arguments
# - We do not bash-expand defaults, so setting '~/app' as a default will not resolve to ${HOME}.
#   you can use bash variables to work around this (so use ${HOME} instead)

# shellcheck disable=SC2015
[[ "${__usage+x}" ]] || read -r -d '' __usage <<-'EOF' || true # exits non-zero when EOF encountered
  -p --virtual-env   [arg]    Virtual python environment.
  -t --target-branch [arg]    Target branch. Default=main
  -r --report        [arg]    Report file.
  -k --api-key       [arg]    Gitea API Key.
  -o --output        [arg]    Report output (html)
  -v                          Enable verbose mode, print script as it is executed
  -d --debug                  Enables debug mode
  -h --help                   This page
  -n --no-color               Disable color output
EOF

# shellcheck disable=SC2015
[[ "${__helptext+x}" ]] || read -r -d '' __helptext <<-'EOF' || true # exits non-zero when EOF encountered
 Run code coverage scan
EOF

source "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/../template.sh"
__root="$(cd ${__dir}/../.. && pwd )"
### Signal trapping and backtracing
##############################################################################

function cleanup_before_exit () {
      info "Cleaning up"
      rm -rf  $__root/diff
      info "Done"
}
trap cleanup_before_exit EXIT INT

# requires `set -o errtrace`
err_report() {
    local error_code
    error_code=${?}
    error "Error in ${__file} in function ${1} on line ${2}"
    exit ${error_code}
}
# Uncomment the following line for always providing an error backtrace
# trap 'err_report "${FUNCNAME:-.}" ${LINENO}' ERR


### Command-line argument switches (like -d for debugmode, -h for showing helppage)
##############################################################################

# debug mode
if [[ "${arg_d:?}" = "1" ]]; then
  set -o xtrace
  LOG_LEVEL="7"
  # Enable error backtracing
  trap 'err_report "${FUNCNAME:-.}" ${LINENO}' ERR
fi

# verbose mode
if [[ "${arg_v:?}" = "1" ]]; then
  set -o verbose
fi

# no color mode
if [[ "${arg_n:?}" = "1" ]]; then
  NO_COLOR="true"
fi

# help mode
if [[ "${arg_h:?}" = "1" ]]; then
  # Help exists with code 1
  help "Help using ${0}"
fi

modules="${__root}/app/src/main/java ${__root}/app-common/src/main/java"
mkdir -p ${__root}/build/reports
if [[ -z ${arg_o} ]]; then
  arg_o=${__root}/build/reports/coverage.json
fi

if [[ -z ${arg_r} ]]; then
  arg_r=${__root}/app/build/reports/jacoco/testCodeCoverageReport/testCodeCoverageReport.xml
fi

if [[ ! -f ${arg_r} ]]; then
  info "Report not found"
  if [[ ! -z ${arg_k} ]];then
    sha=$(git log -1 --format=%H)
        curl -s -X 'POST' \
          "https://git.vanxacloud.com/api/v1/repos/vanxacloud/aetna-cloud/statuses/${sha}" \
          -H 'accept: application/json' \
          -H "Authorization: token ${arg_k}" \
          -H 'Content-Type: application/json' \
          -d "{
          \"context\": \"Code Coverage\",
          \"description\": \"Code coverage: no coverage report\",
          \"state\": \"success\"
        }"
  fi
  exit 0
fi
###################################################EXECUTE###########################################################
if [[ -n ${arg_p} ]]; then
  info "Using virtual environment installed in ${arg_p}"
  source ${arg_p}/bin/activate || emergency "Failed to start virtual environment at ${arg_p}"
else
  info "Setting up python virtual environment"
  python3 -m venv $__root/diff
  source $__root/diff/bin/activate
  python -m pip install --index-url https://nexus.vanxacloud.com/repository/pypi/simple --upgrade pip >/dev/null
  pip install --index-url https://nexus.vanxacloud.com/repository/pypi/simple diff_cover >/dev/null
fi
cd $__root
MERGE_BRANCH="${arg_t}"
if [[ -n "$MERGE_BRANCH" ]]; then
  info "Fetching latest $MERGE_BRANCH"
  git fetch origin 
  diff-cover $arg_r --src-roots $modules --fail-under=80 --compare-branch "origin/$MERGE_BRANCH" --format json:${arg_o} | tee ${__root}/build/coverage.txt
else
  info "Not a merge request pipeline, will print total code coverage for branch"
  cat ${__root}/build/reports/jacoco/index.html | grep -o '<tfoot>.*</tfoot>'
  exit 0
fi
RET=${PIPESTATUS[0]}
if cat ${__root}/build/coverage.txt | grep "No lines with coverage information in this diff"; then
  warning "No diff coverage found. Calculating Total coverage"
  cat ${__root}/app/build/reports/jacoco/test/html/index.html | grep -o '<tfoot>.*</tfoot>'
else
  perc=$(cat ${__root}/build/coverage.txt | grep "Coverage:" | cut -d ":" -f2)
  info "Total coverage:$perc"
  if [[ $RET -ne 0 ]]; then
    error "Code coverage check failed: new code coverage is $perc"
    STATUS="failure"
  else
    STATUS="success"
  fi
  if [[ ! -z ${arg_k} ]]; then
    info "Uploading status"
    sha=$(git log -1 --format=%H)
    curl -s -X 'POST' \
      "https://git.vanxacloud.com/api/v1/repos/vanxacloud/aetna-cloud/statuses/${sha}" \
      -H 'accept: application/json' \
      -H "Authorization: token ${arg_k}" \
      -H 'Content-Type: application/json' \
      -d "{
      \"context\": \"Code Coverage\",
      \"description\": \"Code coverage: ${perc}. Required minimum code coverage: 80%\",
      \"state\": \"${STATUS}\"
    }"
  fi
fi
exit $RET
