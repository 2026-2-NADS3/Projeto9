package com.example.projetodopi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projetodopi.model.PerfilVocacional;
import com.example.projetodopi.util.TelaUtils;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.LinkedHashMap;
import java.util.Map;

// Teste de perfil: cada resposta soma 1 ponto para uma área; a área com mais pontos é o resultado
public class TestePerfilActivity extends AppCompatActivity {

    private final int[] gruposPerguntas = {
            R.id.rgPergunta1, R.id.rgPergunta2, R.id.rgPergunta3, R.id.rgPergunta4, R.id.rgPergunta5
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teste_perfil);
        TelaUtils.aplicarInsets(findViewById(R.id.main), false);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        Button btnVerResultado = findViewById(R.id.btnVerResultado);
        btnVerResultado.setOnClickListener(v -> calcularResultado());
    }

    private void calcularResultado() {
        // Pontuação de cada área (LinkedHashMap mantém a ordem para desempate)
        Map<String, Integer> pontos = new LinkedHashMap<>();
        pontos.put(PerfilVocacional.ADM, 0);
        pontos.put(PerfilVocacional.FIN, 0);
        pontos.put(PerfilVocacional.COM, 0);
        pontos.put(PerfilVocacional.TEC, 0);

        for (int idGrupo : gruposPerguntas) {
            RadioGroup grupo = findViewById(idGrupo);
            int idMarcado = grupo.getCheckedRadioButtonId();
            if (idMarcado == -1) {
                Toast.makeText(this, "Responda todas as perguntas", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton marcado = findViewById(idMarcado);
            String area = (String) marcado.getTag();
            pontos.put(area, pontos.get(area) + 1);
        }

        // Descobre a área com mais pontos
        String melhorArea = PerfilVocacional.ADM;
        int maiorPontuacao = -1;
        for (Map.Entry<String, Integer> item : pontos.entrySet()) {
            if (item.getValue() > maiorPontuacao) {
                maiorPontuacao = item.getValue();
                melhorArea = item.getKey();
            }
        }

        // Envia o resultado para a próxima tela
        Intent intent = new Intent(TestePerfilActivity.this, ResultadoTesteActivity.class);
        intent.putExtra("area", melhorArea);
        intent.putExtra("pontos", maiorPontuacao);
        intent.putExtra("total", gruposPerguntas.length);
        startActivity(intent);
        finish();
    }
}
