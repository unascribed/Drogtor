package com.unascribed.drogtor.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;
import com.unascribed.drogtor.DrogtorPlayer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity {

	public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	@Inject(at = @At("HEAD"), method = "getPlayerListName()Lnet/minecraft/text/Text;", cancellable = true)
	public void getPlayerListName(CallbackInfoReturnable<Text> ci) {
		if (this instanceof DrogtorPlayer && (((DrogtorPlayer)this).drogtor$isActive())) {
			ci.setReturnValue(getDisplayName());
		}
	}
	
}
