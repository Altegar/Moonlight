package net.mehvahdjukaar.moonlight.api.platform.configs.fabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.mehvahdjukaar.moonlight.api.integration.ClothConfigCompat;
import net.mehvahdjukaar.moonlight.api.misc.EventCalled;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigSpec;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class FabricConfigSpec extends ConfigSpec {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final ResourceLocation res;
    private final ConfigCategory mainEntry;
    private final File file;

    public FabricConfigSpec(ResourceLocation name, ConfigCategory mainEntry, ConfigType type, boolean synced, Runnable changeCallback) {
        super(name, FabricLoader.getInstance().getConfigDir(), type, synced, changeCallback);
        this.file = this.getFullPath().toFile();
        this.mainEntry = mainEntry;
        this.res = name;
        if (this.isSynced()) {
            ServerPlayConnectionEvents.JOIN.register(this::onPlayerLoggedIn);
        }
    }

    public ConfigCategory getMainEntry() {
        return mainEntry;
    }

    @Override
    public void register() {
        FabricConfigSpec.addTrackedSpec(this);
    }

    @Override
    public void loadFromFile() {
        JsonElement config = null;

        if (file.exists() && file.isFile()) {
            try (FileInputStream fileInputStream = new FileInputStream(file);
                 InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

                config = GSON.fromJson(bufferedReader, JsonElement.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load config", e);
            }
        }

        if (config instanceof JsonObject jo) {
            //dont call load directly so we skip the main category name
            mainEntry.getEntries().forEach(e -> e.loadFromJson(jo));
        }
    }

    public void saveConfig() {
        try (FileOutputStream stream = new FileOutputStream(this.file);
             Writer writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {

            JsonObject jo = new JsonObject();
            mainEntry.getEntries().forEach(e -> e.saveToJson(jo));

            GSON.toJson(jo, writer);
        } catch (IOException ignored) {
        }
        this.onRefresh();
    }

    public String getTitleKey() {
        return "config." + this.res.toLanguageKey();
    }


    private static final boolean hasScreen = PlatformHelper.isModLoaded("cloth_config");

    @Override
    @Environment(EnvType.CLIENT)
    public Screen makeScreen(Screen parent, ResourceLocation background) {
        if (hasScreen) {
            return ClothConfigCompat.makeScreen(parent, this, background);
        }
        return null;
    }

    @Override
    public boolean hasConfigScreen() {
        return hasScreen;
    }

    @Override
    public void loadFromBytes(InputStream stream) {
        InputStreamReader inputStreamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        JsonElement config = GSON.fromJson(bufferedReader, JsonElement.class);
        if (config instanceof JsonObject jo) {
            //don't call load directly, so we skip the main category name
            mainEntry.getEntries().forEach(e -> e.loadFromJson(jo));
        }
        this.onRefresh();
    }

    @EventCalled
    private void onPlayerLoggedIn(ServerGamePacketListenerImpl listener, PacketSender sender, MinecraftServer minecraftServer) {
        //send this configuration to connected clients
        syncConfigsToPlayer(listener.player);
    }

}
