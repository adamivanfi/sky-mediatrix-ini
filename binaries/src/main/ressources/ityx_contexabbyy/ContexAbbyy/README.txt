abbyy.properties: 	Generelle Einstellungen bzw. Properties f�r die AbbyyIntegration.
					Diese Datei ist zwingend notwendig da hier unter Anderem die Seriennummer der AbbyyEngine konfiguriert wird.
					Aktuell kann hier noch der PDF-Export (de)aktiviert werden (pdfexport=false|true).

ABBYY_HOME:			Zum Ausf�hren muss die Environment-Variable ABBYY_HOME auf das Verzeichnis mit der Applikation (AbbyyOCR.exe) gesetzt werden.

profiles/:			Ordner mit (User) Profilen. Dieser Ordner muss sich unterhalb des in ABBYY_HOME definerten Verzeichnisses befinden.

default.profile:	Defaultkonfiguration der AbbyEngine. Hier�ber wird das Standardverhalten von Abbyy gesteuert. 
					Darunter f�llt beispielsweise das PDF bzw. XML-Exportformat, die Erkennungsgeschwindigkeit und Genauigkeit etc.
					Siehe Abbyy-Help f�r Details.
					
Errorcodes:			666 	- ABBYY_HOME nicht gesetzt
					0x1234	- Meta-Info konnte nicht exportiert werden
					0x0815	- Fehler beim XML-Parsen
					6666	- FREngine.dll nicht gefunden
					1337	- Keine DevSN gefunden




			