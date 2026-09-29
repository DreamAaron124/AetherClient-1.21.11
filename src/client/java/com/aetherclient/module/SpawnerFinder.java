package com.aetherclient.module;

import com.aetherclient.AetherClient;
import com.aetherclient.config.ClientConfig.ModuleConfig;
import com.aetherclient.render.BlockFinderRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;

public final class SpawnerFinder {
    public static void init() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            ModuleConfig c = AetherClient.CONFIG.spawner;
            if (!c.enabled || AetherClient.client().world == null || AetherClient.client().player == null) return;
            double rangeSq = (double)c.range * c.range;
            var player = AetherClient.client().player;
            for (BlockEntity be : AetherClient.client().world.getBlockEntities()) {
                var pos = be.getPos();
                if (player.squaredDistanceTo(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5) > rangeSq) continue;
                if (be.getCachedState().isOf(Blocks.SPAWNER)) BlockFinderRenderer.draw(context, pos, c);
            }
        });
    }
}
