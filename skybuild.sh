#! /bin/bash
if [ -e mac0_Config.sh ]; then
    . mac0_Config.sh
    chmod +x $SCRIPT_PATH/*.sh
else
    chmod +x binaries/src/main/ressources/build_mac/*.sh
    . binaries/src/main/ressources/build_mac/mac0_Config.sh
fi


while getopts '123456scfilhp' flag; do
  case ${flag} in
    i) . $SCRIPT_PATH/mac1_Deployment_UpdateLocalMavenRepo.sh
        pushd $WORKSPACE_PATH
        $MVN_EXEC -o clean install
        valid $1
        popd
        ;;
    l) . $SCRIPT_PATH/mac2_build.sh -f ;;
    3) . $SCRIPT_PATH/mac3_prepareRemoteInstall.sh  ;;
    5) . $SCRIPT_PATH/mac5_getFilesFromItyx.sh  ;;
    6) $SCRIPT_PATH/mac6_StageFromLocalToSkyFTP.sh -c;;
    s) $SCRIPT_PATH/mac2_build.sh
       $SCRIPT_PATH/mac3_prepareRemoteInstall.sh
       $SCRIPT_PATH/mac6_StageFromLocalToSkyFTP.sh -s
       ;;
    c)
       pushd "${WORKSPACE_PATH}/../git_ityx_mxclient"
            git pull
            diffFiles ../sky_dms/mxclient/
            read -p "Press Enter after merge finish"
            git add . && git commit -m "bugfix" && git push
       popd
       #echo "I will wait 2'" ; date ; sleep 120
       $SCRIPT_PATH/mac2_build.sh -f
       #echo "I will wait 15' for continous Build from ITyX" ; date ;  sleep 900
       #echo "Wait 15' for continous Build from ITyX" ; date ;
       date;
       read -p "Press Enter to get signed files from ITyX-FTP"
       
       $SCRIPT_PATH/mac5_getFilesFromItyx.sh
       $SCRIPT_PATH/mac3_prepareRemoteInstall.sh
       $SCRIPT_PATH/mac6_StageFromLocalToSkyFTP.sh -c
     ;;
    p) echo "getFilesFrom ITYX FTP and pack them to SKY FTP"
       $SCRIPT_PATH/mac5_getFilesFromItyx.sh
       $SCRIPT_PATH/mac3_prepareRemoteInstall.sh
       $SCRIPT_PATH/mac6_StageFromLocalToSkyFTP.sh -c
       ;;
    f)
       pushd "${WORKSPACE_PATH}/../git_ityx_mxclient"
           git pull
           diffFiles ../sky_dms/mxclient/
           read -p "Press Enter after merge finish"
            git add . && git commit -m "bugfix" && git push
       popd
       $SCRIPT_PATH/mac2_build.sh -f
       echo "Wait 15' for continous Build from ITyX" ; date ;
       # sleep 300
       read -p "Pres Enter to pull the files from ityx"
       $SCRIPT_PATH/mac5_getFilesFromItyx.sh -f
       $SCRIPT_PATH/mac3_prepareRemoteInstall.sh
       $SCRIPT_PATH/mac6_StageFromLocalToSkyFTP.sh -f
      ;;
     h)
      echo "Options:
      i - inital build, update Mvn-Repo
      l - buildAll local
      3 - prepare remote install
      5 - stage from ItYx to Local
      6 - stage from Local to Sky-FTP
      s - build Server-Extensions only
      c - build Client- and Server Extensions
      f - build FullRelease
      "
     ;;
    *) error "Unexpected option ${flag}" ;;
  esac
done


