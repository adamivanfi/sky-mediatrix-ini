def srcDir ='C:\\mediatrix_access_business\\Lernmengen\\2014-11-11\\Lernmenge'
def destDir='C:\\mediatrix_access_business\\Lernmengen\\2014-11-11\\LernmengeCleaned'
def start = new Date()
//Pattern for eml
def p = ~/(?!Thumbs.db)(.*\..*)/
def spliterR = /(?i)((mfg)|((mit )?((sch[�oe]{1,2}ne)|(freundll?iche)|(viele)|(liebe)|(beste))[nm]? gr[ue�]{1,2}[s�]{1,2}e?n?)|(Gr[�ue]{1,2}[�s]{1,2}e aus)|(Vielen Dank i[mn] Voraus)|(mit freundlichen)|(bitte um( schriftliche)? Best�tigung.)|(Danke und Gr��e)|(Hochachtungsvoll)|(---)|(Original Nachricht)|(Am [\d\.]{10} um [\d:]{5,7} schrieb))/

new File(srcDir).eachFile() { directory ->
   println "###################################################################################################"
   println   "Dir: ${srcDir}/${directory.name}"
   new File(destDir).mkdir()
   def logFile= new File(destDir+'\\..\\'+ 'Cleaning.log')
   if (directory.isDirectory()){
      new File(destDir+'\\'+directory.name).mkdir()
      logFile << "${destDir}\\${directory}\r\n"
      directory.eachFileMatch(p) { file ->
          println " File: ${directory.name}/${file.name}"
          logFile << "\t\t\t${destDir}\\${file.name}\r\n"
          def myFile= new File(destDir+'\\'+directory.name+ '\\'+file.name+'.txt')
          
          if (myFile.exists()){
              myFile.delete();
          }
          if (file.name.contains('.txt')){
              //myFile << file.text
              myFile.write(file.text, "UTF-8")
              println "   TEXTFILE: ${file.name}"
          } else if (file.name.contains('.msg') || file.name.contains('.eml')){
              Properties props = System.getProperties();
              Session mailSession = Session.getDefaultInstance(props, null);
              InputStream source = new FileInputStream(file);
              MimeMessage message = new MimeMessage(mailSession, source);
              def content = message.getContent();
              boolean foundBody=false
              if (content instanceof javax.mail.internet.MimeMultipart){
                    def htmlcontent=null
                    def txtcontent=null
                    //myFile << message.getSubject()
                    myFile.write( message.getSubject(), "UTF-8")
                    println "   Content: message.getContentType(): ${message.getContentType()} Parts: ${content.getCount()}:  ${message.getSubject()}"
                     for(int i = 0; i < content.getCount(); i++) {
                        println "    BodyPart {$i}:  ${content.getBodyPart(i).getContentType()} ${content.getBodyPart(i).getContent().getClass()}  ${content.getBodyPart(i)}";
                        if ((content.getBodyPart(i).getContentType().contains("multipart/alternative") || content.getBodyPart(i).getContentType().contains("multipart/mixed")) && !content.getBodyPart(i).getContent().getClass().equals("java.lang.String") ){
                            def part=content.getBodyPart(i).getContent()
                            for(int j = 0; j < part.getCount(); j++) {
                                println "    BodyPartPart {$i} {$j}: ${part.getBodyPart(j).getContentType()} ${part.getBodyPart(j).getContent().getClass()} ${part.getBodyPart(j)}";
                                if (part.getBodyPart(j).getContentType().contains("text/plain")){
                                        txtcontent=part.getBodyPart(j).getContent()
                                         foundBody=true
                                }else if (part.getBodyPart(j).getContentType().contains("text/html")){
                                       htmlcontent=part.getBodyPart(j).getContent()                                
                                        foundBody=true
                                }
                           } //for
                        }else if (content.getBodyPart(i).getContentType().contains("text/")) {
                                if (content.getBodyPart(i).getContentType().contains("text/plain")){
                                        txtcontent=content.getBodyPart(i).getContent()
                                         foundBody=true
                                }else if (content.getBodyPart(i).getContentType().contains("text/html")){
                                        htmlcontent=content.getBodyPart(i).getContent()    
                                         foundBody=true                            
                                }
                        }                        
                    }  //for
                    if (foundBody){
                        if (txtcontent) {
                             // println ("BODY TXT: " + txtcontent.substring(0,txtcontent.length()>100?100:txtcontent.length()-1))
                             // myFile << txtcontent
                              myFile.write(txtcontent, "UTF-8")
                        } else {
                              //println ("BODY HTML: " + htmlcontent.substring(0,htmlcontent.length()>100?100:htmlcontent.length()-1))
                               myFile.write(htmlcontent, "UTF-8")
                               //myFile << htmlcontent
                        }            
                    }else {
                        throw new IllegalArgumentException("ERROR: body not found")  
                    }                                       
                
                } else {
                    println "   TEXT PLAIN/NoMultipart"
                    //myFile << content
                    myFile.write(content, "UTF-8")
                }
            }else {//end Mail-Processing
                throw new IllegalArgumentException("ERROR: unknown filetype:${file.name}")  
            }
               
           def cleanText=myFile.text


       //Cut all after "mit freundlichen gr��en"
           if(cleanText =~ spliterR){              
              cleanText=cleanText.split(spliterR)[0]
           }

       //Clippings
       cleanText= (cleanText =~ /(?i)((Guten ((Tag)|(Abend)|(Morgen)))|(Sehr geehrte[rnms]?)|(Hallo)|(Liebes))\s*((Frau)|(Herr)|(Damen und Herre?n)|(Sky))?[ \-]?((Customer)|(Kunden))?[\s\-]*((Team)|(Support)|(Service)|(dienst)|(betreeung))?[,.!]?/).replaceAll("")
       cleanText= (cleanText =~ /(?i)((Vielen)|(herzlichen)|(besten)|(lieben)) Dank( im voraus)?/).replaceAll("")
       cleanText= (cleanText =~ /(?i)Danke( sch�n)?( im voraus)?/).replaceAll("")
       cleanText= (cleanText =~ /(?i)Hallo/).replaceAll("")
       cleanText= (cleanText =~ /(?i)Von ?: *([\w_\.-]{1,50}@[\w_\.-]{1,50})?/).replaceAll("")
       cleanText= (cleanText =~ /(?i)Kundennummer ?(: *[\d\.\-]*)?/).replaceAll("")
       cleanText= (cleanText =~ /(?i)Vertrags?nummer ?(: *[\d\.\-]*)?/).replaceAll("")
       cleanText= (cleanText =~ /(?i)Text ?: */).replaceAll("")
       
       cleanText= (cleanText =~ /(?i)((Subject)|(Betreff)) ?:? * /).replaceAll("")
       cleanText= (cleanText =~ /(?i)An ?:? * /).replaceAll("")
       cleanText= (cleanText =~ /(?i)Datum ?:? * /).replaceAll("")
       cleanText= (cleanText =~ /(?i)[\w_\.-]{1,50}@[\w_\.-]{1,50}/).replaceAll("")
       
       cleanText= (cleanText =~ /(?i)\b((AW:)|(FWD:)|(RE:))\b/).replaceAll("")
       cleanText= (cleanText =~ /(?i)Sky Deutschland( (Fernsehen )?GmbH[ &Co\.KG]*)?/).replaceAll("")
       cleanText= (cleanText =~ /(?i)Medienallee 26/).replaceAll("")
       cleanText= (cleanText =~ /(?i)85774 Unterf�hring/).replaceAll("")
       cleanText= (cleanText =~ /(?i)22033 Hamburg/).replaceAll("")
       
       cleanText= (cleanText =~ /SMS schreiben mit WEB.DE FreeMail - einfach, schnell und/).replaceAll("")
       cleanText= (cleanText =~ /kostenguenstig. Jetzt gleich testen! http:\/\/f.web.de\/\?mc=021192/).replaceAll("")
       
      cleanText= (cleanText =~ /WEB.DE DSL Doppel-Flat ab 19,99 �\/mtl\.! Jetzt mit/).replaceAll("")
       cleanText= (cleanText =~ /gratis Handy-Flat! http:\/\/produkte.web.de\/go\/DSL_Doppel_Flatrate/).replaceAll("")
       cleanText= (cleanText =~ /NEU: FreePhone - 0ct\/min Handyspartarif mit Geld-zur�ck-Garantie!/).replaceAll("")
       cleanText= (cleanText =~ /Jetzt informieren: http:\/\/www.gmx.net\/de\/go\/freephone/).replaceAll("")
       cleanText= (cleanText =~ /GMX DSL: Internet-, Telefon- und Handy-Flat ab 19,99 EUR\/mtl./).replaceAll("")
       cleanText= (cleanText =~ /Bis zu 150 EUR Startguthaben inklusive! http:\/\/portal.gmx.net\/de\/go\/dsl/).replaceAll("")
       cleanText= (cleanText =~ /Schon geh�rt? WEB.DE hat einen genialen Phishing-Filter in die/).replaceAll("")
       cleanText= (cleanText =~ /Toolbar eingebaut! http:\/\/produkte.web.de\/go\/toolbar\//).replaceAll("")
       cleanText= (cleanText =~ /GMX DSL Doppel-Flat ab 19,99 Euro\/mtl.! Jetzt mit/).replaceAll("")
       cleanText= (cleanText =~ /gratis Handy-Flat! http:\/\/portal.gmx.net\/de\/go\/dsl/).replaceAll("")
     


       // Cleanup if white spaces only?
       //cleanText= (cleanText =~ /(?i)\s{2,}/).replaceAll(" ")
       
       
       def superCleanText= (cleanText =~ /(?i)\s{2,}/).replaceAll(" ")

       if (myFile.exists()){
           myFile.delete();
       }
       if (!superCleanText || superCleanText.length()<40 ){
           println "*********Ommiting VERY SMAL FILE**************** ${myFile.name} ${myFile.length()}"
       }else{
           myFile.write(cleanText, "UTF-8")       
       }
    
               
          } // end file-iteration         
      } else {
        throw new IllegalArgumentException("FILE ${directory.name} ist kein Verzeihniss")
      }
   } // end dir-iteration
   def end = new Date()
   println "###################################################################################################"
   println "START: ${start}"
   println "END: ${end}"
   println "###################################################################################################"


   
   