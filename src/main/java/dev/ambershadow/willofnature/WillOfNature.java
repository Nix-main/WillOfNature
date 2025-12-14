package dev.ambershadow.willofnature;

import dev.ambershadow.willofnature.command.WONCommand;
import dev.ambershadow.willofnature.index.WONRegistrar;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class WillOfNature implements ModInitializer {

    public static final String MOD_ID = "willofnature";
    public static final String MOD_NAME = "Will Of Nature";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(WillOfNature::registerCommands);
        WONRegistrar.registerAll();

    }

    public static @NotNull ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private static final WONCommand[] COMMANDS = {};

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        Arrays.stream(COMMANDS).forEach(cmd -> cmd.register(dispatcher));
    }
}
