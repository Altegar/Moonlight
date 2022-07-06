package net.mehvahdjukaar.moonlight;

import net.mehvahdjukaar.moonlight.network.ModMessages;
import net.mehvahdjukaar.moonlight.platform.registry.RegHelper;
import net.mehvahdjukaar.moonlight.villager_ai.VillagerAIManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Moonlight {

    public static final String MOD_ID = "moonlight";

    public static final Logger LOGGER = LogManager.getLogger();

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    //called either on mod creation
    public static void commonInit() {
        ModMessages.registerMessages();
        VillagerAIManager.init();
    }

    //mod registration
    public static void commonRegistration() {

    }

    //mod setup
    public static void commonSetup() {
//./gradlew build publishToMavenLocal
    }



    //hanging roots item override (mixin for fabric override for forge)
    //RE add lightning strike growths
    //TODO: disabled conditional growth. add command system
    //fire mixin
    //figure out thin ice situation
    //thin ice repair and solidify when snow??
    //CHARRED stuff falling in water
}
