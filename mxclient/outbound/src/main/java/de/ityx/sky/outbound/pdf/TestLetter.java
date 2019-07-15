package de.ityx.sky.outbound.pdf;

import com.nttdata.de.sky.archive.CustomLine;
import com.nttdata.de.sky.archive.CustomLine.LANGUAGE;
import com.nttdata.de.sky.pdf.PdfTemplate;
import de.ityx.mediatrix.data.Customer;
import de.ityx.mediatrix.modules.tools.logger.Log;
import de.ityx.sky.outbound.extensions.template.ServerTemplateExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Properties;

public class TestLetter {
    public static void main(String[] args) {
        try {
            // #1#
            // Im Global ist der path zu den templates definiert: sky.template.path=/sdf/sdf/sdf/sdf/ssf/sdf
            // Wichtig zum Laden der Templates
            // Global.changePathToConfig("path to mediatrix.properties");
            ServerTemplateExtension extension = ServerTemplateExtension.getInstance();
            PdfTemplate template = extension.loadTemplate(56480, 0, null);
            String body = getBody();

            template.getProperties().put("footer.oneverypage", "false");
            template.getProperties().put("footer.first", "false");
            template.getProperties().put("footer.oneverypage", "false");
            template.getProperties().put("templatepdf.print", "false");

            Properties variables = new Properties();
            variables.put("[Betreff]", "Betreff");
            variables.put("subject", "Subject test");

            Customer customer = new Customer();
            customer.setId(1234567);
            customer.setExternalId("5.345.4556");
            customer.setMsisdn("LIEB25");
            customer.setSalutation("Herr");
            customer.setFirstname("Firstname");
            customer.setName("Name");
            customer.setStreet("Strasse 23");
            customer.setPostcode("12345");
            customer.setCity("City");
            customer.setCountry("Austria");
            customer.setPublicPhone("01 49 166");
            variables.put("customer", customer);

            CustomLine cl = new CustomLine();
            cl.setCustomerId(customer.getId() + "");
            cl.setCountryCode(LANGUAGE.AT);
            cl.setGenJob("13123123");

            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put(CustomLine.class.getName(), cl);

            // #2#
            //  Test mit der Unterschrift
            //  Textbaustein mit der Unterschrift anlegen, in die Antwort einfügen, die Antwort speichern, die ID hie verwenden.
            //            Connection con = new DBConnection().getConnection();
            //            Answer answer = API.getServerAPI().getAnswerAPI().load(con, 6260, true);
            //            ClientUtils.addEmbeddedAttachments(answer, true, parameter);
            //            byte[] pdf = new LetterMaker().generateLetter(null, template, variables, answer.getBody(), parameter);

            byte[] pdf = new LetterMaker().generateLetter(null, template, variables, body, parameter);
            File file = File.createTempFile("Mediatrix", ".pdf");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(pdf);
            fos.close();

            String externViewer = System.getProperty("de.ityx.extern.pdfviewer", "evince");

            try {
                if (externViewer.trim().length() > 0) {
                    Log.message("PDF-Aufruf>" + externViewer + ' ' + file + "<");
                    Runtime.getRuntime().exec(externViewer + ' ' + file);
                    Thread.sleep(2000);
                }
            }
            finally {
                file.deleteOnExit();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static String getBody() {
        String body =
            " Ursprünglich, im geozentrischen Weltbild, wurden alle mit bloßem Auge regelmäßig sichtbaren Himmelserscheinungen, die sich vor dem Hintergrund des Fixsternhimmels bewegen, als Planeten bezeichnet und jeder einem Wochentag zugeordnet: Sonne, Mond, Mars, Merkur, Jupiter, Venus, Saturn. \nMit Einführung des heliozentrischen Weltbildes ging die Bezeichnung Planet auf diejenigen über, die um die Sonne kreisen. Sonne und Mond fielen also heraus und die Erde kam dafür hinzu."

                    + "\r\n\r\n Nach der Erfindung des Fernrohrs im Jahre 1608 von Hans Lippershey entdeckte William Herschel am 13. März 1781 den siebten Planeten des Sonnensystems: Uranus, der außerhalb der Saturnbahn die Sonne umkreist."

                    + "\r\n\r\n Am 1. Januar 1801 entdeckte Giuseppe Piazzi den Zwergplaneten Ceres, der zwischen Mars und Jupiter die Sonne umrundet. Ceres wurde damals jedoch als achter Planet des Sonnensystems betrachtet. Am 28. März 1802 entdeckte Heinrich Wilhelm Olbers mit Pallas ein weiteres \nObjekt, das die Sonne zwischen Mars und Jupiter umkreist. Es folgten die Entdeckungen von weiteren Objekten, die zwischen Mars und Jupiter die Sonne umrunden: Juno (1804), Vesta (1807) und Astraea (1845). Damit war die Zahl der Planeten auf zwölf angestiegen."

                    + "\r\n\r\n Im Jahre 1846 entdeckte Johann Gottfried Galle am 23. September einen 13. Planeten, der schließlich Neptun genannt wurde und die Sonne außerhalb der Bahn von Uranus umkreist."

                    + "\r\n\r\n Weil sich die Neuentdeckungen von Objekten zwischen Mars- und Jupiterbahn ab dem Jahre 1847 zu sehr häuften und alle diese Objekte um Größenordnungen kleiner waren als alle klassischen Planeten, wurde diesen der Planetenstatus wieder aberkannt. Nur noch die Planeten,\n die seit der Antike bekannt waren, plus Uranus und Neptun galten weiterhin als Planeten. Damit sank die Zahl der vollwertigen Planeten auf acht. Für die zahlreichen Objekte zwischen Mars- und Jupiterbahn wurde die Klasse der Asteroiden (Planetoiden) eingeführt."

                    + "\r\n\r\n Außer Merkur und Venus werden alle Planeten des Sonnensystems ihrerseits von natürlichen Satelliten umkreist, die nach dem Erdbegleiter auch „Monde“ genannt werden."

                    + "\r\n\r\n Am 13. März 1930 entdeckte Clyde W. Tombaugh Pluto, dessen Umlaufbahn zum größten Teil außerhalb der des Neptuns liegt. Die Größe Plutos wurde anfangs weit überschätzt, und er wurde bis ins Jahr 2006 als neunter Planet geführt. Sein Planetenstatus war aufgrund seiner\n geringen Größe und seiner sehr elliptischen sowie stark gegen die Ekliptik geneigten Bahn umstritten. Viele Astronomen rechneten ihn schon früh dem Kuipergürtel zu, einem Reservoir von Kometen und Asteroiden, das im Inneren bis an die Neptunbahn heranreicht. Weil im Bereich von Plutos Umlaufbahn im Laufe der Zeit immer mehr vergleichbare Objekte entdeckt wurden, mit Eris sogar eines, das größer ist als Pluto, wurde eine klare Definition für Planeten erforderlich. Mit der Festlegung der Internationalen Astronomischen Union (IAU) vom August 2006, dass Planeten den Bereich ihrer Umlaufbahn dominieren müssen, verlor Pluto den Planetenstatus. Er wurde in die gleichzeitig geschaffene Klasse der Zwergplaneten eingestuft, deren Form durch ihre Gravitation und die durch ihre Rotation verursachten Zentrifugalkräfte bestimmt ist, ohne dass sie Planeten sind. Alle weiteren kleineren Körper wurden zu den so genannten Kleinkörpern zusammengefasst."

                    + "\r\n\r\n Abstimmung über die Planetendefinition am 23. August 2006"

                    + "\r\n\r\n Bis dahin hatte es kein klar definiertes Unterscheidungsmerkmal zwischen Planeten und Asteroiden gegeben. Im Jahr 2004 wurde von der IAU ein Komitee eingesetzt, um verbindliche Kriterien für die Definition eines Planeten zu erarbeiten. Auf der 26. Generalversammlung der\n IAU in Prag wurden am 24. August 2006 offizielle Definitionen für verschiedene Klassen der die Sonne umlaufenden Himmelskörper verabschiedet – und damit hauptsächlich die erste wissenschaftliche Definition eines Planeten:"
                    //                        + "\r\n\r\n#WHITELINE"
                    + "\r\n\r\n HALLO WHITE LINE"
                    //-------
                    + "\r\n\r\n Nach der Erfindung des Fernrohrs im Jahre 1608 von Hans Lippershey entdeckte William Herschel am 13. März 1781 den siebten Planeten des Sonnensystems: Uranus, der außerhalb der Saturnbahn die Sonne umkreist."

                    + "\r\n\r\n Am 1. Januar 1801 entdeckte Giuseppe Piazzi den Zwergplaneten Ceres, der zwischen Mars und Jupiter die Sonne umrundet. Ceres wurde damals jedoch als achter Planet des Sonnensystems betrachtet. Am 28. März 1802 entdeckte Heinrich Wilhelm Olbers mit Pallas ein weiteres \nObjekt, das die Sonne zwischen Mars und Jupiter umkreist. Es folgten die Entdeckungen von weiteren Objekten, die zwischen Mars und Jupiter die Sonne umrunden: Juno (1804), Vesta (1807) und Astraea (1845). Damit war die Zahl der Planeten auf zwölf angestiegen."

                    + "\r\n\r\n Im Jahre 1846 entdeckte Johann Gottfried Galle am 23. September einen 13. Planeten, der schließlich Neptun genannt wurde und die Sonne außerhalb der Bahn von Uranus umkreist."

                    + "\r\n\r\n Weil sich die Neuentdeckungen von Objekten zwischen Mars- und Jupiterbahn ab dem Jahre 1847 zu sehr häuften und alle diese Objekte um Größenordnungen kleiner waren als alle klassischen Planeten, wurde diesen der Planetenstatus wieder aberkannt. Nur noch die Planeten,\n die seit der Antike bekannt waren, plus Uranus und Neptun galten weiterhin als Planeten. Damit sank die Zahl der vollwertigen Planeten auf acht. Für die zahlreichen Objekte zwischen Mars- und Jupiterbahn wurde die Klasse der Asteroiden (Planetoiden) eingeführt."

                    + "\r\n\r\n Außer Merkur und Venus werden alle Planeten des Sonnensystems ihrerseits von natürlichen Satelliten umkreist, die nach dem Erdbegleiter auch „Monde“ genannt werden."

                    + "\r\n\r\n Am 13. März 1930 entdeckte Clyde W. Tombaugh Pluto, dessen Umlaufbahn zum größten Teil außerhalb der des Neptuns liegt. Die Größe Plutos wurde anfangs weit überschätzt, und er wurde bis ins Jahr 2006 als neunter Planet geführt. Sein Planetenstatus war aufgrund seiner\n geringen Größe und seiner sehr elliptischen sowie stark gegen die Ekliptik geneigten Bahn umstritten. Viele Astronomen rechneten ihn schon früh dem Kuipergürtel zu, einem Reservoir von Kometen und Asteroiden, das im Inneren bis an die Neptunbahn heranreicht. Weil im Bereich von Plutos Umlaufbahn im Laufe der Zeit immer mehr vergleichbare Objekte entdeckt wurden, mit Eris sogar eines, das größer ist als Pluto, wurde eine klare Definition für Planeten erforderlich. Mit der Festlegung der Internationalen Astronomischen Union (IAU) vom August 2006, dass Planeten den Bereich ihrer Umlaufbahn dominieren müssen, verlor Pluto den Planetenstatus. Er wurde in die gleichzeitig geschaffene Klasse der Zwergplaneten eingestuft, deren Form durch ihre Gravitation und die durch ihre Rotation verursachten Zentrifugalkräfte bestimmt ist, ohne dass sie Planeten sind. Alle weiteren kleineren Körper wurden zu den so genannten Kleinkörpern zusammengefasst."

                    + "\r\n\r\n Abstimmung über die Planetendefinition am 23. August 2006"

                    + "\r\n\r\n Bis dahin hatte es kein klar definiertes Unterscheidungsmerkmal zwischen Planeten und Asteroiden gegeben. Im Jahr 2004 wurde von der IAU ein Komitee eingesetzt, um verbindliche Kriterien für die Definition eines Planeten zu erarbeiten. Auf der 26. Generalversammlung der\n IAU in Prag wurden am 24. August 2006 offizielle Definitionen für verschiedene Klassen der die Sonne umlaufenden Himmelskörper verabschiedet – und damit hauptsächlich die erste wissenschaftliche Definition eines Planeten:"
                    + "\r\n\r\n#WHITELINE"
                    + "\r\n\r\n HALLO WHITE LINE"
                    + "\r\n\r\n Nach der Erfindung des Fernrohrs im Jahre 1608 von Hans Lippershey entdeckte William Herschel am 13. März 1781 den siebten Planeten des Sonnensystems: Uranus, der außerhalb der Saturnbahn die Sonne umkreist."

                    + "\r\n\r\n Am 1. Januar 1801 entdeckte Giuseppe Piazzi den Zwergplaneten Ceres, der zwischen Mars und Jupiter die Sonne umrundet. Ceres wurde damals jedoch als achter Planet des Sonnensystems betrachtet. Am 28. März 1802 entdeckte Heinrich Wilhelm Olbers mit Pallas ein weiteres \nObjekt, das die Sonne zwischen Mars und Jupiter umkreist. Es folgten die Entdeckungen von weiteren Objekten, die zwischen Mars und Jupiter die Sonne umrunden: Juno (1804), Vesta (1807) und Astraea (1845). Damit war die Zahl der Planeten auf zwölf angestiegen."

                    + "\r\n\r\n Im Jahre 1846 entdeckte Johann Gottfried Galle am 23. September einen 13. Planeten, der schließlich Neptun genannt wurde und die Sonne außerhalb der Bahn von Uranus umkreist."

                    + "\r\n\r\n Weil sich die Neuentdeckungen von Objekten zwischen Mars- und Jupiterbahn ab dem Jahre 1847 zu sehr häuften und alle diese Objekte um Größenordnungen kleiner waren als alle klassischen Planeten, wurde diesen der Planetenstatus wieder aberkannt. Nur noch die Planeten,\n die seit der Antike bekannt waren, plus Uranus und Neptun galten weiterhin als Planeten. Damit sank die Zahl der vollwertigen Planeten auf acht. Für die zahlreichen Objekte zwischen Mars- und Jupiterbahn wurde die Klasse der Asteroiden (Planetoiden) eingeführt."

                    + "\r\n\r\n Außer Merkur und Venus werden alle Planeten des Sonnensystems ihrerseits von natürlichen Satelliten umkreist, die nach dem Erdbegleiter auch „Monde“ genannt werden."

                    + "\r\n\r\n Am 13. März 1930 entdeckte Clyde W. Tombaugh Pluto, dessen Umlaufbahn zum größten Teil außerhalb der des Neptuns liegt. Die Größe Plutos wurde anfangs weit überschätzt, und er wurde bis ins Jahr 2006 als neunter Planet geführt. Sein Planetenstatus war aufgrund seiner\n geringen Größe und seiner sehr elliptischen sowie stark gegen die Ekliptik geneigten Bahn umstritten. Viele Astronomen rechneten ihn schon früh dem Kuipergürtel zu, einem Reservoir von Kometen und Asteroiden, das im Inneren bis an die Neptunbahn heranreicht. Weil im Bereich von Plutos Umlaufbahn im Laufe der Zeit immer mehr vergleichbare Objekte entdeckt wurden, mit Eris sogar eines, das größer ist als Pluto, wurde eine klare Definition für Planeten erforderlich. Mit der Festlegung der Internationalen Astronomischen Union (IAU) vom August 2006, dass Planeten den Bereich ihrer Umlaufbahn dominieren müssen, verlor Pluto den Planetenstatus. Er wurde in die gleichzeitig geschaffene Klasse der Zwergplaneten eingestuft, deren Form durch ihre Gravitation und die durch ihre Rotation verursachten Zentrifugalkräfte bestimmt ist, ohne dass sie Planeten sind. Alle weiteren kleineren Körper wurden zu den so genannten Kleinkörpern zusammengefasst."

                    + "\r\n\r\n Abstimmung über die Planetendefinition am 23. August 2006"

                    + "\r\n\r\n Bis dahin hatte es kein klar definiertes Unterscheidungsmerkmal zwischen Planeten und Asteroiden gegeben. Im Jahr 2004 wurde von der IAU ein Komitee eingesetzt, um verbindliche Kriterien für die Definition eines Planeten zu erarbeiten. Auf der 26. Generalversammlung der\n IAU in Prag wurden am 24. August 2006 offizielle Definitionen für verschiedene Klassen der die Sonne umlaufenden Himmelskörper verabschiedet – und damit hauptsächlich die erste wissenschaftliche Definition eines Planeten:"
                    + "\r\n\r\n#WHITELINE"
                    + "\r\n\r\n HALLO WHITE LINE"
                    + "\r\n\r\n Nach der Erfindung des Fernrohrs im Jahre 1608 von Hans Lippershey entdeckte William Herschel am 13. März 1781 den siebten Planeten des Sonnensystems: Uranus, der außerhalb der Saturnbahn die Sonne umkreist."

                    + "\r\n\r\n Am 1. Januar 1801 entdeckte Giuseppe Piazzi den Zwergplaneten Ceres, der zwischen Mars und Jupiter die Sonne umrundet. Ceres wurde damals jedoch als achter Planet des Sonnensystems betrachtet. Am 28. März 1802 entdeckte Heinrich Wilhelm Olbers mit Pallas ein weiteres \nObjekt, das die Sonne zwischen Mars und Jupiter umkreist. Es folgten die Entdeckungen von weiteren Objekten, die zwischen Mars und Jupiter die Sonne umrunden: Juno (1804), Vesta (1807) und Astraea (1845). Damit war die Zahl der Planeten auf zwölf angestiegen."

                    + "\r\n\r\n Im Jahre 1846 entdeckte Johann Gottfried Galle am 23. September einen 13. Planeten, der schließlich Neptun genannt wurde und die Sonne außerhalb der Bahn von Uranus umkreist."

                    + "\r\n\r\n Weil sich die Neuentdeckungen von Objekten zwischen Mars- und Jupiterbahn ab dem Jahre 1847 zu sehr häuften und alle diese Objekte um Größenordnungen kleiner waren als alle klassischen Planeten, wurde diesen der Planetenstatus wieder aberkannt. Nur noch die Planeten,\n die seit der Antike bekannt waren, plus Uranus und Neptun galten weiterhin als Planeten. Damit sank die Zahl der vollwertigen Planeten auf acht. Für die zahlreichen Objekte zwischen Mars- und Jupiterbahn wurde die Klasse der Asteroiden (Planetoiden) eingeführt."

                    + "\r\n\r\n Außer Merkur und Venus werden alle Planeten des Sonnensystems ihrerseits von natürlichen Satelliten umkreist, die nach dem Erdbegleiter auch „Monde“ genannt werden."

                    + "\r\n\r\n Am 13. März 1930 entdeckte Clyde W. Tombaugh Pluto, dessen Umlaufbahn zum größten Teil außerhalb der des Neptuns liegt. Die Größe Plutos wurde anfangs weit überschätzt, und er wurde bis ins Jahr 2006 als neunter Planet geführt. Sein Planetenstatus war aufgrund seiner\n geringen Größe und seiner sehr elliptischen sowie stark gegen die Ekliptik geneigten Bahn umstritten. Viele Astronomen rechneten ihn schon früh dem Kuipergürtel zu, einem Reservoir von Kometen und Asteroiden, das im Inneren bis an die Neptunbahn heranreicht. Weil im Bereich von Plutos Umlaufbahn im Laufe der Zeit immer mehr vergleichbare Objekte entdeckt wurden, mit Eris sogar eines, das größer ist als Pluto, wurde eine klare Definition für Planeten erforderlich. Mit der Festlegung der Internationalen Astronomischen Union (IAU) vom August 2006, dass Planeten den Bereich ihrer Umlaufbahn dominieren müssen, verlor Pluto den Planetenstatus. Er wurde in die gleichzeitig geschaffene Klasse der Zwergplaneten eingestuft, deren Form durch ihre Gravitation und die durch ihre Rotation verursachten Zentrifugalkräfte bestimmt ist, ohne dass sie Planeten sind. Alle weiteren kleineren Körper wurden zu den so genannten Kleinkörpern zusammengefasst."

                    + "\r\n\r\n Abstimmung über die Planetendefinition am 23. August 2006"

                    + "\r\n\r\n Bis dahin hatte es kein klar definiertes Unterscheidungsmerkmal zwischen Planeten und Asteroiden gegeben. Im Jahr 2004 wurde von der IAU ein Komitee eingesetzt, um verbindliche Kriterien für die Definition eines Planeten zu erarbeiten. Auf der 26. Generalversammlung der\n IAU in Prag wurden am 24. August 2006 offizielle Definitionen für verschiedene Klassen der die Sonne umlaufenden Himmelskörper verabschiedet – und damit hauptsächlich die erste wissenschaftliche Definition eines Planeten:"
                    + "\r\n\r\n#WHITELINE"
                    + "\r\n\r\n HALLO WHITE LINE"
                    + "\r\n\r\n Nach der Erfindung des Fernrohrs im Jahre 1608 von Hans Lippershey entdeckte William Herschel am 13. März 1781 den siebten Planeten des Sonnensystems: Uranus, der außerhalb der Saturnbahn die Sonne umkreist."

                    + "\r\n\r\n Am 1. Januar 1801 entdeckte Giuseppe Piazzi den Zwergplaneten Ceres, der zwischen Mars und Jupiter die Sonne umrundet. Ceres wurde damals jedoch als achter Planet des Sonnensystems betrachtet. Am 28. März 1802 entdeckte Heinrich Wilhelm Olbers mit Pallas ein weiteres \nObjekt, das die Sonne zwischen Mars und Jupiter umkreist. Es folgten die Entdeckungen von weiteren Objekten, die zwischen Mars und Jupiter die Sonne umrunden: Juno (1804), Vesta (1807) und Astraea (1845). Damit war die Zahl der Planeten auf zwölf angestiegen."

                    + "\r\n\r\n Im Jahre 1846 entdeckte Johann Gottfried Galle am 23. September einen 13. Planeten, der schließlich Neptun genannt wurde und die Sonne außerhalb der Bahn von Uranus umkreist."

                    + "\r\n\r\n Weil sich die Neuentdeckungen von Objekten zwischen Mars- und Jupiterbahn ab dem Jahre 1847 zu sehr häuften und alle diese Objekte um Größenordnungen kleiner waren als alle klassischen Planeten, wurde diesen der Planetenstatus wieder aberkannt. Nur noch die Planeten,\n die seit der Antike bekannt waren, plus Uranus und Neptun galten weiterhin als Planeten. Damit sank die Zahl der vollwertigen Planeten auf acht. Für die zahlreichen Objekte zwischen Mars- und Jupiterbahn wurde die Klasse der Asteroiden (Planetoiden) eingeführt."

                    + "\r\n\r\n Außer Merkur und Venus werden alle Planeten des Sonnensystems ihrerseits von natürlichen Satelliten umkreist, die nach dem Erdbegleiter auch „Monde“ genannt werden."

                    + "\r\n\r\n Am 13. März 1930 entdeckte Clyde W. Tombaugh Pluto, dessen Umlaufbahn zum größten Teil außerhalb der des Neptuns liegt. Die Größe Plutos wurde anfangs weit überschätzt, und er wurde bis ins Jahr 2006 als neunter Planet geführt. Sein Planetenstatus war aufgrund seiner\n geringen Größe und seiner sehr elliptischen sowie stark gegen die Ekliptik geneigten Bahn umstritten. Viele Astronomen rechneten ihn schon früh dem Kuipergürtel zu, einem Reservoir von Kometen und Asteroiden, das im Inneren bis an die Neptunbahn heranreicht. Weil im Bereich von Plutos Umlaufbahn im Laufe der Zeit immer mehr vergleichbare Objekte entdeckt wurden, mit Eris sogar eines, das größer ist als Pluto, wurde eine klare Definition für Planeten erforderlich. Mit der Festlegung der Internationalen Astronomischen Union (IAU) vom August 2006, dass Planeten den Bereich ihrer Umlaufbahn dominieren müssen, verlor Pluto den Planetenstatus. Er wurde in die gleichzeitig geschaffene Klasse der Zwergplaneten eingestuft, deren Form durch ihre Gravitation und die durch ihre Rotation verursachten Zentrifugalkräfte bestimmt ist, ohne dass sie Planeten sind. Alle weiteren kleineren Körper wurden zu den so genannten Kleinkörpern zusammengefasst."

                    + "\r\n\r\n Abstimmung über die Planetendefinition am 23. August 2006"

                    + "\r\n\r\n Bis dahin hatte es kein klar definiertes Unterscheidungsmerkmal zwischen Planeten und Asteroiden gegeben. Im Jahr 2004 wurde von der IAU ein Komitee eingesetzt, um verbindliche Kriterien für die Definition eines Planeten zu erarbeiten. Auf der 26. Generalversammlung der\n IAU in Prag wurden am 24. August 2006 offizielle Definitionen für verschiedene Klassen der die Sonne umlaufenden Himmelskörper verabschiedet – und damit hauptsächlich die erste wissenschaftliche Definition eines Planeten:"
                    + "\r\n\r\n#WHITELINE"
                    + "\r\n\r\n HALLO WHITE LINE"

                    //----------
                    + "\r\n\r\n#ENDWHITELINE";

        body =
            "<html><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n\r"
                    +
                    "    <head>\n\r"
                    +
                    "        \n\r"
                    +
                    "        <style>\n\r"
                    +
                    "            p{margin-bottom:0px;margin-top:0px;}\n\r"
                    +
                    "            body{font-family:SansSerif;font-size:10pt;}\n\r"
                    +
                    "        </style>\n\r"
                    +
                    "    </head>\n\r"
                    +
                    "    <body style=\"font-family:Arial;font-size:10pt;\">\n\r"
                    +
                    "        <font face=\"Arial\">---------- Weitergeleitete Nachricht ----------<br>Von: Hubert Ulrich &lt;Ulrich.Hubert@nttdata.com&gt;<br>Datum: 26. April 2012 10:57<br>Betreff: WG: (An Teilnehmer weiterleiten) Bitte treten Sie jetzt bei, das<br>Meeting ist in Gang: DMS Workshop<br>An: &quot;heiko.groftschik@ityx.de&quot; &lt;heiko.groftschik@ityx.de&gt;<br><br><br>&nbsp;** **<br><br>** **<br><br>Yours sincerely<br><br><br>*Ulrich Hubert *| | NTT DATA Deutschland<br>Kronstadter Str. 1 - 81677 M&#252;nchen, Deutschland | Tel: +49 89 9936-1955 |<br>Fax: +49 89 9936-1750 | M: +49 163 4212273 | Ulrich.Hubert@nttdata.com |<br>Learn more at www.nttdata.com/de<br><br>NTT DATA Deutschland GmbH<br>Gesch&#228;ftsf&#252;hrer: Thomas Balgheim<br>Aufsichtsrat: Paul Shelton (Vorsitzender)<br>Sitz und Amtsgericht: M&#252;nchen HRB 51846<br>&nbsp;****<br><br>*Von:* Ulrich Hubert [<font color=\"blue\"><u><a href=\"mailto:messenger@webex.com]\">mailto:messenger@webex.com]</a></u></font><br>*Gesendet:* Donnerstag, 26. April 2012 10:56<br>*An:* Hubert Ulrich<br>*Betreff:* (An Teilnehmer weiterleiten) Bitte treten Sie jetzt bei, das<br>Meeting ist in Gang: DMS Workshop<br>*Wichtigkeit:* Hoch****<br><br>** **<br><br><br>**** Sie k&#246;nnen diese E-Mail-Einladung an die Teilnehmer weiterleiten ****<br><br>Sehr geehrte(r) ,<br><br>bitte treten Sie meinem gegenw&#228;rtig laufenden Meeting bei.<br><br>Thema: DMS Workshop<br>Datum: Donnerstag, 26. April 2012<br>Zeit: 10:56, Europ&#228;ische Sommerzeit (Berlin, GMT+02:00)<br>Meeting-Kennnummer: 705 564 373<br>Meeting-Passwort: Sky123<br><br><br>-------------------------------------------------------<br>So treten Sie dem Online-Meeting bei (Jetzt auch auf mobilen Ger&#228;ten!)<br>-------------------------------------------------------<br>1. Rufen Sie folgende Seite auf:<br><font color=\"blue\"><u><a href=\"https://cirquent.webex.com/cirquent/e.php?AT=MI&amp;EventID=209840887&amp;UID=0&amp;PW=NMGI5NjViNWU0&amp;RT=OCMyNQ%3D%3D\">https://cirquent.webex.com/cirquent/e.php?AT=MI&amp;EventID=209840887&amp;UID=0&amp;PW=NMGI5NjViNWU0&amp;RT=OCMyNQ%3D%3D</a></u></font><br>2. Falls erforderlich, geben Sie bitte Ihren Namen und Ihre E-Mail-Adresse<br>ein.<br>3. Falls ein Passwort erforderlich ist, geben Sie das Meeting-Passwort ein:<br>Sky123<br>4. Klicken Sie auf &quot;Teilnehmen&quot;.<br>5. Folgen Sie den Anweisungen auf dem Bildschirm.<br><br>Klicken Sie f&#252;r die Anzeige in anderen Zeitzonen oder Sprachen auf diesen<br>Link:<br><font color=\"blue\"><u><a href=\"https://cirquent.webex.com/cirquent/e.php?AT=MI&amp;EventID=209840887&amp;UID=0&amp;PW=NMGI5NjViNWU0&amp;ORT=OCMyNQ%3D%3D\">https://cirquent.webex.com/cirquent/e.php?AT=MI&amp;EventID=209840887&amp;UID=0&amp;PW=NMGI5NjViNWU0&amp;ORT=OCMyNQ%3D%3D</a></u></font><br><br>-------------------------------------------------------<br>Zur ausschlie&#223;lichen Teilnahme an der Telefonkonferenz:<br>-------------------------------------------------------<br>Call-in toll-free number: +49-69255114828&nbsp;&nbsp;(Deutschland)<br>Call-in number: +49-89204049604&nbsp;&nbsp;(Deutschland)<br>Participant Pin Code: 483018<br><br>-------------------------------------------------------<br>F&#252;r Unterst&#252;tzung<br>-------------------------------------------------------<br>1. Rufen Sie folgende Seite auf: \n\r"
                    +
                    "            <font color=\"blue\">\n\r"
                    +
                    "                <u>\n\r"
                    +
                    "                    <a href=\"https://cirquent.webex.com/cirquent/mc\">https://cirquent.webex.com/cirquent/mc</a></u></font><br>2. Klicken Sie in der linken Navigationsleiste auf &quot;Support&quot;.<br><br>Sie k&#246;nnen sich mit mir unter folgender Adresse in Verbindung setzen:<br>ulrich.hubert@nttdata.com<br><br><br><br><br><br><font color=\"blue\"><u><a href=\"http://www.webex.com\">http://www.webex.com</a></u></font><br><br>CCP:+4989204049604x483018#<br><br>WICHTIGER HINWEIS: Dieser WebEx-Service bietet eine Funktion, mit der<br>Audiodaten und w&#228;hrend der Sitzung ausgetauschte oder angezeigte Dokumente<br>bzw. sonstige Materialien aufgezeichnet werden k&#246;nnen. Wenn Sie dieser<br>Sitzung beitreten, erkl&#228;ren Sie sich automatisch mit einer derartigen<br>Aufzeichnung einverstanden. Wenn Sie nicht mit einer Aufzeichnung<br>einverstanden sind, &#228;u&#223;ern Sie Ihre Bedenken vor der Aufzeichnung gegen&#252;ber<br>dem Gastgeber des Meetings, oder nehmen Sie nicht an der Sitzung teil.<br>Beachten Sie, dass solche Aufzeichnungen im Falle eines Rechtsstreits<br>herangezogen werden k&#246;nnen. ****<br><br>______________________________________________________________________<br>Disclaimer:This email and any attachments are sent in strictest confidence<br>for the sole use of the addressee and may contain legally privileged,<br>confidential, and proprietary data. If you are not the intended recipient,<br>please advise the sender by replying promptly to this email and then delete<br>and destroy this email and any attachments without any further use, copying<br>or forwarding<br><br><br><br>--<br>Dipl. Inform. Heiko Groftschik<br>Entwicklungsleitung, Ausbildungsleitung<br><br><b><i><font color=\"#33CC00\">ITyX</font></i></b> GmbH (www.<b><i><font color=\"#33CC00\">ityx</font></i></b>.de)<br>Florinsstr. 18<br>56218 M&#252;hlheim-K&#228;rlich<br>Gesch&#228;ftsf&#252;hrer: \n\r"
                    +
                    "            <b>\n\r"
                    +
                    "                <i>\n\r"
                    +
                    "                    <font color=\"#33CC00\">S&#252;leyman</font></i></b> \n\r"
                    +
                    "            <b>\n\r"
                    +
                    "                <i>\n\r"
                    +
                    "                    <font color=\"#33CC00\">Arayan</font></i></b><br>Handelsregister Koblenz HRB 6563<br><br>Tel. +49 (0) 261-200 87 56<br>Fax. +49 (0) 261-133 82 48<br><br>Diese Email enth&#228;lt Informationen der \n\r"
                    +
                    "            <b>\n\r"
                    +
                    "                <i>\n\r"
                    +
                    "                    <font color=\"#33CC00\">ITyX</font></i></b> Ag, \n\r"
                    +
                    "            <b>\n\r"
                    +
                    "                <i>\n\r"
                    +
                    "                    <font color=\"#33CC00\">ITyX</font></i></b> GmbH oder \n\r"
                    +
                    "            <b>\n\r"
                    +
                    "                <i>\n\r"
                    +
                    "                    <font color=\"#33CC00\">ITyX</font></i></b> oHG.<br>Diese sind m&#246;glicherweise vertraulich und ausschliesslich f&#252;r den<br>Adressaten bestimmt. Jeglicher Zugriff auf diese Email durch andere<br>Personen als den Adressaten ist untersagt. Sollten Sie nicht der f&#252;r diese<br>Email bestimmte Adressat sein, ist Ihnen jede Ver&#246;ffentlichung,<br>Vervielf&#228;ltigung oder Weitergabe wie auch das Ergreifen oder Unterlassen<br>von Massnahmen im Vertrauen auf erlangte Information untersagt. Sollten Sie<br>diese elektronische Nachricht irrt&#252;mlicherweise erhalten haben, so<br>informieren Sie uns bitte unverz&#252;glich telefonisch oder per E-Mail (s.o.)<br></font></body>\n\r";
        return body;
    }
}
