package me.figgnus.floatingcompanions;

import me.figgnus.floatingcompanions.client.ClientBootstrap;
import me.figgnus.floatingcompanions.command.PetCommand;
import me.figgnus.floatingcompanions.network.PetNetwork;
import me.figgnus.floatingcompanions.network.PetSyncEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(FloatingCompanionsMod.MOD_ID)
public final class FloatingCompanionsMod {
    public static final String MOD_ID = "floatingcompanions";

    public FloatingCompanionsMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(PetNetwork::registerPayloadHandlers);

        NeoForge.EVENT_BUS.addListener(PetCommand::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(PetSyncEvents::onPlayerLoggedIn);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientBootstrap.init();
        }
    }
}
