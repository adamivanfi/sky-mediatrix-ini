## Correct way to instantiate
# . $((Split-Path ((Get-Variable MyInvocation -Scope 0).Value).MyCommand.Path) + "\900_config.ps1")

$error.clear()
$machine=$Env:computername 
$user=$Env:USERNAME
$TS=(Get-Date -format "yyyy-MM-dd_HH:mm:ss")

if (-not $mediatrix_home){
   $mediatrix_home="C:\mediatrix"
}

$scriptdir="${mediatrix_home}\bin\maintenance"
cd $scriptdir

$backupdir="D:\backup"
$groovy_home="C:\mediatrix_tools\groovy\"

if (Test-Path ${scriptdir}\910_config_${machine}.ps1 ) {
  . ${scriptdir}\910_config_${machine}.ps1
}

. ${scriptdir}\921_commonlogging.ps1
. ${scriptdir}\922_dbconnections.ps1


$TS=(Get-Date -format "yyyy-MM-dd_HH:mm:ss")
$backupdir="C:\backup"

## Ommit some functions if executing on Cold-Standby-Node
#$isColdStandby = isColdStandby
#Write-host "isColdStandby=" $isColdStandby 
## or
#  If (isColdStandby){
Function isColdStandby(){
  If (Test-Path "${mediatrix_home}\ColdStandby.txt"){
    return $true
  }Else{
    return $false
  }
}


## Breaks the execution with ErrorMessage
#coldStandbyProtection
Function coldStandbyProtection(){
  If (isColdStandby){
    $logstring="!!!!!!!!!!!!!!!!! EXECUTION not allowed on COLD STANDBY-Machine! !!!!!!!!!!!!!!!!!!!!!!"
    $TSS=(Get-Date -format "yyyy-MM-dd_HH:mm:ss")
    Add-content $Logfile -value "$TSS  $logstring"
    Write-Host -foregroundcolor yellow "$TSS  $logstring"
    Write-Host -foregroundcolor yellow "$TSS  $logstring"
    Write-Host -foregroundcolor yellow "$TSS  $logstring"
    throw("Break because of ColdStandbyProtection !!!")
  }
}