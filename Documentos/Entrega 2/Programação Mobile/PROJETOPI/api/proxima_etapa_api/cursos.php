<?php
// GET cursos.php: lista todos os cursos oferecidos pelas instituições parceiras
include 'config.php';

$cursos = consultar($conn,
    "SELECT id, titulo, descricao, instituicao, area, carga_horaria AS cargaHoraria,
            DATE_FORMAT(data_inicio, '%Y-%m-%d') AS dataInicio,
            DATE_FORMAT(data_fim, '%Y-%m-%d') AS dataFim,
            horario, local, imagem, total_aulas AS totalAulas
       FROM cursos
      ORDER BY data_inicio");

responder($cursos);
