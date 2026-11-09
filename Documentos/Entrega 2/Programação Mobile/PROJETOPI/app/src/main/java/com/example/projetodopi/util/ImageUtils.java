package com.example.projetodopi.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import com.example.projetodopi.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// Funções de imagem: imagens dos cursos e foto de perfil do aluno
public final class ImageUtils {

    private static final String ARQUIVO_FOTO = "foto_perfil.jpg";

    private ImageUtils() {
    }

    // A API envia o nome da imagem (ex.: "curso1"); buscamos esse nome na pasta drawable
    public static void carregarImagemCurso(ImageView imageView, String nomeImagem) {
        Context context = imageView.getContext();
        int idImagem = 0;
        if (nomeImagem != null && !nomeImagem.isEmpty()) {
            idImagem = context.getResources().getIdentifier(nomeImagem, "drawable", context.getPackageName());
        }
        imageView.setImageResource(idImagem != 0 ? idImagem : R.drawable.proximaetapa);
    }

    // Copia a foto escolhida na galeria para a pasta interna do app
    public static String salvarFotoPerfil(Context context, Uri origem) throws IOException {
        File destino = new File(context.getFilesDir(), ARQUIVO_FOTO);
        try (InputStream entrada = context.getContentResolver().openInputStream(origem);
             OutputStream saida = new FileOutputStream(destino)) {
            if (entrada == null) {
                throw new IOException("Não foi possível abrir a imagem");
            }
            byte[] buffer = new byte[8192];
            int lidos;
            while ((lidos = entrada.read(buffer)) != -1) {
                saida.write(buffer, 0, lidos);
            }
        }
        return destino.getAbsolutePath();
    }

    // Mostra a foto de perfil (ou o ícone padrão se não houver foto)
    public static void carregarFotoPerfil(ImageView imageView, String caminho) {
        if (caminho != null && !caminho.isEmpty() && new File(caminho).exists()) {
            BitmapFactory.Options opcoes = new BitmapFactory.Options();
            opcoes.inSampleSize = 4; // reduz a imagem para economizar memória
            Bitmap bitmap = BitmapFactory.decodeFile(caminho, opcoes);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                return;
            }
        }
        imageView.setImageResource(R.drawable.ic_perfil);
    }
}
