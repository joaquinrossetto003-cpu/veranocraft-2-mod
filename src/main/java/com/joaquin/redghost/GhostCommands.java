package com.joaquin.redghost;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;

/**
 * Comandos del mod.
 *
 * Uso en el juego:
 *   /sun huevo_de_fantasma
 *
 * Da al jugador que ejecuta el comando 1 huevo de aparición del Red Ghost.
 * Requiere nivel de permiso 2 (igual que /give).
 */
public class GhostCommands {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        // /sun
        LiteralArgumentBuilder<CommandSource> sun = Commands.literal("sun")
                .requires(source -> source.hasPermission(2));

        // /sun huevo_de_fantasma
        sun.then(Commands.literal("huevo_de_fantasma")
                .executes(context -> {
                    CommandSource source = context.getSource();
                    ServerPlayerEntity player = source.getPlayerOrException();

                    ItemStack egg = new ItemStack(RedGhostMod.RED_GHOST_SPAWN_EGG.get(), 1);
                    boolean added = player.inventory.add(egg);

                    if (added) {
                        source.sendSuccess(
                                new StringTextComponent("§c[Red Ghost] §fSe te dio el huevo de aparición."),
                                true
                        );
                    } else {
                        // Inventario lleno: soltar al suelo
                        player.drop(egg, false);
                        source.sendSuccess(
                                new StringTextComponent("§c[Red Ghost] §fInventario lleno. El huevo se soltó al suelo."),
                                true
                        );
                    }
                    return 1;
                })
        );

        dispatcher.register(sun);
    }
}
