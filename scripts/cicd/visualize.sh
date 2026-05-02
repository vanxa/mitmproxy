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
  -m --module      [arg]    Module to run coverage for. Can be repeated.
  -r --report      [arg]    Jacoco report file.
  -o --output      [arg]    Cobertura report file.
  -v                        Enable verbose mode, print script as it is executed
  -d --debug                Enables debug mode
  -h --help                 This page
  -n --no-color             Disable color output
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

function join { local IFS=" "; echo "$*"; }

modules=""
if [[ -n "${arg_m:-}" ]] && declare -p arg_m 2> /dev/null | grep -q '^declare \-a'; then
  for module in "${arg_m[@]}"; do
    modules=$(join $modules "${module}/src/main/java")
  done
elif [[ -n "${arg_m:-}" ]]; then
  modules="${arg_m}"
else
  emergency "Required to provide at least a single module"
fi
mkdir -p ${__root}/build/reports

if [[ -z ${arg_o} ]]; then
  arg_o=${__root}/build/reports/cobertura.xml
fi

if [[ -z ${arg_r} ]]; then
  arg_r=${__root}/build/reports/jacoco/report.xml
fi

[[ -f ${arg_r} ]] || emergency "Report not found"
###################################################EXECUTE###########################################################
cd ${__dir}
python3 cover2cover.py ${arg_r} $modules > ${arg_o}
if which xmllint >/dev/null 2>&1; then
  cov=$(xmllint --xpath "string(//coverage/@branch-rate)" ${arg_o})
  info "Total coverage: $(awk "BEGIN {print ${cov} * 100 }")%"
fi
exit $?
