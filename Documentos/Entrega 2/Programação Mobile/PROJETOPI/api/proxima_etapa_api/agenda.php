<?php
// GET agenda.php?alunoId=1: agenda individual (só os encontros do aluno)
include 'config.php';

$agenda = consultar($conn,
    "SELECT id, aluno_id AS alunoId, curso_id AS cursoId, titulo,
            DATE_FORMAT(data, '%Y-%m-%d') AS data, horario, local, codigo_qr AS codigoQr
       FROM agenda
      WHERE aluno_id = ?
      ORDER BY data, horario",
    "i", [alunoIdDaUrl()]);

responder($agenda);
