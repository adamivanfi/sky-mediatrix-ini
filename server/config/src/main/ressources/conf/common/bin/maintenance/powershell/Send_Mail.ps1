#20130808 - Inital Script creation

function sendmail($subject, $body)
{
$ReceiverList="klaus.heiter@sky.de"
	#$filename = "Pfad zum Dateianhang"
	$SmtpClient = new-object system.net.mail.smtpClient
	$MailMessage = New-Object system.net.mail.mailmessage
	#$att = new-object Net.Mail.Attachment($filename)
	$SmtpClient.Host = "p-ng-ux07-emmi.premiere.de"
	$mailmessage.from = "skydmsint@sky.de"
	$mailmessage.To.add($ReceiverList)
	$mailmessage.Subject = $subject
	$MailMessage.IsBodyHtml = $false
	$mailmessage.Body = $body
	#$mailmessage.Attachments.Add($att)
	$smtpclient.Send($mailmessage)
}

#sendmail $subject $body