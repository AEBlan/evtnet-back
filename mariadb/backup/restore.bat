@echo off

if "%~1"=="" (
    echo Usage: restore.bat ^<backup_name^>
    exit /b 1
)

set BACKUP_NAME=%~1
set BACKUP_DIR=C:\app\backups\%BACKUP_NAME%

if not exist "%BACKUP_DIR%" (
    echo Error: Backup not found at %BACKUP_DIR%
    exit /b 1
)

echo Restoring from: %BACKUP_DIR%

docker-compose down -v

docker volume create evtnet-back_mysql_data

docker run --rm -v evtnet-back_mysql_data:/var/lib/mysql -v "%BACKUP_DIR%:/backup" mariadb:11.4 bash -c "mariadb-backup --copy-back --target-dir=/backup && chown -R 999:999 /var/lib/mysql"

docker-compose up -d mariadb

echo Restore completed!