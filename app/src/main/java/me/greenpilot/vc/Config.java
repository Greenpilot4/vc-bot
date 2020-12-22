package me.greenpilot.vc;

import io.github.cdimascio.dotenv.Dotenv;

class config {

    private static final Dotenv dotenv = Dotenv.load();

    public static String Get(String key) {
        return dotenv.get(key.toUpperCase());
    }
}
