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

package org.geysermc.geyser.translator.item;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.geysermc.geyser.api.block.custom.CustomBlockData;
import org.geysermc.geyser.inventory.GeyserItemStack;
import org.geysermc.geyser.registry.BlockRegistries;
import org.geysermc.geyser.registry.populator.conversion.FurnitureItemConverter;
import org.geysermc.geyser.registry.type.ItemMapping;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponentTypes;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponents;

/**
 * Gardens fork: item-side integration hooks, kept out of {@link ItemTranslator} so the touched core
 * methods stay one-line calls that rebase cleanly. All the custom-block-item resolution and the
 * dyeable component belong to the fork.
 *
 * <p>A "custom block item" is an item that should render as a custom Bedrock block rather than a flat
 * item: either a vanilla block item with a {@code CUSTOM_BLOCK_ITEM_OVERRIDES} entry, or a
 * GardensFurniture item carrying an {@code item_model} that maps to a furniture block (unless that
 * model is icon-only). When both apply the furniture block wins, matching the original inline order.
 */
public final class GardensItemHook {

    private GardensItemHook() {
    }

    /**
     * Applies any custom-block rendering for the item being translated and reports whether one was
     * applied (so the caller can skip the flat custom-item definition). Mirrors the vanilla block
     * handling: a plain block item without an override still gets its Bedrock block definition.
     */
    public static boolean applyCustomBlock(GeyserSession session, ItemMapping bedrockItem,
                                           @Nullable DataComponents customComponents, ItemData.Builder builder) {
        boolean hasCustomBlock = false;
        if (bedrockItem.isBlock()) {
            CustomBlockData customBlockData = BlockRegistries.CUSTOM_BLOCK_ITEM_OVERRIDES
                .getOrDefault(bedrockItem.getJavaItem().javaKey().asString(), null);
            if (customBlockData != null) {
                hasCustomBlock = true;
                ItemTranslator.translateCustomBlock(customBlockData, session, builder);
            } else {
                builder.blockDefinition(bedrockItem.getBedrockBlockDefinition());
            }
        }

        CustomBlockData furnitureBlock = furnitureBlock(customComponents == null ? null
            : customComponents.get(DataComponentTypes.ITEM_MODEL));
        if (furnitureBlock != null) {
            hasCustomBlock = true;
            ItemTranslator.translateCustomBlock(furnitureBlock, session, builder);
        }
        return hasCustomBlock;
    }

    /**
     * The custom-block item definition to report for a held item, or {@code null} when the item is not
     * a custom block. Kept in sync with {@link #applyCustomBlock}: if these disagree, held items desync
     * and drop block interactions.
     */
    public static @Nullable ItemDefinition customBlockItemDefinition(GeyserSession session, ItemMapping mapping,
                                                                     GeyserItemStack itemStack) {
        CustomBlockData resolved = furnitureBlock(itemStack.getComponent(DataComponentTypes.ITEM_MODEL));
        if (resolved == null) {
            resolved = BlockRegistries.CUSTOM_BLOCK_ITEM_OVERRIDES
                .getOrDefault(mapping.getJavaItem().javaKey().asString(), null);
        }
        if (resolved == null) {
            return null;
        }
        return session.getItemMappings().getCustomBlockItemDefinitions().get(resolved);
    }

    private static @Nullable CustomBlockData furnitureBlock(@Nullable Object itemModelData) {
        if (itemModelData == null) {
            return null;
        }
        String itemModel = itemModelData.toString();
        CustomBlockData customBlock = FurnitureItemConverter.ITEM_MODEL_TO_BLOCK_DATA.get(itemModel);
        if (customBlock != null && !FurnitureItemConverter.ICON_ONLY_ITEM_MODELS.contains(itemModel)) {
            return customBlock;
        }
        return null;
    }
}
