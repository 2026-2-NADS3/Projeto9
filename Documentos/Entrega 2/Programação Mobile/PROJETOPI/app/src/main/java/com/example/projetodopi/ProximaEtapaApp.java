package com.example.projetodopi;

import android.app.Application;

import com.example.projetodopi.util.NotificationHelper;

// Classe Application: roda uma vez quando o app abre
public class ProximaEtapaApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationHelper.criarCanal(this);
    }
}
