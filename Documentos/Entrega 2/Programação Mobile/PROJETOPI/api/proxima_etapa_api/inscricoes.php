<?php
// GET  inscricoes.php?alunoId=1 -> lista as inscrições e o progresso do aluno
// POST inscricoes.php           -> cria (sem id) ou atualiza (com id) uma inscrição
include 'config.php';

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $linhas = consultar($conn,
        "SELECT id, aluno_id AS alunoId, curso_id AS cursoId, aulas_assistidas AS aulasAssistidas,
                total_aulas AS totalAulas, concluido,
                IFNULL(DATE_FORMAT(data_conclusao, '%Y-%m-%d'), '') AS dataConclusao,
                IFNULL(codigo_certificado, '') AS codigoCertificado
           FROM inscricoes
          WHERE aluno_id = ?",
        "i", [alunoIdDaUrl()]);
    foreach ($linhas as &$linha) {
        $linha['concluido'] = (bool) $linha['concluido']; // JSON true/false
    }
    responder($linhas);
}

$alunoId  = (int) parametro('alunoId');
$cursoId  = (int) parametro('cursoId');
$aulas    = (int) parametro('aulasAssistidas');
$total    = (int) parametro('totalAulas');
$concluido = parametro('concluido') === '1' ? 1 : 0;
$dataConclusao = isset($_POST['dataConclusao']) ? trim($_POST['dataConclusao']) : '';
$codigo   = isset($_POST['codigoCertificado']) ? trim($_POST['codigoCertificado']) : '';

if ($aulas < 0 || $total < 1 || $aulas > $total) {
    responder(["success" => false, "message" => "Quantidade de aulas inválida."], 400);
}

if (isset($_POST['id'])) {
    // Atualiza o progresso (só se a inscrição for deste aluno)
    $id = (int) $_POST['id'];
    executar($conn,
        "UPDATE inscricoes
            SET aulas_assistidas = ?, concluido = ?, data_conclusao = NULLIF(?, ''),
                codigo_certificado = NULLIF(?, '')
          WHERE id = ? AND aluno_id = ?",
        "iissii", [$aulas, $concluido, $dataConclusao, $codigo, $id, $alunoId]);
    responder(["success" => true, "message" => "Inscrição atualizada.", "id" => $id]);
}

// Nova inscrição. Se já existir para o mesmo curso, devolve a existente (sem duplicar).
$id = executar($conn,
    "INSERT INTO inscricoes (aluno_id, curso_id, aulas_assistidas, total_aulas, concluido)
     VALUES (?, ?, ?, ?, ?)
     ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id)",
    "iiiii", [$alunoId, $cursoId, $aulas, $total, $concluido]);

responder(["success" => true, "message" => "Inscrição realizada.", "id" => $id]);
