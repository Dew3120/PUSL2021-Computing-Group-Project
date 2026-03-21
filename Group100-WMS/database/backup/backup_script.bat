@echo off
REM ============================================================
REM  Group 100 — Centralized Apparel WMS
REM  Database Backup Script (Windows)
REM  Performs daily mysqldump of group100_wms database
REM ============================================================

REM — Configuration —
SET DB_HOST=127.0.0.1
SET DB_PORT=3306
SET DB_NAME=group100_wms
SET DB_USER=root
SET DB_PASS=root
SET MYSQL_BIN=C:\xampp\mysql\bin
SET BACKUP_DIR=%~dp0..\..\backups

REM — Create backup directory if it doesn't exist —
IF NOT EXIST "%BACKUP_DIR%" (
    mkdir "%BACKUP_DIR%"
    echo Created backup directory: %BACKUP_DIR%
)

REM — Generate timestamp for filename —
FOR /F "tokens=1-3 delims=/ " %%a IN ('date /t') DO SET DATESTAMP=%%c-%%a-%%b
FOR /F "tokens=1-2 delims=: " %%a IN ('time /t') DO SET TIMESTAMP=%%a%%b
SET FILENAME=%DB_NAME%_backup_%DATESTAMP%_%TIMESTAMP%.sql

REM — Run mysqldump —
echo.
echo ============================================================
echo  Group100 WMS — Database Backup
echo ============================================================
echo  Database:  %DB_NAME%
echo  Host:      %DB_HOST%:%DB_PORT%
echo  Output:    %BACKUP_DIR%\%FILENAME%
echo  Time:      %DATE% %TIME%
echo ============================================================
echo.

"%MYSQL_BIN%\mysqldump.exe" -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% --single-transaction --routines --triggers --add-drop-table %DB_NAME% > "%BACKUP_DIR%\%FILENAME%"

IF %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Backup completed: %FILENAME%
    echo.

    REM — Delete backups older than 30 days —
    echo Cleaning up backups older than 30 days...
    FORFILES /P "%BACKUP_DIR%" /M *.sql /D -30 /C "cmd /c del @path" 2>nul
    echo Cleanup complete.
) ELSE (
    echo [ERROR] Backup failed! Check MySQL connection and credentials.
    echo   - Is MySQL running?
    echo   - Is the path to mysqldump correct? Current: %MYSQL_BIN%
    echo   - Are credentials correct? User: %DB_USER%
)

echo.
echo ============================================================
echo  To schedule daily backups, add this script to Windows
echo  Task Scheduler to run at 23:00 daily.
echo ============================================================
echo.
pause