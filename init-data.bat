@echo off
setlocal
echo ===========================================
echo   INITIALISATION BASE DE DONNEES (Diag)
echo             ISGA MANAGEMENT
echo ===========================================
echo.

REM --- ETAPE 1 : TROUVER MYSQL ---
set MYSQL_CMD=

REM Test 1 : PATH systeme
where mysql >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [INFO] MySQL trouve dans le PATH systeme.
    set MYSQL_CMD=mysql
    goto FOUND
)

REM Test 2 : C:\xampp
if exist "C:\xampp\mysql\bin\mysql.exe" (
    echo [INFO] MySQL trouve dans C:\xampp
    set MYSQL_CMD="C:\xampp\mysql\bin\mysql.exe"
    goto FOUND
)

REM Test 3 : D:\xampp (au cas ou)
if exist "D:\xampp\mysql\bin\mysql.exe" (
    echo [INFO] MySQL trouve dans D:\xampp
    set MYSQL_CMD="D:\xampp\mysql\bin\mysql.exe"
    goto FOUND
)

REM Test 4 : WAMP
if exist "C:\wamp64\bin\mysql\mysql8.0.33\bin\mysql.exe" (
    echo [INFO] MySQL trouve dans WAMP
    set MYSQL_CMD="C:\wamp64\bin\mysql\mysql8.0.33\bin\mysql.exe"
    goto FOUND
)

:NOT_FOUND
echo [ERREUR] Impossible de trouver mysql.exe !
echo.
echo Veuillez entrer le chemin complet vers le dossier bin de mysql
echo Exemple: C:\Program Files\MySQL\MySQL Server 8.0\bin
set /p USER_PATH="Chemin : "
if exist "%USER_PATH%\mysql.exe" (
    set MYSQL_CMD="%USER_PATH%\mysql.exe"
    goto FOUND
) else (
    echo Chemin invalide. Abandon.
    pause
    exit /b
)

:FOUND
echo [OK] Utilisation de : %MYSQL_CMD%
echo.

REM --- ETAPE 2 : TEST DE CONNEXION ---
echo [TEST] Tentative de connexion a MySQL...
%MYSQL_CMD% -u root -e "SELECT @@version;" 
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERREUR] Echec de connexion a MySQL.
    echo Cause possible : XAMPP n'est pas lance ou mot de passe root non vide.
    echo.
    pause
    exit /b
)
echo [OK] Connexion reussie.
echo.

REM --- ETAPE 3 : EXECUTION ---
echo [ACTION] Importation de init-data.sql...
%MYSQL_CMD% -u root < init-data.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===========================================
    echo [SUCCES] Base de donnees initialisee !
    echo ===========================================
) else (
    echo.
    echo [ERREUR] Une erreur est survenue lors de l'import.
    echo.
    echo *** ACTION REQUISE ***
    echo Si l'erreur est "Table doesnt exist":
    echo 1. Lancez d'abord start.bat UNE FOIS (pour creer les tables).
    echo 2. Attendez que l'application demarre.
    echo 3. Revenez ici et relancez ce script.
)

pause
