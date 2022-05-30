package net.mehvahdjukaar.selene.items;

import net.mehvahdjukaar.selene.block_set.wood.WoodType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class WoodBasedBlockItem extends BlockItem {
    private final int burnTime;
    private final WoodType woodType;

    public WoodBasedBlockItem(Block blockIn, Properties builder, WoodType woodType) {
        this(blockIn, builder, woodType, 300);
    }

    public WoodBasedBlockItem(Block blockIn, Properties builder, int burnTicks) {
        this(blockIn, builder, WoodType.OAK_WOOD_TYPE, burnTicks);
    }

    public WoodBasedBlockItem(Block blockIn, Properties builder, WoodType woodType, int burnTicks) {
        super(blockIn, builder);
        this.woodType = woodType;
        this.burnTime = woodType.canBurn() ? burnTicks : 0;
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        return this.burnTime;
    }

    @Override
    protected boolean allowdedIn(CreativeModeTab pCategory) {
        if (woodType.planks.asItem().getItemCategory() == null) return false;
        return super.allowdedIn(pCategory);
    }
}
