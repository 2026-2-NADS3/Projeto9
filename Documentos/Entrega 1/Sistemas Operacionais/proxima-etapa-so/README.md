# Backup Automatizado - Próxima Etapa

## 1. Descrição

Este projeto foi desenvolvido para a automação de backups dos dados da aplicação da ONG Próxima Etapa.

O objetivo é utilizar recursos do sistema operacional Linux para realizar backups de forma automatizada, registrar as operações realizadas e manter somente os backups mais recentes.

Os dados utilizados neste projeto são fictícios e foram criados apenas para testes.

## 2. Estrutura do projeto

```text
proxima-etapa-so/
├── dados/
│   ├── alunos.txt
│   └── cursos.txt
├── backups/
├── logs/
│   └── backup.log
├── scripts/
│   └── backup.sh
└── README.md
```

## 3. Tecnologias e comandos utilizados

* Ubuntu Linux
* Bash
* tar
* find
* sort
* rm
* date
* mkdir
* chmod

## 4. Funcionamento

O script `backup.sh` realiza as seguintes etapas:

1. Define os diretórios de origem, destino e arquivo de log.
2. Obtém a data e hora da execução.
3. Verifica se o diretório de dados existe.
4. Cria um arquivo compactado `.tar.gz` com os dados.
5. Registra o resultado da operação no arquivo de log.
6. Identifica os backups existentes.
7. Utiliza um loop para controlar a quantidade de backups.
8. Remove backups antigos quando existem mais de 5 arquivos.
9. Registra a finalização da operação.

## 5. Como executar

Acesse o diretório do projeto:

```bash
cd ~/proxima-etapa-so
```

Dê permissão de execução ao script:

```bash
chmod +x scripts/backup.sh
```

Execute:

```bash
./scripts/backup.sh
```

## 6. Verificar os backups

Para visualizar os backups criados:

```bash
ls -lh backups
```

Exemplo:

```text
backup_2026-09-24_00-02-39.tar.gz
```

## 7. Verificar o log

O histórico das operações pode ser consultado com:

```bash
cat logs/backup.log
```

Exemplo:

```text
[2026-09-24 00:02:39] Iniciando backup...
[2026-09-24 00:02:39] Backup criado: backup_2026-09-24_00-02-39.tar.gz
[2026-09-24 00:02:39] Backup finalizado com sucesso.
```

## 8. Tratamento de erros

O script verifica se o diretório de origem existe.

Caso o diretório não seja encontrado, o script registra o erro no log e encerra a execução.

Também é verificado o código de retorno do comando `tar`. Caso a criação do backup falhe, uma mensagem de erro é registrada no log.

## 9. Controle de backups

O script mantém somente os 5 backups mais recentes.

Para isso, utiliza um loop `for` juntamente com os comandos `find`, `sort` e `rm`.

## 10. Resultado esperado

Ao executar o script corretamente, um novo arquivo `.tar.gz` será criado na pasta `backups/` e a execução será registrada em `logs/backup.log`.

O sistema permite demonstrar conceitos de administração de sistemas Linux, incluindo arquivos e diretórios, permissões, comandos, compactação, redirecionamento, estruturas de repetição e tratamento básico de erros.
