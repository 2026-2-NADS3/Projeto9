#!/bin/bash

ORIGEM="$HOME/proxima-etapa-so/dados"
DESTINO="$HOME/proxima-etapa-so/backups"
LOG="$HOME/proxima-etapa-so/logs/backup.log"

DATA=$(date +"%Y-%m-%d_%H-%M-%S")
ARQUIVO="backup_$DATA.tar.gz"

mkdir -p "$DESTINO"
mkdir -p "$(dirname "$LOG")"

echo "==========================================" | tee -a "$LOG"
echo "[$(date '+%Y-%m-%d %H:%M:%S')] Iniciando backup..." | tee -a "$LOG"

if [ ! -d "$ORIGEM" ]; then
echo "[$(date '+%Y-%m-%d %H:%M:%S')] ERRO: diretório não encontrado: $ORIGEM" | tee -a "$LOG"
exit 1
fi

TAMANHO=$(du -sh "$ORIGEM" | cut -f1)
echo "[$(date '+%Y-%m-%d %H:%M:%S')] Tamanho dos dados: $TAMANHO" | tee -a "$LOG"

tar -czf "$DESTINO/$ARQUIVO" "$ORIGEM" 2>> "$LOG"

if [ $? -eq 0 ]; then
TAMANHO_BACKUP=$(du -h "$DESTINO/$ARQUIVO" | cut -f1)
echo "[$(date '+%Y-%m-%d %H:%M:%S')] Backup criado: $ARQUIVO" | tee -a "$LOG"
echo "[$(date '+%Y-%m-%d %H:%M:%S')] Tamanho do backup: $TAMANHO_BACKUP" | tee -a "$LOG"
else
echo "[$(date '+%Y-%m-%d %H:%M:%S')] ERRO ao criar o backup." | tee -a "$LOG"
exit 1
fi

BACKUPS=$(find "$DESTINO" -name "backup_*.tar.gz" -type f | sort)

CONTADOR=0

for ARQUIVO_ANTIGO in $BACKUPS; do
CONTADOR=$((CONTADOR + 1))

```
if [ "$CONTADOR" -gt 5 ]; then
    rm -f "$ARQUIVO_ANTIGO"
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] Backup antigo removido: $ARQUIVO_ANTIGO" | tee -a "$LOG"
fi
```

done

echo "[$(date '+%Y-%m-%d %H:%M:%S')] Backup finalizado com sucesso." | tee -a "$LOG"
echo "==========================================" | tee -a "$LOG"

