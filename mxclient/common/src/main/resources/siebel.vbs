

'MsgBox "Argument: " & WScript.Arguments(0),64,"Debug-Only"

'Siebel Application Object
Dim siebApp 'As SiebelHTMLApplication
Dim siebSvcs ' As SiebelService
Dim siebPropSet 'As SiebelPropertySet
Dim bool 'As Boolean
Dim errCode 'As Integer
Dim errText 'As String
Dim connStr 'As String
Dim lng 'As String
'Create The SiebelHTML Object
Set siebApp = CreateObject("Siebel.Desktop_Integration_Application.1")
If Not siebApp Is Nothing Then
	'Create A New Property Set
	Set siebPropSet = siebApp.NewPropertySet
	Set siebOutPropSet = siebApp.NewPropertySet
	If Not siebPropSet Is Nothing Then
		'MsgBox "Calling BS: " & WScript.Arguments(0),64,"Debug-Only"
		siebPropSet.SetProperty "CustomerId", WScript.Arguments(0)
	Else
		errCode = siebApp.GetLastErrCode
		errText = siebApp.GetLastErrText
		MsgBox "Property Set Creation failed: " & errCode & "::" & errText,64,"Debug-Only"
	End If
	
	'Get A Siebel Service
	Set siebSvcs = siebApp.GetService("SKYDE DMS Integration BS")
	If Not siebSvcs Is Nothing Then
	    siebSvcs.InvokeMethod "BringToTop", siebPropSet, siebOutPropSet
		siebSvcs.InvokeMethod "GotoKundenPortal", siebPropSet, siebOutPropSet
	Else
		errCode = siebApp.GetLastErrCode
		errText = siebApp.GetLastErrText
		MsgBox "Could not Get Siebel Service: " & errCode & "::" & errText,64,"Debug-Only"
	End If
	
	Set siebApp = Nothing
End If