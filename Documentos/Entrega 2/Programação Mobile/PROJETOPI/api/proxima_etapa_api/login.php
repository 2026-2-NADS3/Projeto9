<?php
// POST login.php (email, senha): autentica o aluno
include 'config.php';

$email = parametro('email');
$senha = parametro('senha');

// A senha é guardada como hash SHA-256, nunca em texto puro
$linhas = consultar($conn,
    "SELECT id, nome, email, telefone, escola, serie, perfil_vocacional AS perfilVocacional
       FROM alunos
      WHERE email = ? AND senha_hash = SHA2(?, 256)",
    "ss", [$email, $senha]);

if (count($linhas) === 0) {
    responder(["success" => false, "message" => "E-mail ou senha inválidos."]);
}
responder(["success" => true, "message" => "Login realizado com sucesso!", "aluno" => $linhas[0]]);
