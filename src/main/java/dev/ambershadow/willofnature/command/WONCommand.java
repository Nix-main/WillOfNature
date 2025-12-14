package dev.ambershadow.willofnature.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public interface WONCommand {
    void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher);
}
