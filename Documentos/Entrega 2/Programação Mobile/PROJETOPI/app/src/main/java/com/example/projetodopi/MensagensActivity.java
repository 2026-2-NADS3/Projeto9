package com.example.projetodopi;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetodopi.adapter.MensagemAdapter;
import com.example.projetodopi.data.MensagemDao;
import com.example.projetodopi.data.SyncManager;
import com.example.projetodopi.model.Mensagem;
import com.example.projetodopi.util.DateUtils;
import com.example.projetodopi.util.TelaUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

// Mensagens/chat com a ONG: salva no SQLite e envia pela API (fica pendente se estiver offline)
public class MensagensActivity extends AppCompatActivity {

    private MensagemDao mensagemDao;
    private RecyclerView rvMensagens;
    private TextInputEditText etMensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mensagens);
        // true: a caixa de texto sobe junto com o teclado
        TelaUtils.aplicarInsets(findViewById(R.id.main), true);

        mensagemDao = new MensagemDao(this);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        rvMensagens = findViewById(R.id.rvMensagens);
        etMensagem = findViewById(R.id.etMensagem);
        FloatingActionButton btnEnviarMensagem = findViewById(R.id.btnEnviarMensagem);

        toolbar.setNavigationOnClickListener(v -> finish());

        LinearLayoutManager gerenciador = new LinearLayoutManager(this);
        gerenciador.setStackFromEnd(true); // começa mostrando as mensagens mais recentes
        rvMensagens.setLayoutManager(gerenciador);

        btnEnviarMensagem.setOnClickListener(v -> enviar());

        carregarMensagens();
        sincronizar();
    }

    private void enviar() {
        String texto = etMensagem.getText() == null ? "" : etMensagem.getText().toString().trim();
        if (texto.isEmpty()) {
            return;
        }
        mensagemDao.inserirLocal(texto, Mensagem.AUTOR_ALUNO, DateUtils.agora());
        etMensagem.setText("");
        carregarMensagens();
        sincronizar();
    }

    // Envia as mensagens pendentes e busca respostas da ONG na API
    private void sincronizar() {
        SyncManager.sincronizar(this, resultado -> {
            if (!isFinishing()) {
                carregarMensagens();
            }
        });
    }

    private void carregarMensagens() {
        List<Mensagem> mensagens = mensagemDao.listar();
        rvMensagens.setAdapter(new MensagemAdapter(mensagens));
        if (!mensagens.isEmpty()) {
            rvMensagens.scrollToPosition(mensagens.size() - 1);
        }
    }
}
