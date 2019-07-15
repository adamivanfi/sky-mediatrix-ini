dim sblenv
dim customer
dim result
dim wshShell

sblenv=WScript.Arguments(1)
customer=WScript.Arguments(0)
Set wshShell = WScript.CreateObject ("WScript.Shell")
'Set wshShell = new ActiveXObject("WScript.Shell")
mypath = wshShell.ExpandEnvironmentStrings("%ProgramFiles(x86)%")
result = wshShell.Run(""""&mypath&"/sbl81/skyde_siebel_desktop.vbs"" /Service:SKYDE+DMS+Integration+BS /Method:GotoKundenPortal /Env:" & sblenv & " /CustomerId:"& customer,1,true)
if result<0 then MsgBox "Siebel-Applikationsfenster mit aktiven Login wurde nicht gefunden. Kunde:"&customer& " ["&sblenv&"]" end if