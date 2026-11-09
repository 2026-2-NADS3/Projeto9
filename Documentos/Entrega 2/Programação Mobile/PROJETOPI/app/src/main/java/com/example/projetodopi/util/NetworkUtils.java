package com.example.projetodopi.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;

// Verifica a conexão com a internet e avisa quando ela cai ou volta
public final class NetworkUtils {

    private NetworkUtils() {
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        Network rede = cm.getActiveNetwork();
        if (rede == null) {
            return false;
        }
        NetworkCapabilities capacidades = cm.getNetworkCapabilities(rede);
        return capacidades != null && capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    public interface OnConexaoMudouListener {
        void onConexaoMudou(boolean online);
    }

    // Monitor de conexão: usado na Área do Aluno para mostrar o aviso "sem conexão"
    public static class Monitor {

        private final Context context;
        private final ConnectivityManager cm;
        private final OnConexaoMudouListener listener;
        private Boolean ultimoEstado = null;   // avisa só quando a situação muda
        private final ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                avisar();
            }

            @Override
            public void onLost(@NonNull Network network) {
                avisar();
            }
        };
        private boolean registrado = false;

        public Monitor(Context context, OnConexaoMudouListener listener) {
            this.context = context.getApplicationContext();
            this.cm = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
            this.listener = listener;
        }

        private void avisar() {
            AppExecutors.main().post(() -> {
                boolean online = isOnline(context);
                if (ultimoEstado == null || ultimoEstado != online) {
                    ultimoEstado = online;
                    listener.onConexaoMudou(online);
                }
            });
        }

        public void iniciar() {
            if (cm != null && !registrado) {
                cm.registerDefaultNetworkCallback(callback);
                registrado = true;
            }
        }

        public void parar() {
            if (cm != null && registrado) {
                cm.unregisterNetworkCallback(callback);
                registrado = false;
            }
        }
    }
}
