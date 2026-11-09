package com.example.projetodopi.util;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Funções de tela usadas em várias Activities
public final class TelaUtils {

    private TelaUtils() {
    }

    // Ajusta o espaço das barras do sistema (EdgeToEdge) em todas as telas.
    // incluirTeclado = true: a tela "sobe" quando o teclado abre.
    public static void aplicarInsets(View raiz, boolean incluirTeclado) {
        ViewCompat.setOnApplyWindowInsetsListener(raiz, (v, insets) -> {
            int tipos = WindowInsetsCompat.Type.systemBars();
            if (incluirTeclado) {
                tipos = tipos | WindowInsetsCompat.Type.ime();
            }
            Insets barras = insets.getInsets(tipos);
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    // Pinta uma etiqueta (TextView com fundo bg_tag)
    public static void pintarTag(TextView tag, @ColorRes int corFundo, @ColorRes int corTexto) {
        ViewCompat.setBackgroundTintList(tag,
                ColorStateList.valueOf(ContextCompat.getColor(tag.getContext(), corFundo)));
        tag.setTextColor(ContextCompat.getColor(tag.getContext(), corTexto));
    }
}
