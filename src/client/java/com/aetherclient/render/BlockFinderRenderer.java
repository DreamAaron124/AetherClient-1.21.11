package com.aetherclient.render;

import com.aetherclient.config.ClientConfig.ModuleConfig;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

public final class BlockFinderRenderer {
    private BlockFinderRenderer() {}

    public static void draw(BlockPos pos, ModuleConfig c) {
        int stroke = argb(c.outlineOpacity, c.red, c.green, c.blue);
        int fill = argb(c.fillOpacity, c.red, c.green, c.blue);
        Box box = new Box(pos);

        switch (c.mode) {
            case "FULL_BOX" -> GizmoDrawing.box(box,
                    DrawStyle.filledAndStroked(stroke, Math.max(1.0f, c.lineWidth), fill));
            case "CORNERS" -> GizmoDrawing.box(box,
                    DrawStyle.stroked(stroke, Math.max(1.0f, c.lineWidth)), true);
            default -> GizmoDrawing.box(box,
                    DrawStyle.stroked(stroke, Math.max(1.0f, c.lineWidth)));
        }
    }

    private static int argb(float opacity, int red, int green, int blue) {
        int alpha = Math.max(0, Math.min(255, Math.round(opacity * 255.0f)));
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
