#!/bin/bash

echo "====== RESTAURACIÓN DE COPIAS DE SEGURIDAD (EVTNET) ======"

read -p "Ingrese el directorio de la copia a restaurar: " BACKUP_DIR
if [ ! -d "$BACKUP_DIR" ]; then
    echo "El directorio no existe."
    exit 1
fi

read -p "Host de la base de datos (por ej. localhost): " DB_HOST
read -p "Usuario de la base de datos: " DB_USER
read -s -p "Password del usuario: " DB_PASS
echo
read -p "Directorio de datos de MariaDB (por ej. /var/lib/mysql): " MYSQL_DATA

if [ ! -d "$MYSQL_DATA" ]; then
    echo "El directorio de datos no existe."
    exit 1
fi

if [ "$(ls -A $MYSQL_DATA)" ]; then
    echo "La base de datos debe estar VACÍA (sin estructura ni datos)."
    exit 1
fi

find_complete_base() {
    local dir="$1"
    local parent="$(dirname "$dir")"
    local name="$(basename "$dir")"

    IFS="_" read -r prefix id tipo depende fecha hora <<< "$name"

    if [[ "$tipo" == "auto_comp" || "$tipo" == "manual" ]]; then
        echo "$dir"
        return
    fi

    for d in "$parent"/backup_*; do
        base="$(basename "$d")"
        IFS="_" read -r p2 id2 tipo2 depende2 fecha2 hora2 <<< "$base"
        if [[ "$tipo2" == "auto_comp" ]] && [[ "$id2" -eq "$depende" ]]; then
            echo "$d"
            return
        fi
    done

    echo "$dir"
}

find_incrementals_after() {
    local dir="$1"
    local parent="$(dirname "$dir")"
    local base_id=$(basename "$dir" | awk -F"_" '{print $2}')

    for d in "$parent"/backup_*; do
        local bn=$(basename "$d")
        IFS="_" read -r p id tipo depende fecha hora <<< "$bn"

        if [[ "$tipo" == "auto_inc" ]] && [[ "$depende" -eq "$base_id" ]]; then
            echo "$d"
        fi
    done
}

echo "Determinando cadena de restauración..."

BASE=$(find_complete_base "$BACKUP_DIR")
CHAIN=("$BASE")

NEXT="$BASE"
while true; do
    INC=$(find_incrementals_after "$NEXT")
    if [ -z "$INC" ]; then break; fi
    for i in $INC; do
        CHAIN+=("$i")
        NEXT="$i"
    done
done

echo "Cadena construida:"
for c in "${CHAIN[@]}"; do
    echo " - $c"
done

LOG_DIR="/var/log/mysql"
TS="$(date +%Y-%m-%d_%H-%M-%S)"
mkdir -p "$LOG_DIR/old/$TS"
if [ -d "$LOG_DIR" ]; then
    cp -r "$LOG_DIR"/* "$LOG_DIR/old/$TS" 2>/dev/null
fi

systemctl stop mariadb

rm -rf "$MYSQL_DATA"/*
mkdir -p "$MYSQL_DATA"

i=0
for DIR in "${CHAIN[@]}"; do
    echo "Restaurando: $DIR"
    if [ $i -eq 0 ]; then
        mariabackup --prepare --target-dir="$DIR/data"
        mariabackup --copy-back --target-dir="$DIR/data" --datadir="$MYSQL_DATA"
    else
        mariabackup --prepare --target-dir="$DIR/data" --incremental-dir="$DIR/data"
        mariabackup --copy-back --target-dir="$DIR/data" --datadir="$MYSQL_DATA"
    fi
    i=$((i+1))
done

chown -R mysql:mysql "$MYSQL_DATA"

systemctl start mariadb

echo "====== RESTAURACIÓN COMPLETA EXITOSA ======"
