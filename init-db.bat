@echo off
echo Initialisation de la base de donnees...
mvn compile exec:java "-Dexec.mainClass=com.sqli.gestiontests.DataInitializer" "-Dexec.cleanupDaemonThreads=false"
pause
