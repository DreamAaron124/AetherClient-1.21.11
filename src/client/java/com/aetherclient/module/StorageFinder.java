package com.aetherclient.module;

import com.aetherclient.AetherClient;
import com.aetherclient.config.ClientConfig.ModuleConfig;
import com.aetherclient.render.BlockFinderRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;

public final class StorageFinder {
    public static void init() {
        WorldRenderEvents.END_EXTRACTION.register(context -> {
            ModuleConfig c = AetherClient.CONFIG.storage;
            if (!c.enabled || AetherClient.client().world == null || AetherClient.client().player == null) return;
            double rangeSq = (double)c.range * c.range;
            var player = AetherClient.client().player;
            for (BlockEntity be : AetherClient.client().world.getBlockEntities()) {
                var pos = be.getPos();
                if (player.squaredDistanceTo(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5) > rangeSq) continue;
                var b = be.getCachedState().getBlock();
                if (b == Blocks.CHEST || b == Blocks.TRAPPED_CHEST || b == Blocks.BARREL || b == Blocks.SHULKER_BOX ||
                    b == Blocks.HOPPER || b == Blocks.DROPPER || b == Blocks.DISPENSER) BlockFinderRenderer.draw(pos, c);
            }
        });
    }
}
