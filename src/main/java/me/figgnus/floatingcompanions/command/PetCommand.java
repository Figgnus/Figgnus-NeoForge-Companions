package me.figgnus.floatingcompanions.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.figgnus.floatingcompanions.FloatingCompanionsMod;
import me.figgnus.floatingcompanions.pet.PetRegistry;
import me.figgnus.floatingcompanions.pet.PetType;
import me.figgnus.floatingcompanions.pet.PlayerPetService;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Optional;
import java.util.stream.Collectors;

public final class PetCommand {
    private static final DynamicCommandExceptionType INVALID_PET_ID =
            new DynamicCommandExceptionType(input -> Component.translatable("command.floatingcompanions.invalid_pet", input));
    private static final SuggestionProvider<CommandSourceStack> PET_SUGGESTIONS = (context, builder) -> {
        for (PetType petType : PetRegistry.values()) {
            builder.suggest(petType.id().getPath());
            builder.suggest(petType.id().toString());
        }
        return builder.buildFuture();
    };

    private PetCommand() {
    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("floatingpet")
                        .then(Commands.literal("set")
                                .then(Commands.argument("pet", StringArgumentType.word())
                                        .suggests(PET_SUGGESTIONS)
                                        .executes(context -> setForSelf(context, StringArgumentType.getString(context, "pet")))
                                        .then(Commands.argument("target", EntityArgument.player())
                                                .requires(source -> source.hasPermission(2))
                                                .executes(context -> setForTarget(
                                                        context,
                                                        StringArgumentType.getString(context, "pet"),
                                                        EntityArgument.getPlayer(context, "target")
                                                )))))
                        .then(Commands.literal("clear")
                                .executes(PetCommand::clearSelf)
                                .then(Commands.argument("target", EntityArgument.player())
                                        .requires(source -> source.hasPermission(2))
                                        .executes(context -> clearTarget(context, EntityArgument.getPlayer(context, "target")))))
                        .then(Commands.literal("list")
                                .executes(PetCommand::listPets))
        );
    }

    private static int setForSelf(CommandContext<CommandSourceStack> context, String rawPetId) throws CommandSyntaxException {
        return setForTarget(context, rawPetId, context.getSource().getPlayerOrException());
    }

    private static int setForTarget(CommandContext<CommandSourceStack> context, String rawPetId, ServerPlayer target) throws CommandSyntaxException {
        ResourceLocation petId = normalizePetId(rawPetId);
        Optional<PetType> maybePet = PetRegistry.get(petId);

        if (maybePet.isEmpty()) {
            context.getSource().sendFailure(Component.translatable("command.floatingcompanions.pet_not_found", rawPetId));
            return 0;
        }

        PetType petType = maybePet.get();
        boolean changed = PlayerPetService.setActivePet(target, petType.id());

        if (changed) {
            if (context.getSource().getEntity() == target) {
                context.getSource().sendSuccess(
                        () -> Component.translatable("command.floatingcompanions.set.self", petType.displayName()),
                        false
                );
            } else {
                context.getSource().sendSuccess(
                        () -> Component.translatable("command.floatingcompanions.set.target", petType.displayName(), target.getDisplayName()),
                        true
                );
            }
        } else {
            context.getSource().sendSuccess(
                    () -> Component.translatable("command.floatingcompanions.set.already_active", petType.displayName(), target.getDisplayName()),
                    false
            );
        }

        return 1;
    }

    private static int clearSelf(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return clearTarget(context, context.getSource().getPlayerOrException());
    }

    private static int clearTarget(CommandContext<CommandSourceStack> context, ServerPlayer target) {
        boolean changed = PlayerPetService.clearActivePet(target);

        if (!changed) {
            if (context.getSource().getEntity() == target) {
                context.getSource().sendFailure(Component.translatable("command.floatingcompanions.clear.none.self"));
            } else {
                context.getSource().sendFailure(
                        Component.translatable("command.floatingcompanions.clear.none.target", target.getDisplayName())
                );
            }
            return 0;
        }

        if (context.getSource().getEntity() == target) {
            context.getSource().sendSuccess(
                    () -> Component.translatable("command.floatingcompanions.clear.self"),
                    false
            );
        } else {
            context.getSource().sendSuccess(
                    () -> Component.translatable("command.floatingcompanions.clear.target", target.getDisplayName()),
                    true
            );
        }

        return 1;
    }

    private static int listPets(CommandContext<CommandSourceStack> context) {
        String petList = PetRegistry.values()
                .stream()
                .map(petType -> petType.id().toString())
                .collect(Collectors.joining(", "));

        context.getSource().sendSuccess(
                () -> Component.translatable("command.floatingcompanions.list", petList),
                false
        );

        return 1;
    }

    private static ResourceLocation normalizePetId(String rawPetId) throws CommandSyntaxException {
        String normalized = rawPetId.contains(":")
                ? rawPetId
                : FloatingCompanionsMod.MOD_ID + ":" + rawPetId;

        ResourceLocation parsed = ResourceLocation.tryParse(normalized);
        if (parsed == null) {
            throw INVALID_PET_ID.create(rawPetId);
        }

        return parsed;
    }
}
