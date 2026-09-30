package com.aetherclient.module;

import com.aetherclient.AetherClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MarkerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public final class Freecam {
    private static KeyBinding key;
    private static MarkerEntity camera;
    private static double speed = 0.8;
    private static double savedX, savedY, savedZ;
    private static float savedYaw, savedPitch;

    private Freecam() {}

    public static void init() {
        key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.aetherclient.freecam",
                GLFW.GLFW_KEY_RIGHT_ALT,
                KeyBinding.Category.create(Identifier.of("aetherclient", "category"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null || client.player == null) return;

            if (key.wasPressed()) {
                if (camera == null) enable(client.player);
                else disable(client.player);
            }

            if (camera != null) tickCamera(client.player);
        });
    }

    private static void enable(ClientPlayerEntity player) {
        savedX = player.getX();
        savedY = player.getY();
        savedZ = player.getZ();
        savedYaw = player.getYaw();
        savedPitch = player.getPitch();

        camera = new MarkerEntity(EntityType.MARKER, player.getWorld());
        camera.setPosition(savedX, savedY + player.getStandingEyeHeight(), savedZ);
        camera.setYaw(savedYaw);
        camera.setPitch(savedPitch);
        player.getWorld().addEntity(camera);
        AetherClient.client().setCameraEntity(camera);

        AetherClient.CONFIG.freecam.enabled = true;
        AetherClient.saveConfig();
    }

    private static void disable(ClientPlayerEntity player) {
        if (camera != null) {
            camera.discard();
            camera = null;
        }
        player.setPosition(savedX, savedY, savedZ);
        player.setYaw(savedYaw);
        player.setPitch(savedPitch);
        AetherClient.client().setCameraEntity(player);

        AetherClient.CONFIG.freecam.enabled = false;
        AetherClient.saveConfig();
    }

    private static void tickCamera(ClientPlayerEntity player) {
        if (camera == null) return;

        // Minecraft continues to update the player's view rotation from the mouse.
        // We use that rotation for the detached camera, then restore the player's
        // original rotation so the player itself does not visually turn.
        float yaw = player.getYaw();
        float pitch = player.getPitch();

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

        double yawRad = yaw * MathHelper.RADIANS_PER_DEGREE;
        double dx = (forward * -Math.sin(yawRad) + strafe * Math.cos(yawRad)) * speed;
        double dz = (forward * Math.cos(yawRad) + strafe * Math.sin(yawRad)) * speed;
        double dy = 0.0;

        if (client.options.jumpKey.isPressed()) dy += speed;
        if (client.options.sneakKey.isPressed()) dy -= speed;

        camera.setPosition(camera.getX() + dx, camera.getY() + dy, camera.getZ() + dz);
        camera.setYaw(yaw);
        camera.setPitch(pitch);

        player.setPosition(savedX, savedY, savedZ);
        player.setYaw(savedYaw);
        player.setPitch(savedPitch);
    }

    public static void shutdown() {
        if (camera != null && AetherClient.client().player != null) {
            disable(AetherClient.client().player);
        }
    }
}
