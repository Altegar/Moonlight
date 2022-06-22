package net.mehvahdjukaar.selene.fluids;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.mehvahdjukaar.selene.Moonlight;
import net.mehvahdjukaar.selene.network.ClientBoundSyncFluidsPacket;
import net.mehvahdjukaar.selene.util.DispenserHelper;
import net.mehvahdjukaar.selene.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.*;

@Deprecated
public class SoftFluidRegistryOld extends SimpleJsonResourceReloadListener {

    public static final SoftFluidRegistryOld INSTANCE = new SoftFluidRegistryOld();
    private static final SoftFluid EMPTY =SoftFluidRegistry.EMPTY ;

    // id -> SoftFluid
    private final HashMap<ResourceLocation, SoftFluid> idMap = new HashMap<>();
    // filled item -> SoftFluid. need to handle potions separately since they map to same item id
    private final HashMap<Item, SoftFluid> itemMap = new HashMap<>();
    // forge fluid  -> SoftFluid
    private final HashMap<Fluid, SoftFluid> fluidMap = new HashMap<>();
    //for stuff that is registers using a code built fluid
    private boolean initializedDispenser = false;
    int currentReload = 0;

    private SoftFluidRegistryOld() {
        super(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create(), "soft_fluids");
    }

    public static Collection<SoftFluid> getValues() {
        return Collections.unmodifiableCollection(INSTANCE.idMap.values());
    }

    public static SoftFluid get(String id) {
        return INSTANCE.idMap.getOrDefault(new ResourceLocation(id), EMPTY);
    }

    /**
     * gets a soft fluid provided his registry id
     *
     * @param id fluid registry id
     * @return soft fluid. empty fluid if not found
     */
    public static SoftFluid get(ResourceLocation id) {
        return INSTANCE.idMap.getOrDefault(id, EMPTY);
    }

    public static Optional<SoftFluid> getOptional(ResourceLocation id) {
        return Optional.ofNullable(INSTANCE.idMap.getOrDefault(id, null));
    }

    /**
     * gets a soft fluid provided a forge fluid
     *
     * @param fluid equivalent forge fluid
     * @return soft fluid. empty fluid if not found
     */
    public static SoftFluid fromForgeFluid(Fluid fluid) {
        return INSTANCE.fluidMap.getOrDefault(fluid, EMPTY);
    }

    /**
     * gets a soft fluid provided a bottle like item
     *
     * @param filledContainerItem item containing provided fluid
     * @return soft fluid. empty fluid if not found
     */
    @Nonnull
    public static SoftFluid fromItem(Item filledContainerItem) {
        return INSTANCE.itemMap.getOrDefault(filledContainerItem, EMPTY);
    }

    //vanilla built-in fluid references

    public static final FluidReference WATER = FluidReference.of("minecraft:water");
    public static final FluidReference LAVA = FluidReference.of("minecraft:lava");
    public static final FluidReference HONEY = FluidReference.of("minecraft:honey");
    public static final FluidReference MILK = FluidReference.of("minecraft:milk");
    public static final FluidReference MUSHROOM_STEW = FluidReference.of("minecraft:mushroom_stew");
    public static final FluidReference BEETROOT_SOUP = FluidReference.of("minecraft:beetroot_stew");
    public static final FluidReference RABBIT_STEW = FluidReference.of("minecraft:rabbit_stew");
    public static final FluidReference SUS_STEW = FluidReference.of("minecraft:suspicious_stew");
    public static final FluidReference POTION = FluidReference.of("minecraft:potion");
    public static final FluidReference DRAGON_BREATH = FluidReference.of("minecraft:dragon_breath");
    public static final FluidReference XP = FluidReference.of("minecraft:experience");
    public static final FluidReference SLIME = FluidReference.of("minecraft:slime");
    public static final FluidReference GHAST_TEAR = FluidReference.of("minecraft:ghast_tear");
    public static final FluidReference MAGMA_CREAM = FluidReference.of("minecraft:magma_cream");
    public static final FluidReference POWDERED_SNOW = FluidReference.of("minecraft:powder_snow");


    private static void register(SoftFluid s) {
        if (ModList.get().isLoaded(s.getRegistryName().getNamespace())) {
            for (Fluid f : s.getEquivalentFluids()) {
                //remove non-custom equivalent forge fluids in favor of this one
                if (INSTANCE.fluidMap.containsKey(f)) {
                    SoftFluid old = INSTANCE.fluidMap.get(f);
                    if (!old.isGenerated) {
                        INSTANCE.idMap.remove(old.getRegistryName());
                        old.getFilledContainer(Items.BUCKET).ifPresent(INSTANCE.itemMap::remove);
                    }
                }
            }
            registerUnchecked(s);
        }
    }

    private static void registerUnchecked(SoftFluid... fluids) {
        Arrays.stream(fluids).forEach(s -> {
            s.getEquivalentFluids().forEach(f -> INSTANCE.fluidMap.put(f, s));
            s.getContainerList().getPossibleFilled().forEach(i -> {
                //dont associate water to potion bottle
                if (i != Items.POTION || !s.getRegistryName().toString().equals("minecraft:water")) {
                    INSTANCE.itemMap.put(i, s);
                }
            });
            ResourceLocation key = s.getRegistryName();
            if (INSTANCE.idMap.containsKey(key)) {
                INSTANCE.idMap.put(key, SoftFluid.merge(INSTANCE.idMap.get(key), s));
            } else {
                INSTANCE.idMap.put(key, s);
            }
        });
    }

    private static void convertAndRegisterAllForgeFluids() {
        for (Fluid f : ForgeRegistries.FLUIDS) {
            try {
                if (f == null) continue;
                if (f instanceof FlowingFluid flowingFluid && flowingFluid.getSource() != f) continue;
                if (f instanceof ForgeFlowingFluid.Flowing || f == Fluids.EMPTY) continue;
                //if fluid map contains fluid it means that another equivalent fluid has already been registered
                if (INSTANCE.fluidMap.containsKey(f)) continue;
                //is not equivalent: merge new SoftFluid from forge fluid
                if(Utils.getID(f) != null) registerUnchecked((new SoftFluid.Builder(f)).build());
            } catch (Exception ignored) {
            }
        }
    }

    public static void acceptClientFluids(ClientBoundSyncFluidsPacket packet) {
        INSTANCE.idMap.clear();
        INSTANCE.fluidMap.clear();
        INSTANCE.itemMap.clear();
        packet.getFluids().forEach(SoftFluidRegistryOld::register);
        INSTANCE.currentReload++;
    }


    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        this.idMap.clear();
        this.fluidMap.clear();
        this.itemMap.clear();

        for (var j : jsons.entrySet()) {
            Optional<SoftFluid> result = SoftFluid.CODEC.parse(JsonOps.INSTANCE, j.getValue())
                    .resultOrPartial(e -> Moonlight.LOGGER.error("Failed to parse soft fluid JSON object for {} : {}", j.getKey(), e));
            result.ifPresent(SoftFluidRegistryOld::register);
        }
        convertAndRegisterAllForgeFluids();
        if (!this.initializedDispenser) {
            this.initializedDispenser = true;
            getValues().forEach(DispenserHelper::registerFluidBehavior);
        }
        Moonlight.LOGGER.info("Loaded {} Soft Fluids", this.idMap.size());
        //we need to do it at the very end otherwise we might grab stuff before it gets refreshed
        this.currentReload++;
    }





}