package com.example.projetodopi.util;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.projetodopi.NotificacoesActivity;
import com.example.projetodopi.R;

// Cria o canal e exibe notificações do sistema (barra de status do celular)
public final class NotificationHelper {

    public static final String CANAL_ID = "avisos_proxima_etapa";

    private NotificationHelper() {
    }

    // Android 8+ exige um canal de notificação
    public static void criarCanal(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(CANAL_ID,
                    "Avisos da Próxima Etapa", NotificationManager.IMPORTANCE_DEFAULT);
            canal.setDescription("Novos cursos, lembretes de encontros e confirmações de presença");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(canal);
            }
        }
    }

    public static void mostrar(Context context, String titulo, String texto) {
        if (!new SessionManager(context).isNotificacoesAtivas()) {
            return;
        }
        // Android 13+: só mostra se o usuário deu permissão
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Ao tocar na notificação, abre a tela de notificações
        Intent intent = new Intent(context, NotificacoesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ID)
                .setSmallIcon(R.drawable.ic_stat_notificacao)
                .setColor(ContextCompat.getColor(context, R.color.pe_primary))
                .setContentTitle(titulo)
                .setContentText(texto)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(texto))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        int idNotificacao = (int) (System.currentTimeMillis() % 100000);
        NotificationManagerCompat.from(context).notify(idNotificacao, builder.build());
    }
}
