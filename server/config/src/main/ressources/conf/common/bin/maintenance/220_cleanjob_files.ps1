#Variables
$job="120_cleanjob_files"

#Imports
$scriptdir=(Split-Path ((Get-Variable MyInvocation -Scope 0).Value).MyCommand.Path) 
$mediatrix_home="${scriptdir}\..\.."
cd $scriptdir
. ${scriptdir}/900_config.ps1

#Programm
$deleteafter=(Get-Date).AddDays(-$daysToleave)

LogWrite "FileCleaningJob Start by $user on $machine"

#Get-ItemProperty -Path C:\tst.txt | Format-list -Property * -Force


Function CleanFolder{
  Param ([string]$dirtoclean, [int]$daysToleave)
  if (($machine -eq "S-NG-CTEX1-B") -or ($machine -eq "S-NG-MTRIX3-B"))  {
     if ($dirtoclean -and (Test-Path $dirtoclean)) {
	 $deleteafter=(Get-Date).AddDays(-$daysToleave)
	 LogWrite "Execution of CleanFolder by $user on $machine for $dirtoclean started. Delete older than: $deleteafter"
	 $i=1; $j=1
  	 get-childitem -Path $dirtoclean | %{$j=$j+1;$_}  | where-object {$_.CreationTime -lt $deleteafter} | %{LogWrite  ("$i $j "+${_}.CreationTime+" Remove file: "+${_}.name );$i=$i+1;$_}  | Remove-Item -Recurse -Force 
	 Check	
	 LogWriteOk ("Execution of CleanFolder by $user on $machine for $dirtoclean finished checked: "+(${j}-1)+", deleted "+(${i}-1))
     }else{
 	 LogWrite "CleanFolder param not set"  
     }
  }else{
       LogWrite "Execution of CleanFolder is only possible from S-NG-CTEX1-B"  
  }
}


$daysToleave=14
CleanFolder $ctx_fax_dst_dir $daysToleave
CleanFolder $ctx_letter_dst_dir $daysToleave

$daysToleave=5
CleanFolder $ctx_fax1_dir $daysToleave
CleanFolder $ctx_fax2_dir $daysToleave

$daysToleave=1
CleanFolder $ctx_archive_indst $daysToleave
CleanFolder $ctx_archive_inint $daysToleave
CleanFolder $ctx_archive_inpdf $daysToleave
CleanFolder $ctx_archive_insrc $daysToleave
CleanFolder $ctx_archive_inxml $daysToleave

CleanFolder $ctx_archive_oudst $daysToleave
CleanFolder $ctx_archive_ouint $daysToleave
CleanFolder $ctx_archive_oupdf $daysToleave
CleanFolder $ctx_archive_ousrc $daysToleave
CleanFolder $ctx_archive_ouxml $daysToleave

CleanFolder $ctx_archive_ouint $daysToleave

$daysToleave=1
CleanFolder "C:\mediatrix\tmp\OUTBOUND\dst" $daysToleave
CleanFolder "C:\mediatrix\tmp\OUTBOUND\src" $daysToleave
CleanFolder "C:\mediatrix\tmp\INBOUND\dst" $daysToleave
CleanFolder "C:\mediatrix\tmp\INBOUND\src" $daysToleave
CleanFolder "C:\mediatrix\tmp\interim" $daysToleave


LogWrite "FileCleaningJob Complete"




