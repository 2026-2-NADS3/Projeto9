package com.example.projetodopi.data;

import android.content.Context;

import com.example.projetodopi.model.Aluno;
import com.example.projetodopi.model.Checkin;
import com.example.projetodopi.model.Inscricao;
import com.example.projetodopi.model.Mensagem;
import com.example.projetodopi.model.Notificacao;
import com.example.projetodopi.network.ApiService;
import com.example.projetodopi.util.AppExecutors;
import com.example.projetodopi.util.DateUtils;
import com.example.projetodopi.util.NetworkUtils;
import com.example.projetodopi.util.NotificationHelper;
import com.example.projetodopi.util.SessionManager;

import org.json.JSONException;

import java.io.IOException;

// Sincronização entre o banco local e a API:
// 1) envia o que foi feito sem internet (check-ins, inscrições, mensagens, perfil)
// 2) baixa os dados atualizados da API e salva no SQLite
public final class SyncManager {

    public interface Callback {
        void onConcluido(Resultado resultado);
    }

    public static class Resultado {
        public boolean sucesso;
        public String mensagem;
        public int enviados;
        public int novasNotificacoes;
    }

    private SyncManager() {
    }

    // Roda em segundo plano e devolve o resultado na thread principal
    public static void sincronizar(Context context, Callback callback) {
        Context app = context.getApplicationContext();
        AppExecutors.io().execute(() -> {
            Resultado resultado = executar(app);
            AppExecutors.main().post(() -> {
                if (callback != null) {
                    callback.onConcluido(resultado);
                }
            });
        });
    }

    private static synchronized Resultado executar(Context context) {
        Resultado resultado = new Resultado();
        SessionManager sessao = new SessionManager(context);

        if (!NetworkUtils.isOnline(context)) {
            resultado.sucesso = false;
            resultado.mensagem = "Sem conexão. Seus dados ficaram salvos e serão enviados quando a internet voltar.";
            return resultado;
        }

        ApiService api = new ApiService(context, sessao.getApiUrl());
        int alunoId = sessao.getAlunoId();
        boolean primeiraSincronizacao = sessao.getUltimaSincronizacao().isEmpty();

        try {
            // ---------- 1. Envio dos dados pendentes ----------
            CheckinDao checkinDao = new CheckinDao(context);
            for (Checkin checkin : checkinDao.listarPendentes()) {
                int remoteId = api.enviarCheckin(alunoId, checkin);
                checkinDao.marcarSincronizado(checkin.getId(), remoteId);
                resultado.enviados++;
            }

            InscricaoDao inscricaoDao = new InscricaoDao(context);
            for (Inscricao inscricao : inscricaoDao.listarPendentes()) {
                if (inscricao.getRemoteId() == 0) {
                    int remoteId = api.criarInscricao(alunoId, inscricao);
                    inscricaoDao.marcarSincronizada(inscricao.getId(), remoteId);
                } else {
                    api.atualizarInscricao(alunoId, inscricao);
                    inscricaoDao.marcarSincronizada(inscricao.getId(), inscricao.getRemoteId());
                }
                resultado.enviados++;
            }

            MensagemDao mensagemDao = new MensagemDao(context);
            for (Mensagem mensagem : mensagemDao.listarPendentes()) {
                int remoteId = api.enviarMensagem(alunoId, mensagem);
                mensagemDao.marcarSincronizada(mensagem.getId(), remoteId);
                resultado.enviados++;
            }

            if (sessao.isPerfilPendente()) {
                Aluno aluno = sessao.getAluno();
                api.atualizarPerfil(aluno);
                sessao.setPerfilPendente(false);
                resultado.enviados++;
            }

            // ---------- 2. Download dos dados da API ----------
            new CursoDao(context).substituirTodos(api.listarCursos());
            new AgendaDao(context).substituirTodos(api.listarAgenda(alunoId));
            inscricaoDao.substituirSincronizadas(api.listarInscricoes(alunoId));

            for (Checkin checkin : api.listarCheckins(alunoId)) {
                checkinDao.inserirRemotoSeNaoExistir(checkin);
            }

            for (Mensagem mensagem : api.listarMensagens(alunoId)) {
                mensagemDao.salvarRemotaSeNaoExistir(mensagem);
            }

            // Notificações novas da API também aparecem na barra do celular
            NotificacaoDao notificacaoDao = new NotificacaoDao(context);
            for (Notificacao notificacao : api.listarNotificacoes()) {
                boolean nova = notificacaoDao.salvarRemota(notificacao);
                if (nova && !primeiraSincronizacao) {
                    resultado.novasNotificacoes++;
                    NotificationHelper.mostrar(context, notificacao.getTitulo(), notificacao.getMensagem());
                }
            }

            sessao.setUltimaSincronizacao(DateUtils.agora());
            sessao.setModoOffline(false);
            resultado.sucesso = true;
            resultado.mensagem = montarMensagemSucesso(resultado);
        } catch (IOException e) {
            // Perda de conexão ou API fora do ar: o app continua com os dados do SQLite
            sessao.setModoOffline(true);
            resultado.sucesso = false;
            resultado.mensagem = "Não foi possível acessar a API. Mostrando os dados salvos no aparelho.";
        } catch (JSONException e) {
            resultado.sucesso = false;
            resultado.mensagem = "A API respondeu em um formato inesperado.";
        }
        return resultado;
    }

    private static String montarMensagemSucesso(Resultado resultado) {
        StringBuilder texto = new StringBuilder("Dados sincronizados com a API");
        if (resultado.enviados > 0) {
            texto.append(" • ").append(resultado.enviados).append(" registro(s) enviado(s)");
        }
        if (resultado.novasNotificacoes > 0) {
            texto.append(" • ").append(resultado.novasNotificacoes).append(" nova(s) notificação(ões)");
        }
        return texto.toString();
    }
}
