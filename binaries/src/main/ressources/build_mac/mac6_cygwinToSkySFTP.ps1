$USERNAME=$Env:USERNAME
$CRED="PFAD\adm${USERNAME}"

$FTPHOST="sul.sky.de"
$FTPUSER="SFTPdms_Mediatrix"
$FTPPASSWD="kuChec3cut" 

$WORKSPACE="C:\Development\Sky\sky_dms"
$NTTUSERNAME="mediatrix"
if ($USERNAME -match "admmein49" -or $USERNAME -match "mein49" -or $USERNAME -match "meinug"){
  $WORKSPACE="V:\Development\Sky\sky_dms"
  $NTTUSERNAME="meinusch"
}
elseif ($USERNAME -match "admlope22" -or $USERNAME -match "lope22" -or $USERNAME -match "lopezr"){
  $WORKSPACE="c:\Dev\Sky_Migrate\sky_dms"
#  $NTTUSERNAME="lopezr"
}
elseif ($USERNAME -match "admroth101" -or $USERNAME -match "roth101" -or $USERNAME -match "xrotj2"){
  $WORKSPACE="c:\Dev\Sky\sky_dms"
#  $NTTUSERNAME="XROTJ2"
}
elseif ($USERNAME -match "IVANFA") {
  $WORKSPACE="C:\Users\ivanfa\Development\SKY\sky_dms"
#  $NTTUSERNAME="IVANFA"
}

$RENCI="${WORKSPACE}\binaries\src\main\ressources\build_mac\Renci.SshNet.dll"
$SRC="${WORKSPACE}\binaries\target\main"
 
$FTPFILE="deployments\${NTTUSERNAME}_MX4Sky.zip" 
$ZIPFILE="${SRC}\${NTTUSERNAME}_MX4Sky.zip"

# Test:
# Write-Host "( RENCI ist:  $($RENCI) )"
Add-Type -Path ${RENCI}
date
write-host "Uploading file:  ${ZIPFILE} to: ${FTPHOST}"
$sftp = New-Object Renci.SshNet.SftpClient($FTPHOST, $FTPUSER, $FTPPASSWD)
$sftp.Connect();
$of=[System.IO.File]::OpenRead("${ZIPFILE}");
$sftp.UploadFile( $of, $FTPFILE, $null );
$of.Close();
$sftp.Disconnect();
date

