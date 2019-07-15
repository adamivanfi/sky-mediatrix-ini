@echo off
REM 20130807 - HEITEK - Initial script creation - Call all powershell Check scripts sequentially
powershell -Noninteractive -noLogo -File "C:\mediatrix\bin\maintenance\powershell\Check_AS_ServiceLogs.ps1"
powershell -Noninteractive -noLogo -File "C:\mediatrix\bin\maintenance\powershell\Check_CX_ErrorFolderFileCount.ps1"
REM powershell -Noninteractive -noLogo -File "C:\mediatrix\bin\maintenance\powershell\Check_FaxServer.ps1"  REM Check_FaxServer.ps1 has unsolved performance problems
powershell -Noninteractive -noLogo -File "C:\mediatrix\bin\maintenance\powershell\Check_Lettershop_SRC-DIR.ps1" REM Check_Lettershop_SRC-DIR.ps1 has unsolved performance problems
powershell -Noninteractive -noLogo -File "C:\mediatrix\bin\maintenance\powershell\Check_PBLetterShare.ps1"