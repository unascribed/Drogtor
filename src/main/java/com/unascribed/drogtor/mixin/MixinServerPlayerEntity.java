package com.unascribed.drogtor.mixin;

import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.scoreboard.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

	public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile profile, PlayerPublicKey publicKey) {
		super(world, pos, yaw, profile, publicKey);
	}

	@Inject(at = @At("HEAD"), method = "getPlayerListName()Lnet/minecraft/text/Text;", cancellable = true)
	public void getPlayerListName(CallbackInfoReturnable<Text> ci) {
		if (this instanceof DrogtorPlayer us && us.drogtor$isActive()) {
			ci.setReturnValue(Team.decorateName(this.getScoreboardTeam(), getDisplayName()));
		}
	}

	@Inject(at = @At("HEAD"), method = "copyFrom(Lnet/minecraft/server/network/ServerPlayerEntity;Z)V")
	public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
		if (this instanceof DrogtorPlayer us && oldPlayer instanceof DrogtorPlayer them) {
			us.drogtor$setNickname(them.drogtor$getNickname());
			us.drogtor$setNameColor(them.drogtor$getNameColor());
			us.drogtor$setBio(them.drogtor$getBio());
		}
	}
}
