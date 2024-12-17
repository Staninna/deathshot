package org.staninna.deathshot.client.mixin;

import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Unique;
import org.staninna.deathshot.client.DeathshotClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow @Nullable public Screen currentScreen;

    @Unique
    private boolean shouldTakeScreenshot;
    @Unique
    private boolean isDead;

    @Unique
    private static final boolean DELAYED_MODE = false; // false = instant screenshot, true = print coords & wait

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        if (shouldTakeScreenshot) {
            DeathshotClient.saveScreenShot();
            shouldTakeScreenshot = false;
            return;
        }

        boolean isPlayerDead = player.isDead();
        if (!isDead && isPlayerDead) {
            if (player.showsDeathScreen() && !(currentScreen instanceof DeathScreen)) return;

            if (DELAYED_MODE) {
                printCoordinates(player);
                shouldTakeScreenshot = true;
            } else {
                DeathshotClient.saveScreenShot();
            }
        }
        isDead = isPlayerDead;
    }

    @Unique
    private void printCoordinates(ClientPlayerEntity player) {
        float x = (float) Math.round(player.getX() * 100) / 100;
        float y = (float) Math.round(player.getY() * 100) / 100;
        float z = (float) Math.round(player.getZ() * 100) / 100;

        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(
                Text.literal("Current coordinates: ")
                        .append(Text.literal(x + " " + y + " " + z))
        );
    }
}