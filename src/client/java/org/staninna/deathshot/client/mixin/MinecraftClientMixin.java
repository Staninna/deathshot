package org.staninna.deathshot.client.mixin;
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
import org.staninna.deathshot.client.DeathshotClient;

import java.util.Objects;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow @Nullable public Screen currentScreen;
    private boolean tookDeathShot;

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null)
            return;
        if(!tookDeathShot && player.isDead()) {
            if(player.showsDeathScreen() && !(currentScreen instanceof DeathScreen))
                return;
            tookDeathShot = true;
            float x = (float) player.getX();
            float y = (float) player.getY();
            float z = (float) player.getZ();

            DeathshotClient.saveScreenShot(x, y, z);
            
            // print pos to client chat
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Objects.requireNonNull(player.getDisplayName()).copy().append(" died at ").append(String.format("(%.2f, %.2f, %.2f)", x, y, z)));
        } else if(tookDeathShot && !player.isDead()) {
            tookDeathShot = false;
        }
    }
}