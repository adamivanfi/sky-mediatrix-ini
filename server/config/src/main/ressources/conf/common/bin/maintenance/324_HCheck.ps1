$job241 ="324_hcheck"
. ./900_config.ps1

$scriptdir=(Split-Path ((Get-Variable MyInvocation -Scope 0).Value).MyCommand.Path) 
$mediatrix_home="${scriptdir}\..\.."
cd "${scriptdir}"

& ${groovy_home}\bin\groovy.bat ${scriptdir}\HealthCheck.groovy  
# & ${groovy_home}\bin\groovy.bat ${scriptdir}\324_HealthCheck.groovy  

