<?php
// Configuração da conexão com o banco MySQL (XAMPP) e funções usadas por todos os scripts.
// As variáveis de ambiente permitem reaproveitar a API no Docker (Cloud Native).
header('Content-Type: application/json; charset=utf-8');

$servidor = getenv('DB_HOST') ?: 'localhost';
$usuario  = getenv('DB_USER') ?: 'root';
$senha    = getenv('DB_PASS') ?: '';
$banco    = getenv('DB_NAME') ?: 'proxima_etapa';

mysqli_report(MYSQLI_REPORT_OFF);
$conn = mysqli_init();
$conn->options(MYSQLI_OPT_INT_AND_FLOAT_NATIVE, 1); // números saem como números no JSON
if (!@$conn->real_connect($servidor, $usuario, $senha, $banco)) {
    responder(["success" => false, "message" => "Falha na conexão com o banco de dados."], 500);
}
$conn->set_charset('utf8mb4');

// Envia a resposta em JSON e encerra o script
function responder($dados, $status = 200) {
    http_response_code($status);
    echo json_encode($dados, JSON_UNESCAPED_UNICODE);
    exit;
}

// Lê um parâmetro obrigatório enviado por POST
function parametro($nome) {
    if (!isset($_POST[$nome])) {
        responder(["success" => false, "message" => "Parâmetro ausente: $nome"], 400);
    }
    return trim($_POST[$nome]);
}

// Lê o alunoId da URL (ex.: agenda.php?alunoId=1)
function alunoIdDaUrl() {
    if (!isset($_GET['alunoId'])) {
        responder(["success" => false, "message" => "Informe o alunoId"], 400);
    }
    return (int) $_GET['alunoId'];
}

// Consulta parametrizada (prepared statement): evita SQL Injection
function consultar($conn, $sql, $tipos = "", $valores = []) {
    $stmt = $conn->prepare($sql);
    if ($stmt === false) {
        responder(["success" => false, "message" => "Erro na consulta."], 500);
    }
    if ($tipos !== "") {
        $stmt->bind_param($tipos, ...$valores);
    }
    if (!$stmt->execute()) {
        responder(["success" => false, "message" => "Erro ao executar a consulta."], 500);
    }
    $resultado = $stmt->get_result();
    return $resultado === false ? [] : $resultado->fetch_all(MYSQLI_ASSOC);
}

// Executa INSERT/UPDATE e devolve o id do registro
function executar($conn, $sql, $tipos, $valores) {
    $stmt = $conn->prepare($sql);
    if ($stmt === false) {
        responder(["success" => false, "message" => "Erro na consulta."], 500);
    }
    $stmt->bind_param($tipos, ...$valores);
    if (!$stmt->execute()) {
        responder(["success" => false, "message" => "Erro ao gravar: " . $stmt->error], 500);
    }
    return $conn->insert_id;
}
