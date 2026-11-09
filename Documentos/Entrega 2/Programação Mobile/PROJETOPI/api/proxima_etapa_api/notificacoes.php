<?php
// GET notificacoes.php: avisos e novos cursos enviados pela Próxima Etapa
include 'config.php';

responder(consultar($conn,
    "SELECT id, titulo, mensagem, tipo,
            DATE_FORMAT(data, '%Y-%m-%d %H:%i') AS data,
            IFNULL(curso_id, 0) AS cursoId
       FROM notificacoes
      ORDER BY data DESC"));
