/*
 * Copyright (c) 2019-2024 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.geyser.gardens;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.protocol.bedrock.data.definitions.BlockDefinition;
import org.geysermc.geyser.api.block.custom.component.GeometryComponent;
import org.geysermc.geyser.level.block.Blocks;
import org.geysermc.geyser.level.block.type.Block;
import org.geysermc.geyser.level.block.type.BlockState;
import org.geysermc.geyser.registry.type.BlockMappings;
import org.geysermc.geyser.registry.type.GeyserBedrockBlock;
import org.geysermc.geyser.session.GeyserSession;

import java.util.Set;

/**
 * Gardens fork: block-side integration hooks, kept out of Geyser core so the touched core files stay
 * one-line calls that rebase cleanly. Everything here is Gardens-specific logic.
 *
 * <p>The "furniture host" blocks are the vanilla blocks GardensFurniture places to carry a custom
 * Bedrock block (note blocks, leaves, waxed copper variants, etc.). For Bedrock clients these are
 * swapped for the custom block reported by {@code WorldManager#getBedrockBlockOverride} at the block's
 * live world position.
 */
public final class GardensBlockHook {

    /**
     * Vanilla blocks that may host a GardensFurniture custom block. Membership is baked into
     * {@code BlockMappings#getFurnitureBlocks()} at registry time via {@link #isFurnitureHost(Block)}.
     */
    private static final Set<Block> FURNITURE_HOSTS = Set.of(
        Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.CHERRY_LEAVES, Blocks.NOTE_BLOCK, Blocks.CAVE_VINES, Blocks.CAVE_VINES_PLANT, Blocks.TRIPWIRE, Blocks.OAK_SAPLING, Blocks.DARK_OAK_SAPLING,
        Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS,
        Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB,
        Blocks.WAXED_COPPER_DOOR, Blocks.WAXED_EXPOSED_COPPER_DOOR, Blocks.WAXED_WEATHERED_COPPER_DOOR, Blocks.WAXED_OXIDIZED_COPPER_DOOR,
        Blocks.WAXED_COPPER_TRAPDOOR, Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR, Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR, Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR,
        Blocks.BARRIER
    );

    private GardensBlockHook() {
    }

    /**
     * Whether {@code block} can host a furniture custom block, i.e. should be tracked in
     * {@code BlockMappings#getFurnitureBlocks()}.
     */
    public static boolean isFurnitureHost(Block block) {
        return FURNITURE_HOSTS.contains(block);
    }

    /**
     * The Bedrock block definition to send for {@code state} at {@code position}: the furniture custom
     * block override when one exists there, otherwise the vanilla mapping.
     */
    public static BlockDefinition orOverride(GeyserSession session, Vector3i position, BlockState state) {
        BlockDefinition override = session.getGeyser().getWorldManager()
            .getBedrockBlockOverride(session, position.getX(), position.getY(), position.getZ());
        if (override != null) {
            return override;
        }
        return session.getBlockMappings().getBedrockBlock(state);
    }

    /**
     * The Bedrock runtime id to send for {@code javaState} at {@code position}: the furniture custom
     * block override's id when this is a furniture host with an override there, otherwise the vanilla
     * runtime id.
     */
    public static int bedrockBlockId(GeyserSession session, Vector3i position, int javaState) {
        BlockMappings mappings = session.getBlockMappings();
        if (mappings.getFurnitureBlocks().contains(javaState)) {
            GeyserBedrockBlock override = session.getGeyser().getWorldManager()
                .getBedrockBlockOverride(session, position.getX(), position.getY(), position.getZ());
            if (override != null) {
                return override.getRuntimeId();
            }
        }
        return mappings.getBedrockBlockId(javaState);
    }

    /**
     * Copies the fork's optional culling geometry keys onto a {@code minecraft:geometry} NBT builder.
     */
    public static void applyCulling(NbtMapBuilder geometryBuilder, GeometryComponent geometryComponent) {
        String cullingLayer = geometryComponent.cullingLayer();
        if (cullingLayer != null) {
            geometryBuilder.putString("culling_layer", cullingLayer);
        }
        String culling = geometryComponent.culling();
        if (culling != null) {
            geometryBuilder.putString("culling", culling);
        }
        String cullingShape = geometryComponent.cullingShape();
        if (cullingShape != null) {
            geometryBuilder.putString("culling_shape", cullingShape);
        }
    }
}
