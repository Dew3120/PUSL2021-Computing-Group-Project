#!/bin/bash
# ============================================================
#  Group 100 — Centralized Apparel WMS
#  Database Backup Script (Linux/Mac)
#  Performs daily mysqldump of group100_wms database
# ============================================================

# — Configuration —
DB_HOST="127.0.0.1"
DB_PORT="3306"
DB_NAME="group100_wms"
DB_USER="root"
DB_PASS="root"
BACKUP_DIR="$(dirname "$0")/../../backups"

# — Create backup directory if it doesn't exist —
if [ ! -d "$BACKUP_DIR" ]; then
    mkdir -p "$BACKUP_DIR"
    echo "Created backup directory: $BACKUP_DIR"
fi

# — Generate timestamp for filename —
DATESTAMP=$(date +"%Y-%m-%d")
TIMESTAMP=$(date +"%H%M")
FILENAME="${DB_NAME}_backup_${DATESTAMP}_${TIMESTAMP}.sql"

# — Run mysqldump —
echo ""
echo "============================================================"
echo " Group100 WMS — Database Backup"
echo "============================================================"
echo " Database:  $DB_NAME"
echo " Host:      $DB_HOST:$DB_PORT"
echo " Output:    $BACKUP_DIR/$FILENAME"
echo " Time:      $(date)"
echo "============================================================"
echo ""

mysqldump -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASS" \
    --single-transaction --routines --triggers --add-drop-table \
    "$DB_NAME" > "$BACKUP_DIR/$FILENAME"

if [ $? -eq 0 ]; then
    echo "[SUCCESS] Backup completed: $FILENAME"
    echo ""

    # — Delete backups older than 30 days —
    echo "Cleaning up backups older than 30 days..."
    find "$BACKUP_DIR" -name "*.sql" -type f -mtime +30 -delete
    echo "Cleanup complete."
else
    echo "[ERROR] Backup failed! Check MySQL connection and credentials."
    echo "  - Is MySQL running?"
    echo "  - Are credentials correct? User: $DB_USER"
fi

echo ""
echo "============================================================"
echo " To schedule daily backups, add this to crontab:"
echo " 0 23 * * * $(realpath "$0")"
echo "============================================================"
echo ""