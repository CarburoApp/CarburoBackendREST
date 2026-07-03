@echo off
echo ============================================================
echo           CARBURO TFG - INICIANDO PRUEBAS DE CARGA
echo ============================================================
echo Ejecutando escenarios secuenciales: Load, Stress y Spike Tests...

 "D:\Aplicaciones\k6.exe" run load_test_backend.js > resultado_pruebas_carga.txt 2>&1

echo.
echo Pruebas finalizadas. Informe generado en: "resultado_pruebas_carga.txt"
echo ============================================================
pause