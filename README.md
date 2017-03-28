# ASE_WS1617_Terminabstimmung-1

Anleitung zum Serverstart:

Vorraussetzung: python3.5 , virtualenv
Die Befehle müssen in der Konsole zur Verfügung stehen. 

Im Ordner Server das Skript setup.sh ausführen (Konsole: ./setup.sh ) um die virtuelle Umgebung zu erstellen und die benötigten Packages zu installieren.

Zum Starten des Servers im selben Ordner ./startServer ausführen.

Ein bestehender Account ist a@b.com, Passwort ist rootTermin

URLs:
	admin/ Adminoberfläche zur Manipulation der Datenbank
	api/ mit Browser durchsuchbare API
	login/ Ermöglicht Login, um auf die Termine etc. zugreifen zu können, leitet auf Kalender um
	/o/applications Ansehen der OAuth-Applikations für Keys
