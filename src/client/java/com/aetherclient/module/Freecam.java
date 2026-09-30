package com.aetherclient.module;

import com.aetherclient.AetherClient;
import com.aetherclient.config.ClientConfig.ModuleConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public final class Freecam {
    private static KeyBinding key;
    private static ArmorStandEntity camera;
    private static double speed = 0.8;

    private Freecam() {}

    public static void init() {
        key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.aetherclient.freecam",
                GLFW.GLFW_KEY_RIGHT_ALT,
                KeyBinding.Category.create(net.minecraft.util.Identifier.of("aetherclient", "category"))
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null || client.player == null) return;

            if (key.wasPressed()) {
                AetherClient.CONFIG.freecam.enabled = !AetherClient.CONFIG.freecam.enabled;
                AetherClient.saveConfig();
                if (AetherClient.CONFIG.freecam.enabled) enable(client.player);
                else disable(client.player);
            }

            if (AetherClient.CONFIG.freecam.enabled && camera != null) tickCamera(client.player);
        });
    }

    private static void enable(ClientPlayerEntity player) {
        if (camera != null) return;
        camera = new ArmorStandEntity(EntityType.ARMOR_STAND, player.getWorld());
        camera.setInvisible(true);
        camera.setNoGravity(true);
        camera.setPosition(player.getX(), player.getY(), player.getZ());
        camera.setYaw(player.getYaw());
        camera.setPitch(player.getPitch());
        player.getWorld().addEntity(camera);
        AetherClient.client().setCameraEntity(camera);
    }

    private static void disable(ClientPlayerEntity player) {
        if (camera != null) {
            camera.discard();
            camera = null;
        }
        AetherClient.client().setCameraEntity(player);
    }

    private static void tickCamera(ClientPlayerEntity player) {
        if (camera == null) return;
        float yaw = camera.getYaw() * MathHelper.RADIANS_PER_DEGREE;
        double forward = 0.0;
        double strafe = 0.0;
        var client = AetherClient.client();

        if (client.options.forwardKey.isPressed()) forward += 1.0;
        if (client.options.backKey.isPressed()) forward -= 1.0;
        if (client.options.rightKey.isPressed()) strafe += 1.0;
        if (client.options.leftKey.isPressed()) strafe -= 1.0;

        double length = Math.sqrt(forward * forward + strafe * strafe);
        if (length > 0.0) {
            forward /= length;
            strafe /= length;
        }

        double dx = (forward * -Math.sin(yaw) + strafe * Math.cos(yaw)) * speed;
        double dz = (forward * Math.cos(yaw) + strafe * Math.sin(yaw)) * speed;
        double dy = 0.0;
        if (client.options.jumpKey.isPressed()) dy += speed;
        if (client.options.sneakKey.isPressed()) dy -= speed;

        camera.setPosition(camera.getX() + dx, camera.getY() + dy, camera.getZ() + dz);
        camera.setYaw(player.getYaw());
        camera.setPitch(player.getPitch());
    }

    public static void shutdown() {
        if (AetherClient.client().player != null) disable(AetherClient.client().player);
    }
}
