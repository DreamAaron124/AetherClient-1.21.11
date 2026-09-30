package com.aetherclient.render;

import com.aetherclient.config.ClientConfig.ModuleConfig;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import org.joml.Matrix4f;

public final class BlockFinderRenderer {
    private static final RenderLayer ESP_LINES = RenderLayer.of(
            "aetherclient_esp_lines",
            RenderLayer.DEFAULT_BUFFER_SIZE,
            RenderPipelines.RENDERTYPE_LINES_SNIPPET,
            RenderLayer.MultiPhaseParameters.builder().build(false)
    );

    private BlockFinderRenderer() {}

    public static void draw(WorldRenderContext context, BlockPos pos, ModuleConfig c) {
        VertexConsumer v = context.consumers().getBuffer(ESP_LINES);
        Matrix4f matrix = context.matrices().peek().positionMatrix();
        Box b = new Box(pos);

        float r = c.red / 255.0f;
        float g = c.green / 255.0f;
        float bl = c.blue / 255.0f;
        float a = Math.max(0.05f, Math.min(1.0f, c.outlineOpacity));

        drawBox(v, matrix, b, r, g, bl, a, c.lineWidth);
    }

    private static void drawBox(VertexConsumer v, Matrix4f m, Box b, float r, float g, float bl, float a, float width) {
        float x1=(float)b.minX,y1=(float)b.minY,z1=(float)b.minZ;
        float x2=(float)b.maxX,y2=(float)b.maxY,z2=(float)b.maxZ;
        line(v,m,x1,y1,z1,x2,y1,z1,r,g,bl,a,width);
        line(v,m,x2,y1,z1,x2,y1,z2,r,g,bl,a,width);
        line(v,m,x2,y1,z2,x1,y1,z2,r,g,bl,a,width);
        line(v,m,x1,y1,z2,x1,y1,z1,r,g,bl,a,width);
        line(v,m,x1,y2,z1,x2,y2,z1,r,g,bl,a,width);
        line(v,m,x2,y2,z1,x2,y2,z2,r,g,bl,a,width);
        line(v,m,x2,y2,z2,x1,y2,z2,r,g,bl,a,width);
        line(v,m,x1,y2,z2,x1,y2,z1,r,g,bl,a,width);
        line(v,m,x1,y1,z1,x1,y2,z1,r,g,bl,a,width);
        line(v,m,x2,y1,z1,x2,y2,z1,r,g,bl,a,width);
        line(v,m,x2,y1,z2,x2,y2,z2,r,g,bl,a,width);
        line(v,m,x1,y1,z2,x1,y2,z2,r,g,bl,a,width);
    }

    private static void line(VertexConsumer v, Matrix4f m,
                             float x1,float y1,float z1,float x2,float y2,float z2,
                             float r,float g,float b,float a,float width) {
        v.vertex(m,x1,y1,z1).color(r,g,b,a).normal(0,1,0);
        v.vertex(m,x2,y2,z2).color(r,g,b,a).normal(0,1,0);
    }
}
