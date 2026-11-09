package com.example.projetodopi.network;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// Cliente HTTP com a biblioteca Volley
// Usa RequestFuture para esperar a resposta: por isso deve ser chamado
// fora da thread principal (a sincronização roda em segundo plano).
public class ApiClient {

    private static final int TEMPO_LIMITE_MS = 8000;

    // Uma única fila de requisições para o app inteiro
    private static RequestQueue fila;

    private final String urlBase;

    public ApiClient(Context context, String urlBase) {
        this.urlBase = urlBase.endsWith("/") ? urlBase : urlBase + "/";
        synchronized (ApiClient.class) {
            if (fila == null) {
                fila = Volley.newRequestQueue(context.getApplicationContext());
            }
        }
    }

    // GET: busca dados na API
    public String get(String caminho) throws IOException {
        return executar(Request.Method.GET, caminho, null);
    }

    // POST: envia dados no formato chave=valor
    public String post(String caminho, Map<String, String> parametros) throws IOException {
        return executar(Request.Method.POST, caminho, parametros);
    }

    private String executar(int metodo, String caminho, Map<String, String> parametros) throws IOException {
        final byte[] corpo = parametros == null ? null : montarCorpo(parametros);

        // RequestFuture funciona como "resposta" e "erro" ao mesmo tempo
        RequestFuture<String> futuro = RequestFuture.newFuture();
        StringRequest requisicao = new StringRequest(metodo, urlBase + caminho, futuro, futuro) {
            @Override
            public byte[] getBody() {
                return corpo;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };
        requisicao.setShouldCache(false);   // sempre busca dados atualizados
        requisicao.setRetryPolicy(new DefaultRetryPolicy(TEMPO_LIMITE_MS, 0, 1f)); // sem reenvio duplicado
        fila.add(requisicao);

        try {
            return futuro.get(TEMPO_LIMITE_MS + 2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Requisição interrompida", e);
        } catch (ExecutionException | TimeoutException e) {
            // Sem internet, servidor fora do ar, tempo esgotado ou erro HTTP
            throw new IOException("Não foi possível acessar a API", e);
        }
    }

    // Monta "chave1=valor1&chave2=valor2", codificando acentos e símbolos
    private byte[] montarCorpo(Map<String, String> parametros) throws IOException {
        StringBuilder dados = new StringBuilder();
        for (Map.Entry<String, String> item : parametros.entrySet()) {
            if (dados.length() > 0) {
                dados.append("&");
            }
            String valor = item.getValue() == null ? "" : item.getValue();
            dados.append(URLEncoder.encode(item.getKey(), "UTF-8"))
                    .append("=")
                    .append(URLEncoder.encode(valor, "UTF-8"));
        }
        return dados.toString().getBytes(StandardCharsets.UTF_8);
    }
}
