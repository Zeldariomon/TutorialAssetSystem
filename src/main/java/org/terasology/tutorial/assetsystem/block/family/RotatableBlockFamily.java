/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.tutorial.assetsystem.block.family;

import java.util.Locale;
import java.util.Map;

import org.terasology.math.Side;
import org.terasology.math.geom.Vector3i;
import org.terasology.naming.Name;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockUri;
import org.terasology.world.block.family.AbstractBlockFamily;

import com.google.common.collect.Maps;

/**
 * Block family for blocks which can rotate in all directions.
 * 
 */
public class RotatableBlockFamily extends AbstractBlockFamily {

    private Map<Side, Block> blocks = Maps.newEnumMap(Side.class);
    private Block archetype;

    /**
     * @param uri    The uri for the block group.
     * @param blockMap The set of blocks that make up the group. Front, Back, Left and Right must be provided - the rest is ignored.
     */
    public RotatableBlockFamily(BlockUri uri, Map<Side, Map<Side, Block>> blockMap, Iterable<String> categories) {
        super(uri, categories);
        for (Side topSide : Side.values()) {
            for (Side frontSide : Side.values()) {
                Block block = blockMap.get(topSide).get(frontSide);
                //some combinations may be null as sides can not switch
                if (block != null) {
                    this.blocks.put(topSide, block);
                    block.setBlockFamily(this);
                    block.setUri(new BlockUri(uri, new Name(topSide.name() + "-" + frontSide.name())));
                    if (topSide == Side.TOP && frontSide == Side.FRONT) {
                        archetype = block;
                    }
                }
            }
        }
    }

    @Override
    public Block getBlockForPlacement(WorldProvider worldProvider, BlockEntityRegistry blockEntityRegistry, Vector3i location, Side attachmentSide, Side direction) {
        return blocks.get(attachmentSide);
    }

    @Override
    public Block getArchetypeBlock() {
        return archetype;
    }

    @Override
    public Block getBlockFor(BlockUri blockUri) {
        if (getURI().equals(blockUri.getFamilyUri())) {
            try {
                Side side = Side.valueOf(blockUri.getIdentifier().toString().toUpperCase(Locale.ENGLISH));
                return blocks.get(side);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Iterable<Block> getBlocks() {
        return blocks.values();
    }
}
