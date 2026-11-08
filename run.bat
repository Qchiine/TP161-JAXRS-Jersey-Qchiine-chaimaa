@echo off
title TP-161-JAXRS-Jersey
chcp 65001 >nul
mode con: cols=120 lines=55
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "MAVEN_OPTS=-Dfile.encoding=UTF-8"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d "%~dp0"

:menu
cls
echo ==============================================================================
echo   TP 7 - JAX-RS / Jersey : microservice bancaire des comptes
echo   Spring Boot 3.3.1  /  Jersey 3.1  /  H2 memoire  /  JSON + XML  /  JDK 17
echo ==============================================================================
echo.
echo   [1] Lancer l'application    (http://localhost:8082)
echo   [2] Suite de tests d'integration (mvn test)
echo   [3] Recompiler proprement le projet
echo   [4] Afficher les URLs et les commandes curl
echo   [0] Quitter
echo.
set "choix="
set /p "choix=Votre choix : "
if "%choix%"=="1" goto :lancer
if "%choix%"=="2" goto :tests
if "%choix%"=="3" goto :compile
if "%choix%"=="4" goto :urls
if "%choix%"=="0" goto :fin
goto :menu

:lancer
cls
echo ==============================================================================
echo   L'application demarre sur le port 8082.
echo   Appuyez sur Ctrl+C dans cette fenetre pour l'arreter.
echo ==============================================================================
echo.
echo   Comptes JSON : http://localhost:8082/banque/comptes
echo   Comptes XML  : http://localhost:8082/banque/comptes  (Accept: application/xml)
echo   Statistiques : http://localhost:8082/banque/statistiques
echo   Console H2   : http://localhost:8082/h2-console
echo.
call mvn -B -q spring-boot:run
goto :suspension

:tests
cls
echo Execution de la suite de tests d'integration (JSON, XML, CRUD, filtres)...
echo.
call mvn -B test
if errorlevel 1 goto :echec
goto :suspension

:compile
cls
call mvn -B -q clean compile
if errorlevel 1 goto :echec
echo.
echo Compilation terminee sans erreur.
goto :suspension

:urls
cls
echo ==============================================================================
echo   URLS DE L'API  (application lancee, choix 1)
echo ==============================================================================
echo.
echo   Lecture
echo     GET     http://localhost:8082/banque/comptes
echo     GET     http://localhost:8082/banque/comptes/1
echo     GET     http://localhost:8082/banque/comptes/type/EPARGNE
echo     GET     http://localhost:8082/banque/comptes/riches?seuil=500
echo     GET     http://localhost:8082/banque/statistiques
echo.
echo   Ecriture
echo     POST    http://localhost:8082/banque/comptes
echo     PUT     http://localhost:8082/banque/comptes/1
echo     DELETE  http://localhost:8082/banque/comptes/1
echo.
echo ------------------------------------------------------------------------------
echo   Exemples curl (Git Bash ou PowerShell)
echo ------------------------------------------------------------------------------
echo.
echo   Liste JSON
echo     curl -i -H "Accept: application/json" http://localhost:8082/banque/comptes
echo.
echo   Liste XML
echo     curl -i -H "Accept: application/xml" http://localhost:8082/banque/comptes
echo.
echo   Creation JSON
echo     curl -i -X POST http://localhost:8082/banque/comptes ^
echo       -H "Content-Type: application/json" ^
echo       -d "{\"solde\":1200.5,\"dateCreation\":\"2024-05-17\",\"type\":\"COURANT\"}"
echo.
echo   Statistiques XML
echo     curl -i -H "Accept: application/xml" http://localhost:8082/banque/statistiques
echo.
goto :suspension

:echec
echo.
echo *** Echec de la compilation ou des tests. ***
:suspension
echo.
echo ==============================================================================
echo   Fin de l'operation. Appuyez sur une touche pour revenir au menu.
echo ==============================================================================
pause >nul
goto :menu

:fin
exit /b 0
