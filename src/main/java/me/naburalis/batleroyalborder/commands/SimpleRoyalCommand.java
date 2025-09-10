package me.naburalis.batleroyalborder.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import me.naburalis.batleroyalborder.Config;
import me.naburalis.batleroyalborder.util.*;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;

public class SimpleRoyalCommand {
    // Holds the currently running shrink thread, if any
    private static volatile Thread shrinkThread;
    
    // Boss bar for countdown and shrinking progress
    private static volatile ServerBossBar bossBar;

    // Current parameters (initialised from config, can be overridden via /simpleroyal set ...)
    private static volatile double shrinkAmount = Config.getDouble("simpleroyal.shrinkAmount");
    private static volatile int shrinkTime = Config.getInteger("simpleroyal.shrinkTime");
    private static volatile int delayTime = Config.getInteger("simpleroyal.delayTime");

    /**
     * Converts a string color name to BossBar.Color enum
     */
    private static BossBar.Color parseBossBarColor(String colorName) {
        switch (colorName.toUpperCase()) {
            case "PINK": return BossBar.Color.PINK;
            case "BLUE": return BossBar.Color.BLUE;
            case "GREEN": return BossBar.Color.GREEN;
            case "YELLOW": return BossBar.Color.YELLOW;
            case "PURPLE": return BossBar.Color.PURPLE;
            case "WHITE": return BossBar.Color.WHITE;
            default: return BossBar.Color.RED;
        }
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            var root = literal("simpleroyal");

            // simpleroyal set <shrink_amount> <shrink_time> <delay_time>
            root.then(literal("set").then(argument("shrink_amount", StringArgumentType.word())
                    .then(argument("shrink_time", StringArgumentType.word())
                            .then(argument("delay_time", StringArgumentType.word())
                                    .executes(ctx -> set(
                                            ctx.getSource(),
                                            StringArgumentType.getString(ctx, "shrink_amount"),
                                            StringArgumentType.getString(ctx, "shrink_time"),
                                            StringArgumentType.getString(ctx, "delay_time")
                                    ))))));

            // simpleroyal start
            root.then(literal("start").executes(ctx -> start(ctx.getSource())));

            // simpleroyal stop
            root.then(literal("stop").executes(ctx -> stop(ctx.getSource())));

            // simpleroyal get
            root.then(literal("get").executes(ctx -> get(ctx.getSource())));

            // simpleroyal reset
            root.then(literal("reset").executes(ctx -> reset(ctx.getSource())));

            // Register root and alias /sr
            var rootNode = dispatcher.register(root);
            dispatcher.register(literal("sr").redirect(rootNode));
        });
    }

    private static int start(ServerCommandSource source) {
        ServerWorld world = source.getWorld();
        MinecraftServer server = world.getServer();

        // Prevent duplicate starts
        if (shrinkThread != null && shrinkThread.isAlive()) {
            source.sendFeedback(() -> Text.literal("Border shrinking already in progress."), false);
            return Command.SINGLE_SUCCESS;
        }

        server.getPlayerManager().broadcast(Text.literal("World border shrinking started!"), false);

        // Create boss bar with color from config
        bossBar = new ServerBossBar(Text.literal("Border Shrinking"), parseBossBarColor(Config.getString("simpleroyal.bossBar.color")), BossBar.Style.PROGRESS);
        server.getPlayerManager().getPlayerList().forEach(bossBar::addPlayer);

        shrinkThread = new Thread(() -> {
            double currentSize = world.getWorldBorder().getSize();
            while (!Thread.currentThread().isInterrupted() && currentSize > shrinkAmount) {
                double targetSize = Math.max(1, currentSize - shrinkAmount);

                // Inform players of upcoming shrink and countdown
                long countdownMs = delayTime;
                if (countdownMs > 0) {
                    long seconds = countdownMs / 1000;
                    server.execute(() -> {
                        bossBar.setName(Text.literal("Border will shrink to " + (int) targetSize + " blocks"));
                        bossBar.setPercent(1.0f);
                    });

                    for (long i = seconds; i > 0 && !Thread.currentThread().isInterrupted(); i--) {
                        final long remaining = i;
                        final float progress = (float) remaining / seconds;
                        server.execute(() -> {
                            bossBar.setName(Text.literal("Shrinking in " + TimeUtils.formatSeconds(remaining)));
                            bossBar.setPercent(progress);
                        });
                        
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            return;
                        }
                    }
                }

                // Start shrinking
                server.execute(() -> {
                    bossBar.setName(Text.literal("Shrinking border to " + (int) targetSize + " blocks..."));
                    bossBar.setPercent(0.0f);
                });

                server.execute(() -> world.getWorldBorder().interpolateSize(world.getWorldBorder().getSize(), targetSize, shrinkTime));

                // Progress bar for shrinking
                long shrinkSteps = Math.max(1, shrinkTime / 100); // Update every 100ms
                for (long step = 0; step < shrinkSteps && !Thread.currentThread().isInterrupted(); step++) {
                    final float shrinkProgress = (float) step / shrinkSteps;
                    server.execute(() -> bossBar.setPercent(shrinkProgress));
                    try {
                        Thread.sleep(shrinkTime / shrinkSteps);
                    } catch (InterruptedException ignored) {
                        return;
                    }
                }
                
                // Complete shrinking progress
                server.execute(() -> bossBar.setPercent(1.0f));
                
                try {
                    Thread.sleep(100); // brief pause to show completion
                } catch (InterruptedException ignored) {
                    return; // stop if interrupted
                }

                currentSize = targetSize;
            }
            server.execute(() -> {
                server.getPlayerManager().broadcast(Text.literal("Border has reached its final size."), false);
                if (bossBar != null) {
                    bossBar.clearPlayers();
                    bossBar = null;
                }
            });
            shrinkThread = null;
        }, "BorderShrinkThread");

        shrinkThread.start();

        return Command.SINGLE_SUCCESS;
    }

    private static int set(ServerCommandSource source, String amountStr, String timeStr, String delayStr) {
        shrinkAmount = NumberUtils.parseWithSuffix(amountStr);
        shrinkTime = TimeUtils.parseTime(timeStr);
        delayTime = TimeUtils.parseTime(delayStr);
        source.sendFeedback(() -> Text.literal("Parameters set: amount=" + NumberUtils.formatWithSuffix((long) shrinkAmount) + ", time=" + TimeUtils.formatMillis(shrinkTime) + ", delay=" + TimeUtils.formatMillis(delayTime)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int stop(ServerCommandSource source) {
        MinecraftServer server = source.getServer();

        if (shrinkThread != null && shrinkThread.isAlive()) {
            shrinkThread.interrupt();
            shrinkThread = null;
            if (bossBar != null) {
                server.execute(() -> {
                    bossBar.clearPlayers();
                    bossBar = null;
                });
            }
            server.getPlayerManager().broadcast(Text.literal("World border shrinking stopped."), false);
        } else {
            source.sendFeedback(() -> Text.literal("No border shrinking is in progress."), false);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int get(ServerCommandSource source) {
        boolean running = shrinkThread != null && shrinkThread.isAlive();
        String status = running ? "running" : "stopped";
        String msg = String.format("Current params -> amount=%s, time=%s, delay=%s, status=%s", NumberUtils.formatWithSuffix((long) shrinkAmount), TimeUtils.formatMillis(shrinkTime), TimeUtils.formatMillis(delayTime), status);
        source.sendFeedback(() -> Text.literal(msg), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int reset(ServerCommandSource source) {
        shrinkAmount = Config.getDouble("simpleroyal.shrinkAmount");
        shrinkTime = Config.getInteger("simpleroyal.shrinkTime");
        delayTime = Config.getInteger("simpleroyal.delayTime");
        source.sendFeedback(() -> Text.literal("Parameters reset to defaults."), false);
        return Command.SINGLE_SUCCESS;
    }
}
