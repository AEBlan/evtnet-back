@echo off

if "%~1"=="" (
    echo Usage: backup.bat ^<backup_name^>
    exit /b 1
)

set BACKUP_NAME=%~1
set BACKUP_DIR=C:\app\backups_b\%BACKUP_NAME%

echo Creating backup: %BACKUP_NAME%

docker exec evtnet-mariadb mariadb-backup --backup --target-dir=/tmp/backup --user=root --password=root_pass

docker exec evtnet-mariadb mariadb-backup --prepare --target-dir=/tmp/backup

if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"
docker cp evtnet-mariadb:/tmp/backup/. "%BACKUP_DIR%/"

docker exec evtnet-mariadb rm -rf /tmp/backup

echo Backup completed: %BACKUP_DIR%