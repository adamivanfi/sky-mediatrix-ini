function navigate(rSession,customerid)
  On Error Resume Next
                rSession.WaitForEvent rcKbdEnabled, "30", "0", 1, 1
                rSession.TransmitTerminalKey rcIBMPf4Key
                rSession.WaitForEvent rcKbdEnabled, "30", "0", 1, 1
    rSession.WaitForEvent rcEnterPos, "30", "0", 10, 43
    rSession.WaitForDisplayString ":", "30", 10, 41
    rSession.TransmitANSI "138"
    rSession.TransmitANSI customerid
    rSession.TransmitTerminalKey rcIBMEnterKey
                'Wscript.Echo rSession.GetDisplayText(1, 1,  rSession.DisplayColumns * rSession.DisplayRows)
  If Err.Number <> 0 Then
    Wscript.Echo "Reflection Client ERROR:" & Err.Number &" Msg:" & Err.Message
    Wscript.Quit -1
  End If
  'Wscript.Echo rSession.Name &":"& rSession.Hostname & ":"  & rSession.SettingsFile & ":"  & "#" & customerid
end function

dim customer=WScript.Arguments(0)
dim premEnv=WScript.Arguments(1)

'Main
Set namedArguments = WScript.Arguments.Named
resultInt = -1
'' check if Customerid is set
If Not ( customer ) Then
    Wscript.Echo "Reflection Client cannot not be started without the CustomerId argument."
                WScript.Quit resultInt
End If

'' get ReflectionSession
On Error Resume Next
Set ReflectionReGIS =    GetObject(,"ReflectionIBM.Session")  'GetObject("RIBM") '

'' exit if reflectionClient not found
If Err.Number <> 0 Then
    Wscript.Echo "Reflection Client for Env:" &premEnv & " not found." &vbNewLine & "Please login into prem2(" & premEnv &")."
    Wscript.Quit -1
End If

'' Check if the Environments (Prem/MX) matches
envConf = Right(ReflectionReGIS.SettingsFile,Len(ReflectionReGIS.SettingsFile)- InStr(ReflectionReGIS.SettingsFile,"\sky\")-4)
If (envConf="Iprem2.rsf" AND premEnv="INT")then
  ' Int MX und Int Reflection
  navigate ReflectionReGIS, customer
  resultInt = 0
else
  If (envConf="prem2.rsf" AND premEnv="PROD")then
   ' Prod MX und Prod Reflection
    navigate ReflectionReGIS, customer
                resultInt = 0
  else
                Wscript.Echo "Reflection Client for Env:" &premEnv & " not found." &vbNewLine & "Please login into prem2("&premEnv &") and make sure you have opened singe Reflection Client window."
  end if
 end if
WScript.Quit resultInt