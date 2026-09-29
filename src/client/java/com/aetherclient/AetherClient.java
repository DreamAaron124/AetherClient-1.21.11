package com.aetherclient;

import com.aetherclient.config.ClientConfig;
import com.aetherclient.gui.ClickGuiScreen;
import com.aetherclient.module.SpawnerFinder;
import com.aetherclient.module.StorageFinder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public final class AetherClient implements ClientModInitializer {
    public static final ClientConfig CONFIG = ClientConfig.load();
    private static KeyBinding clickGuiKey;

    @Override
    public void onInitializeClient() {
        clickGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.aetherclient.clickgui", GLFW.GLFW_KEY_RIGHT_SHIFT, "category.aetherclient"));

        StorageFinder.init();
        SpawnerFinder.init();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (clickGuiKey.wasPressed() && client.currentScreen == null) {
                client.setScreen(new ClickGuiScreen());
            }
        });
    }

    public static void saveConfig() { CONFIG.save(); }
    public static MinecraftClient client() { return MinecraftClient.getInstance(); }
}
