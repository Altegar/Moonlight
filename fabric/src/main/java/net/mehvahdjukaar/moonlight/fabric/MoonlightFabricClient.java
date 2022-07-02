package net.mehvahdjukaar.moonlight.fabric;

import net.mehvahdjukaar.moonlight.Moonlight;
import net.mehvahdjukaar.moonlight.MoonlightClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.function.Function;

public class MoonlightFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MoonlightClient.initClient();
        MoonlightClient.onRegisterEntityRenderTypes(EntityRendererRegistry::register);
        MoonlightClient.onRegisterBlockColors(ColorProviderRegistry.BLOCK::register);
        MoonlightClient.onRegisterItemColors(ColorProviderRegistry.ITEM::register);
        MoonlightClient.onRegisterParticles(MoonlightFabricClient::registerParticle);

        ClientSpriteRegistryCallback.event(InventoryMenu.BLOCK_ATLAS).register(((atlasTexture, registry) -> {
            registry.register(Moonlight.res( "particle/ember_0"));
            registry.register(Moonlight.res( "particle/soot_0"));
            registry.register(Moonlight.res( "particle/ember_1"));
            registry.register(Moonlight.res( "particle/soot_1"));
            registry.register(Moonlight.res( "particle/emberspark_0"));
            registry.register(Moonlight.res( "particle/emberspark_1"));
            registry.register(Moonlight.res( "particle/moss_0"));
            registry.register(Moonlight.res( "particle/moss_1"));

            registry.register(Moonlight.res( "particle/oak_leaf_0"));
            registry.register(Moonlight.res( "particle/birch_leaf_0"));
            registry.register(Moonlight.res( "particle/spruce_leaf_0"));
            registry.register(Moonlight.res( "particle/jungle_leaf_0"));
            registry.register(Moonlight.res( "particle/acacia_leaf_0"));
            registry.register(Moonlight.res( "particle/dark_oak_leaf_0"));
            registry.register(Moonlight.res( "particle/azalea_leaf_0"));
            registry.register(Moonlight.res( "particle/azalea_flower_0"));
            registry.register(Moonlight.res( "particle/oak_leaf_1"));
            registry.register(Moonlight.res( "particle/birch_leaf_1"));
            registry.register(Moonlight.res( "particle/spruce_leaf_1"));
            registry.register(Moonlight.res( "particle/jungle_leaf_1"));
            registry.register(Moonlight.res( "particle/acacia_leaf_1"));
            registry.register(Moonlight.res( "particle/dark_oak_leaf_1"));
            registry.register(Moonlight.res( "particle/azalea_leaf_1"));
            registry.register(Moonlight.res( "particle/azalea_flower_1"));


            registry.register(Moonlight.res( "particle/oak_bark_0"));
            registry.register(Moonlight.res( "particle/birch_bark_0"));
            registry.register(Moonlight.res( "particle/spruce_bark_0"));
            registry.register(Moonlight.res( "particle/jungle_bark_0"));
            registry.register(Moonlight.res( "particle/acacia_bark_0"));
            registry.register(Moonlight.res( "particle/dark_oak_bark_0"));
            registry.register(Moonlight.res( "particle/nether_scale_0"));
            registry.register(Moonlight.res( "particle/oak_bark_1"));
            registry.register(Moonlight.res( "particle/birch_bark_1"));
            registry.register(Moonlight.res( "particle/spruce_bark_1"));
            registry.register(Moonlight.res( "particle/jungle_bark_1"));
            registry.register(Moonlight.res( "particle/acacia_bark_1"));
            registry.register(Moonlight.res( "particle/dark_oak_bark_1"));
            registry.register(Moonlight.res( "particle/nether_scale_1"));
            registry.register(Moonlight.res( "particle/oak_bark_2"));
            registry.register(Moonlight.res( "particle/birch_bark_2"));
            registry.register(Moonlight.res( "particle/spruce_bark_2"));
            registry.register(Moonlight.res( "particle/jungle_bark_2"));
            registry.register(Moonlight.res( "particle/acacia_bark_2"));
            registry.register(Moonlight.res( "particle/dark_oak_bark_2"));
            registry.register(Moonlight.res( "particle/nether_scale_2"));


            //flower crowns
            registry.register(Moonlight.res( "particle/bee_0"));
            registry.register(Moonlight.res( "particle/bee_1"));
        }));


    }

    private static <T extends ParticleOptions> void registerParticle(ParticleType<T> type, Function<SpriteSet,
            ParticleProvider<T>> registration) {
        ParticleFactoryRegistry.getInstance().register(type,registration::apply);
    }
}
