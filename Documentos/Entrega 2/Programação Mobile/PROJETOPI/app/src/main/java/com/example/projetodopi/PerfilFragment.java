package com.example.projetodopi;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.projetodopi.data.CheckinDao;
import com.example.projetodopi.data.DatabaseHelper;
import com.example.projetodopi.data.InscricaoDao;
import com.example.projetodopi.data.MensagemDao;
import com.example.projetodopi.data.NotificacaoDao;
import com.example.projetodopi.model.Aluno;
import com.example.projetodopi.model.PerfilVocacional;
import com.example.projetodopi.util.ImageUtils;
import com.example.projetodopi.util.SessionManager;
import com.example.projetodopi.util.TelaUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;

// Perfil do aluno: dados pessoais e acadêmicos, atalhos da jornada e configurações
public class PerfilFragment extends Fragment implements Atualizavel {

    private SessionManager sessao;
    private ImageView ivFotoPerfil;
    private TextView tvNomePerfil, tvEmailPerfil, tvEscolaPerfil, tvPerfilVocacional;
    private TextView tvStatConcluidos, tvStatHoras, tvStatPresencas;
    private View raiz;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        raiz = inflater.inflate(R.layout.fragment_perfil, container, false);
        sessao = new SessionManager(requireContext());

        ivFotoPerfil = raiz.findViewById(R.id.ivFotoPerfil);
        tvNomePerfil = raiz.findViewById(R.id.tvNomePerfil);
        tvEmailPerfil = raiz.findViewById(R.id.tvEmailPerfil);
        tvEscolaPerfil = raiz.findViewById(R.id.tvEscolaPerfil);
        tvPerfilVocacional = raiz.findViewById(R.id.tvPerfilVocacional);
        tvStatConcluidos = raiz.findViewById(R.id.tvStatConcluidos);
        tvStatHoras = raiz.findViewById(R.id.tvStatHoras);
        tvStatPresencas = raiz.findViewById(R.id.tvStatPresencas);
        Button btnEditarPerfil = raiz.findViewById(R.id.btnEditarPerfil);
        MaterialSwitch switchNotificacoes = raiz.findViewById(R.id.switchNotificacoes);

