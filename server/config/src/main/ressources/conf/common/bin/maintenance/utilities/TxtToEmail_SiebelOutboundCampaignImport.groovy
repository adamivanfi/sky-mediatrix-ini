def srcDir ='D:\\tmp\\SiebelOutboundCampaignImport'
def recipients="skydmsprod@sky.de";
//def recipients="gregor.meinusch@sky.de";
def srcFilter = ~/(?!Thumbs.db)(.*\.txt)/

new File(srcDir).eachFileMatch(srcFilter){ efile ->
     def subject=''
     efile.eachLine {    
       if (it.startsWith('Subject: ')) {
          subject=it.replaceAll('Subject: ', '');
       }
     }
     println  "${efile.name} ${subject}"     
     sendTextMail(recipients, subject , efile.text);    
}
     

def sendTextMail(String recipients, String subject, String txtmessage){
    Session session=null
    Transport transport=null
    try {
        Properties properties = new Properties()
        properties.put("mail.smtp.port", "25");
        properties.put("mail.smtp.host", "p-ng-ux07-emmi.premiere.de");    
        properties.put("mail.transport.protocol", "smtp");              
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication("skydmsprod",  "bAntarw8");
            }
        }
        session = new Session(properties, auth); 
        transport=session.getTransport()
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("skydmsint@sky.de"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        message.setSubject(subject);
        message.setText(txtmessage)
        //println "sending message:" + recipients+":"+(subject)
        transport.send(message);
     } catch (MessagingException e) {
         e.printStackTrace();
     } finally {
        if (transport) transport.close()
     }
}

     