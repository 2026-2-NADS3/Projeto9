<?php
// GET  checkins.php?alunoId=1 -> histórico de presenças do aluno
// POST checkins.php           -> registra uma presença lida pelo QR Code
include 'config.php';

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    responder(consultar($conn,
        "SELECT id, aluno_id AS alunoId, agenda_id AS agendaId, codigo_qr AS codigoQr,
                DATE_FORMAT(data_hora, '%Y-%m-%d %H:%i:%s') AS dataHora
           FROM checkins
          WHERE aluno_id = ?
          ORDER BY data_hora DESC",
        "i", [alunoIdDaUrl()]));
}

$alunoId  = (int) parametro('alunoId');
$agendaId = (int) parametro('agendaId');
$codigo   = parametro('codigoQr');
$dataHora = parametro('dataHora');

// Regra de negócio: o QR Code precisa ser do encontro e o encontro precisa ser do aluno
$encontro = consultar($conn,
    "SELECT id FROM agenda WHERE id = ? AND aluno_id = ? AND codigo_qr = ?",
    "iis", [$agendaId, $alunoId, $codigo]);
if (count($encontro) === 0) {
    responder(["success" => false, "message" => "QR Code não pertence a um encontro deste aluno."], 400);
}

// A chave única (aluno_id, agenda_id) evita presença duplicada no mesmo encontro
$id = executar($conn,
    "INSERT INTO checkins (aluno_id, agenda_id, codigo_qr, data_hora)
     VALUES (?, ?, ?, ?)
     ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id)",
    "iiss", [$alunoId, $agendaId, $codigo, $dataHora]);

responder(["success" => true, "message" => "Presença registrada.", "id" => $id]);
