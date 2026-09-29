package com.aetherclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public final class ClientConfig {
    public ModuleConfig storage = ModuleConfig.storageDefaults();
    public ModuleConfig spawner = ModuleConfig.spawnerDefaults();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path path() { return FabricLoader.getInstance().getConfigDir().resolve("aetherclient.json"); }

    public static ClientConfig load() {
        try {
            if (Files.exists(path())) return GSON.fromJson(Files.readString(path()), ClientConfig.class);
        } catch (Exception ignored) { }
        return new ClientConfig();
    }

    public void save() {
        try {
            Files.createDirectories(path().getParent());
            Files.writeString(path(), GSON.toJson(this));
        } catch (IOException ignored) { }
    }

    public static final class ModuleConfig {
        public boolean enabled;
        public String mode = "OUTLINE";
        public int red, green, blue;
        public float outlineOpacity = .95f;
        public float fillOpacity = .18f;
        public float lineWidth = 2.0f;
        public int range = 64;

        static ModuleConfig storageDefaults() {
            ModuleConfig c = new ModuleConfig(); c.enabled = true; c.red = 70; c.green = 180; c.blue = 255; return c;
        }
        static ModuleConfig spawnerDefaults() {
            ModuleConfig c = new ModuleConfig(); c.enabled = true; c.mode = "CORNERS"; c.red = 205; c.green = 90; c.blue = 255; return c;
        }
    }
}
