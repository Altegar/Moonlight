package net.mehvahdjukaar.selene.resources.pack;

import com.google.gson.JsonElement;
import net.mehvahdjukaar.selene.misc.BlockLootTableAccessor;
import net.mehvahdjukaar.selene.resources.RPUtils;
import net.mehvahdjukaar.selene.resources.ResType;
import net.mehvahdjukaar.selene.resources.TagBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.io.ByteArrayInputStream;
import java.util.Collection;

public class DynamicDataPack extends DynamicResourcePack {

    public DynamicDataPack(ResourceLocation name, PackType type, Pack.Position position, boolean fixed, boolean hidden) {
        super(name, PackType.SERVER_DATA, position, fixed, hidden);
    }

    public DynamicDataPack(ResourceLocation name) {
        super(name, PackType.SERVER_DATA);
    }

    //helpers. maybe remove
    @Deprecated
    public <T extends ForgeRegistryEntry<T>> void addTag(TagKey<?> key, Collection<T> values, ResourceKey<?> type) {
        var builder = TagBuilder.of(key);
        builder.addEntries(values);
        this.addTag(builder, type);
    }
    //TODO: reformat
    /*
    public <R, T extends ForgeRegistryEntry<T>> void addTag(ResourceLocation key, Collection<T> getValues, ResourceKey<Registry<R>> type) {
        var builder = new TagBuilder(key);
        builder.addAllEntries(getValues);
           this.addTag(builder,type);
    }*/

    @Deprecated
    public void addTag(ResourceLocation tagLocation, Collection<ResourceLocation> values, ResourceKey<?> type) {
        var builder = TagBuilder.of(tagLocation);
        values.forEach(builder::add);
        this.addTag(builder, type);
    }

    public void addTag(TagBuilder builder, ResourceKey<?> type) {

        ResourceLocation tagId = builder.getId();
        String tagPath = type.location().getPath();
        if (tagPath.equals("block") || tagPath.equals("entity_type") || tagPath.equals("item")) tagPath = tagPath + "s";
        ResourceLocation loc = ResType.TAGS.getPath(new ResourceLocation(tagId.getNamespace(),
                tagPath + "/" + tagId.getPath()));
        //merge tags
        if (this.resources.containsKey(loc)) {
            var r = resources.get(loc);
            var oldTag = RPUtils.deserializeJson(new ByteArrayInputStream(r));
            builder.addFromJson(oldTag);
        }
        JsonElement json = builder.serializeToJson();
        this.addJson(loc, json, ResType.GENERIC);
    }

    /**
     * Adds a simple loot table that only drops the block itself
     *
     * @param block block to be dropped
     */
    public void addSimpleBlockLootTable(Block block) {
        this.addJson(block.getLootTable(),
                LootTables.serialize(BlockLootTableAccessor.dropping(block).setParamSet(LootContextParamSets.BLOCK).build()),
                ResType.LOOT_TABLES);
    }

    public void addRecipe(FinishedRecipe recipe) {
        this.addJson(recipe.getId(), recipe.serializeRecipe(), ResType.RECIPES);
        ResourceLocation advancementId = recipe.getAdvancementId();
        if (advancementId != null) {
            this.addJson(recipe.getAdvancementId(), recipe.serializeAdvancement(), ResType.ADVANCEMENTS);
        }
    }

    public void addRecipeNoAdvancement(FinishedRecipe recipe) {
        this.addJson(recipe.getId(), recipe.serializeRecipe(), ResType.RECIPES);
    }

    @Deprecated
    public void addRecipeWithAdvancement(FinishedRecipe recipe) {
        this.addRecipe(recipe);
        ResourceLocation advancementId = recipe.getAdvancementId();
        if (advancementId != null) {
            //this.addJson(advancementId, recipe.serializeAdvancement(), ResType.ADVANCEMENTS);
        }
    }


}