@echo off
echo ===========================================
echo       RESET DATABASE ISGA MANAGEMENT
echo ===========================================
echo.
echo [ATTENTION] Ce script va supprimer et recreer la base 'gestion_tests'.
echo Assurez-vous que MySQL est lance.
echo.

REM Tenter de trouver mysql dans les chemins standards (XAMPP, WAMP...)
set MYSQL_CMD=mysql
if exist "C:\xampp\mysql\bin\mysql.exe" set MYSQL_CMD="C:\xampp\mysql\bin\mysql.exe"
if exist "C:\wamp64\bin\mysql\mysql8.0.33\bin\mysql.exe" set MYSQL_CMD="C:\wamp64\bin\mysql\mysql8.0.33\bin\mysql.exe"

%MYSQL_CMD% -u root -e "DROP DATABASE IF EXISTS gestion_tests; CREATE DATABASE gestion_tests CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

if %ERRORLEVEL% NEQ 0 (
    echo [ERREUR] Impossible de se connecter a MySQL. Verifiez le chemin ou que le serveur tourne.
    pause
    exit /b
)

echo [OK] Base vide creee.
echo.
echo [INFO] Importation des donnees...
%MYSQL_CMD% -u root gestion_tests < init-data.sql

echo.
echo [SUCCES] Base de donnees reinitialisee avec succes !
pause
