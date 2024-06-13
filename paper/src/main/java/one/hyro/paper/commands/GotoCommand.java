package one.hyro.paper.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import one.hyro.paper.HyrosPaper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GotoCommand implements BasicCommand {
    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (stack.getExecutor() instanceof Player player) {
            if (args.length < 1) return;
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) return;

            sendPlayerToTargetServer(player, target);

            Location targetLocation = target.getLocation();
            player.teleportAsync(targetLocation).thenAccept(success -> {
                if (success) {
                    TextComponent message = Component.text("You have been teleported to ", NamedTextColor.GOLD)
                            .append(Component.text(target.getName(), NamedTextColor.RED));

                    player.sendMessage(message);
                }
            });
        } else {
            Player player = Bukkit.getPlayer(args[0]);
            Player target = Bukkit.getPlayer(args[1]);
            if (player == null) return;
            if (target == null) return;

            sendPlayerToTargetServer(player, target);

            Location targetLocation = target.getLocation();
            player.teleportAsync(targetLocation).thenAccept(success -> {
                if (success) {
                    TextComponent message = Component.text("You have been teleported to ", NamedTextColor.GOLD)
                            .append(Component.text(target.getName(), NamedTextColor.RED))
                            .append(Component.text(" by an ", NamedTextColor.GOLD))
                            .append(Component.text("operator", NamedTextColor.RED));

                    player.sendMessage(message);
                }
            });
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers())
                playerNames.add(player.getName());
            return playerNames;
        }
        return List.of();
    }

    private void sendPlayerToTargetServer(Player player, Player target) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(target.getServer().getName());

        player.sendPluginMessage(HyrosPaper.getInstance(), "BungeeCord", out.toByteArray());
    }
}