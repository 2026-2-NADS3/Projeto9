<?php
// GET  mensagens.php?alunoId=1 -> conversa do aluno com a ONG
// POST mensagens.php           -> nova mensagem
include 'config.php';

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    responder(consultar($conn,
        "SELECT id, aluno_id AS alunoId, texto, autor,
                DATE_FORMAT(data_hora, '%Y-%m-%d %H:%i:%s') AS dataHora
           FROM mensagens
          WHERE aluno_id = ?
          ORDER BY data_hora, id",
        "i", [alunoIdDaUrl()]));
}

$texto = parametro('texto');
$autor = parametro('autor') === 'ong' ? 'ong' : 'aluno';
if ($texto === '') {
    responder(["success" => false, "message" => "A mensagem está vazia."], 400);
}

$id = executar($conn,
    "INSERT INTO mensagens (aluno_id, texto, autor, data_hora) VALUES (?, ?, ?, ?)",
    "isss", [(int) parametro('alunoId'), $texto, $autor, parametro('dataHora')]);

responder(["success" => true, "message" => "Mensagem enviada.", "id" => $id]);
