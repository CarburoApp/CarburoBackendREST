@echo off

cd /d %~dp0

set K6_PATH=C:\Users\Manuel\Downloads\k6-v2.0.0-windows-amd64\k6.exe

echo ==============================
echo SELECCIONA SCRIPT K6
echo ==============================
echo 1 - catalogos-load.js
echo 2 - estaciones-cercanas-load.js
echo 3 - precios-historicos-load.js
echo 4 - mixed-public-load.js
echo 5 - postgis-stress.js
echo ==============================

set /p CHOICE=Elige opcion:

if "%CHOICE%"=="1" set SCRIPT=catalogos-load.js
if "%CHOICE%"=="2" set SCRIPT=estaciones-cercanas-load.js
if "%CHOICE%"=="3" set SCRIPT=precios-historicos-load.js
if "%CHOICE%"=="4" set SCRIPT=mixed-public-load.js
if "%CHOICE%"=="5" set SCRIPT=postgis-stress.js

echo ==============================
echo CONFIGURACION API KEY
echo ==============================
set /p API_KEY=Introduce tu API KEY:

echo ==============================
echo Ejecutando: %SCRIPT%
echo ==============================

"%K6_PATH%" run -e API_KEY=%API_KEY% %SCRIPT%

pause