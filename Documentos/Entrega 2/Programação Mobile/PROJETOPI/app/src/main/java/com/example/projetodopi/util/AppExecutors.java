package com.example.projetodopi.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Threads do app: "io" para rede e banco, "main" para atualizar a tela.
// O Android não permite acessar a internet na thread principal.
public final class AppExecutors {

    private static final ExecutorService IO = Executors.newSingleThreadExecutor();
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    private AppExecutors() {
    }

    public static ExecutorService io() {
        return IO;
    }

    public static Handler main() {
        return MAIN;
    }
}
