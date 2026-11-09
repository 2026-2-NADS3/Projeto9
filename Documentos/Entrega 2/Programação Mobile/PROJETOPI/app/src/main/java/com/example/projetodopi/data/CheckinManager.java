package com.example.projetodopi.data;

import android.content.Context;

import com.example.projetodopi.model.ItemAgenda;
import com.example.projetodopi.model.Notificacao;
import com.example.projetodopi.util.DateUtils;
import com.example.projetodopi.util.NotificationHelper;

// Regra do check-in: valida o código do QR Code e grava a presença no banco local
public final class CheckinManager {

    public static class Resultado {
        public final boolean sucesso;
        public final String titulo;
        public final String mensagem;

        Resultado(boolean sucesso, String titulo, String mensagem) {
            this.sucesso = sucesso;
            this.titulo = titulo;
            this.mensagem = mensagem;
        }
    }

    private CheckinManager() {
    }

    public static Resultado registrar(Context context, String codigoLido) {
        String codigo = codigoLido == null ? "" : codigoLido.trim();

        // 1. O código precisa pertencer a um encontro da agenda do aluno
        ItemAgenda encontro = new AgendaDao(context).buscarPorCodigo(codigo);
        if (encontro == null) {
            return new Resultado(false, "QR Code inválido",
                    "O código \"" + codigo + "\" não pertence a nenhum encontro da sua agenda.");
        }

        // 2. Não pode registrar presença duas vezes no mesmo encontro
        CheckinDao checkinDao = new CheckinDao(context);
        if (checkinDao.existeParaAgenda(encontro.getId())) {
            return new Resultado(false, "Presença já registrada",
                    "Você já confirmou presença em \"" + encontro.getTitulo() + "\".");
        }

        // 3. Grava a presença no aparelho (persistência local) e atualiza o progresso do curso
        checkinDao.inserir(encontro.getId(), codigo, DateUtils.agora());
        boolean concluiuCurso = new InscricaoDao(context).registrarAula(encontro.getCursoId());

        NotificacaoDao notificacaoDao = new NotificacaoDao(context);
        String detalhe = encontro.getTitulo() + " — " + DateUtils.formatarData(encontro.getData())
                + " às " + encontro.getHorario();
        notificacaoDao.inserirLocal("Presença confirmada", detalhe, Notificacao.TIPO_PRESENCA,
                encontro.getCursoId());
        NotificationHelper.mostrar(context, "Presença confirmada", detalhe);

        if (concluiuCurso) {
            notificacaoDao.inserirLocal("Curso concluído!",
                    "Parabéns! Seu certificado de \"" + encontro.getTitulo() + "\" já está disponível.",
                    Notificacao.TIPO_AVISO, encontro.getCursoId());
            NotificationHelper.mostrar(context, "Curso concluído!", "Seu certificado já está disponível no app.");
        }

        String mensagem = detalhe + "\nA presença foi salva no aparelho e será enviada para a API na sincronização.";
        if (concluiuCurso) {
            mensagem += "\nVocê concluiu o curso e já pode ver o certificado!";
        }
        return new Resultado(true, "Presença registrada!", mensagem);
    }
}
