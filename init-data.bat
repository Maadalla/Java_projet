@echo off
echo ==========================================
echo INITIALISATION DES DONNEES DE TEST
echo SQLI - E-Challenge Platform
echo ==========================================
echo.

echo Verification de MySQL...
mysql --version >nul 2>&1
if errorlevel 1 (
    echo ERREUR: MySQL n'est pas dans le PATH
    echo Verifiez que XAMPP est demarre et que MySQL est accessible
    pause
    exit /b 1
)

echo.
echo Importation des donnees de test...
echo.

mysql -u root -p gestion_tests < init-data.sql

if errorlevel 1 (
    echo.
    echo ERREUR lors de l'importation!
    echo Verifiez que:
    echo - XAMPP est demarre
    echo - MySQL fonctionne
    echo - La base 'gestion_tests' existe
    pause
    exit /b 1
) else (
    echo.
    echo ==========================================
    echo SUCCES! Donnees importees
    echo ==========================================
    echo.
    echo BASE DE DONNEES INITIALISEE:
    echo - Administrateur: admin / admin123
    echo - 4 Themes
    echo - 16 Questions avec reponses
    echo - 7 Creneaux
    echo - 1 Candidat de test
    echo.
    echo CODE DE TEST: TEST-2025
    echo.
    echo Vous pouvez maintenant:
    echo 1. Demarrer l'application: start.bat
    echo 2. Tester avec le code: TEST-2025
    echo 3. Admin: admin / admin123
    echo.
)

pause
