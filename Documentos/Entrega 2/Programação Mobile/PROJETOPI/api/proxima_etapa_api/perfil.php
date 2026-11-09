<?php
// POST perfil.php: atualiza os dados do perfil e o resultado do teste de perfil
include 'config.php';

$id = (int) parametro('id');
executar($conn,
    "UPDATE alunos SET nome = ?, telefone = ?, escola = ?, serie = ?, perfil_vocacional = ? WHERE id = ?",
    "sssssi",
    [parametro('nome'), parametro('telefone'), parametro('escola'), parametro('serie'),
     parametro('perfilVocacional'), $id]);

responder(["success" => true, "message" => "Perfil atualizado.", "id" => $id]);