        btnEditarPerfil.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), EditarPerfilActivity.class)));

        // Configuração: liga/desliga as notificações do sistema
        switchNotificacoes.setChecked(sessao.isNotificacoesAtivas());
        switchNotificacoes.setOnCheckedChangeListener((botao, ativo) -> sessao.setNotificacoesAtivas(ativo));

        configurarOpcoes();
        return raiz;
    }

    @Override
    public void onResume() {
        super.onResume();
        atualizarDados();
    }

    private void configurarOpcoes() {
        configurarOpcao(R.id.opcaoInscricoes, R.drawable.ic_cursos, "Minhas inscrições",
                "Progresso e carga horária de cada curso",
                v -> startActivity(new Intent(requireContext(), ResultadoActivity.class)));

        configurarOpcao(R.id.opcaoCertificados, R.drawable.ic_certificado, "Certificados",
                "Cursos concluídos com certificado",
                v -> startActivity(new Intent(requireContext(), CertificadosActivity.class)));

        configurarOpcao(R.id.opcaoHistorico, R.drawable.ic_historico, "Histórico de presenças",
                "Check-ins enviados e pendentes",
                v -> startActivity(new Intent(requireContext(), HistoricoActivity.class)));

        configurarOpcao(R.id.opcaoTeste, R.drawable.ic_teste, "Teste de perfil",
                "Descubra as áreas que combinam com você",
                v -> startActivity(new Intent(requireContext(), TestePerfilActivity.class)));

        configurarOpcao(R.id.opcaoMensagens, R.drawable.ic_chat, "Mensagens",
                "Converse com a equipe da Próxima Etapa",
                v -> startActivity(new Intent(requireContext(), MensagensActivity.class)));

        configurarOpcao(R.id.opcaoNotificacoes, R.drawable.ic_notificacao, "Notificações",
                "Novos cursos e avisos importantes",
                v -> startActivity(new Intent(requireContext(), NotificacoesActivity.class)));

        configurarOpcao(R.id.opcaoApi, R.drawable.ic_link, "Endereço da API",
                sessao.getApiUrl(), v -> mostrarDialogoApi());

        configurarOpcao(R.id.opcaoAvaliar, R.drawable.ic_estrela, "Avaliar o app",
                "Conte o que achou do aplicativo", v -> mostrarDialogoAvaliacao());

        configurarOpcao(R.id.opcaoSair, R.drawable.ic_sair, "Sair da conta",
                "Os dados salvos neste aparelho serão apagados", v -> confirmarSaida());
    }

    // Preenche uma linha incluída com <include layout="@layout/item_opcao_perfil">
    private void configurarOpcao(int idOpcao, @DrawableRes int icone, String titulo, String descricao,
                                 View.OnClickListener acao) {
        View opcao = raiz.findViewById(idOpcao);
        ((ImageView) opcao.findViewById(R.id.ivOpcaoIcone)).setImageResource(icone);
        ((TextView) opcao.findViewById(R.id.tvOpcaoTitulo)).setText(titulo);
        ((TextView) opcao.findViewById(R.id.tvOpcaoDescricao)).setText(descricao);
        opcao.setOnClickListener(acao);
    }

    @Override
    public void atualizarDados() {
        if (getView() == null) {
            return;
        }
        Aluno aluno = sessao.getAluno();
        ImageUtils.carregarFotoPerfil(ivFotoPerfil, sessao.getFotoPath());
        tvNomePerfil.setText(aluno.getNome());
        tvEmailPerfil.setText(aluno.getEmail());
        tvEscolaPerfil.setText(aluno.getEscola() + (aluno.getSerie().isEmpty() ? "" : " • " + aluno.getSerie()));

        // Resultado do teste de perfil
        String area = aluno.getPerfilVocacional();
        if (area == null || area.isEmpty()) {
            tvPerfilVocacional.setText("PERFIL: FAÇA O TESTE");
            TelaUtils.pintarTag(tvPerfilVocacional, R.color.pe_tertiary_container, R.color.pe_on_tertiary_container);
        } else {
            tvPerfilVocacional.setText("PERFIL: " + PerfilVocacional.nome(area).toUpperCase());
            TelaUtils.pintarTag(tvPerfilVocacional, R.color.pe_secondary_container, R.color.pe_on_secondary_container);
        }

        // Dados numéricos da jornada
        InscricaoDao inscricaoDao = new InscricaoDao(requireContext());
        tvStatConcluidos.setText(String.valueOf(inscricaoDao.contarConcluidas()));
        tvStatHoras.setText(inscricaoDao.somarHorasConcluidas() + "h");
        tvStatPresencas.setText(String.valueOf(new CheckinDao(requireContext()).contar()));

        int naoLidas = new NotificacaoDao(requireContext()).contarNaoLidas();
        ((TextView) raiz.findViewById(R.id.opcaoNotificacoes).findViewById(R.id.tvOpcaoDescricao))
                .setText(naoLidas > 0 ? naoLidas + " não lida(s)" : "Novos cursos e avisos importantes");
        int mensagensPendentes = new MensagemDao(requireContext()).listarPendentes().size();
        ((TextView) raiz.findViewById(R.id.opcaoMensagens).findViewById(R.id.tvOpcaoDescricao))
                .setText(mensagensPendentes > 0 ? mensagensPendentes + " mensagem(ns) aguardando envio"
                        : "Converse com a equipe da Próxima Etapa");
    }

    // Permite trocar o endereço da API (ex.: IP do computador ao testar no celular)
    private void mostrarDialogoApi() {
        EditText campo = new EditText(requireContext());
        campo.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        campo.setText(sessao.getApiUrl());
        FrameLayout container = new FrameLayout(requireContext());
        int margem = (int) (20 * getResources().getDisplayMetrics().density);
        container.setPadding(margem, margem / 2, margem, 0);
        container.addView(campo);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Endereço da API")
                .setMessage("Emulador: http://10.0.2.2/proxima_etapa_api/\nCelular: http://IP-DO-COMPUTADOR/proxima_etapa_api/")
                .setView(container)
                .setPositiveButton("Salvar", (dialogo, botao) -> {
                    String url = campo.getText().toString().trim();
                    if (!url.startsWith("http")) {
                        Toast.makeText(requireContext(), "O endereço deve começar com http://", Toast.LENGTH_LONG).show();
                        return;
                    }
                    sessao.setApiUrl(url);
                    ((TextView) raiz.findViewById(R.id.opcaoApi).findViewById(R.id.tvOpcaoDescricao)).setText(url);
                    if (getActivity() instanceof FormActivity) {
                        ((FormActivity) getActivity()).solicitarSincronizacao();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Avaliação do app com RatingBar
        private void mostrarDialogoAvaliacao() {
        RatingBar ratingBar = new RatingBar(requireContext());
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1);
        ratingBar.setRating(sessao.getAvaliacao());
        FrameLayout container = new FrameLayout(requireContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = android.view.Gravity.CENTER;
        container.addView(ratingBar, params);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Avalie nosso app")
                .setView(container)
                .setPositiveButton("Enviar", (dialogo, botao) -> {
                    sessao.setAvaliacao(ratingBar.getRating());
                    Toast.makeText(requireContext(), "Obrigado pela avaliação: "
                            + (int) ratingBar.getRating() + " estrela(s)!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Agora não", null)
                .show();
    }

    private void confirmarSaida() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sair da conta?")
                .setMessage("Registros que ainda não foram sincronizados serão perdidos.")
                .setPositiveButton("Sair", (dialogo, botao) -> {
                    sessao.encerrarSessao();
                    DatabaseHelper.getInstance(requireContext()).limparTudo();
                    Intent intent = new Intent(requireContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
