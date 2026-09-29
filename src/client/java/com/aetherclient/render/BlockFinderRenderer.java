package com.aetherclient.render;

import com.aetherclient.AetherClient;
import com.aetherclient.config.ClientConfig.ModuleConfig;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

/** Minecraft 1.21.11 world highlighting using the new GizmoDrawing API. */
public final class BlockFinderRenderer {
    private BlockFinderRenderer() {}

    public static void draw(WorldRenderContext context, BlockPos pos, ModuleConfig c) {
        int stroke = argb(c.outlineOpacity, c.red, c.green, c.blue);
        int fill = argb(c.fillOpacity, c.red, c.green, c.blue);

        // 1.21.11's renderer owns the gizmo collector. The call is made during a
        // world-render event, so the gizmo is collected for the current frame.
        try (var ignored = AetherClient.client().worldRenderer.startDrawingGizmos()) {
            switch (c.mode) {
                case "FULL_BOX" -> GizmoDrawing.box(pos,
                        DrawStyle.filledAndStroked(stroke, Math.max(1.0f, c.lineWidth), fill));
                case "CORNERS" -> GizmoDrawing.box(pos,
                        DrawStyle.stroked(stroke, Math.max(1.0f, c.lineWidth)), true);
                default -> GizmoDrawing.box(pos,
                        DrawStyle.stroked(stroke, Math.max(1.0f, c.lineWidth)));
            }
        }
    }

    private static int argb(float opacity, int red, int green, int blue) {
        int alpha = Math.max(0, Math.min(255, Math.round(opacity * 255.0f)));
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
