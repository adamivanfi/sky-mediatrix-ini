#!/bin/bash
if [ -e mac0_Config.sh ]; then
    . mac0_Config.sh
else
    . binaries/src/main/ressources/build_mac/mac0_Config.sh
fi

fflag=false
while getopts 'f' flag; do
  case "${flag}" in
    f) fflag=true
    ;;  ## make full deployment instead of ntt* libs
    *) error "Unexpected option ${flag}" ;;
  esac
done 

 if [ $fflag == false ] ; then
    BuildMaven "server"
    valid $1
 else
    BuildMaven "${WORKSPACE_PATH}"
    valid $1
 fi
