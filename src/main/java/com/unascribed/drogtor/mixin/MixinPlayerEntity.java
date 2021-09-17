package com.unascribed.drogtor.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.drogtor.DrogtorPlayer;
import com.unascribed.drogtor.ForgeHelper;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.util.NbtType;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity implements DrogtorPlayer {

	protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}
	
	private String drogtor$nickname;
	private Formatting drogtor$namecolor;
	
	@Inject(at = @At("TAIL"), method = "writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V")
	public void writeCustomDataToNbt(NbtCompound tag, CallbackInfo ci) {
		if (drogtor$nickname != null) {
			tag.putString("drogtor:nickname", drogtor$nickname);
		}
		if (drogtor$namecolor != null) {
			tag.putString("drogtor:namecolor", drogtor$namecolor.getName());
		}
	}
	
	@Inject(at = @At("TAIL"), method = "readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V")
	public void readCustomDataFromNbt(NbtCompound tag, CallbackInfo ci) {
		drogtor$nickname = tag.contains("drogtor:nickname", NbtType.STRING) ? tag.getString("drogtor:nickname") : null;
		drogtor$namecolor = tag.contains("drogtor:namecolor", NbtType.STRING) ? Formatting.byName(tag.getString("drogtor:namecolor")) : null;
	}
	
	@Inject(at = @At("HEAD"), method = "getName()Lnet/minecraft/text/Text;", cancellable = true)
	public void getName(CallbackInfoReturnable<Text> ci) {
		String nick = drogtor$getNickname();
		if (nick != null) {
			ci.setReturnValue(new LiteralText(nick));
		}
	}
	
	@Inject(at = @At("RETURN"), method = "getDisplayName()Lnet/minecraft/text/Text;")
	public void getDisplayName(CallbackInfoReturnable<Text> ci) {
		if (drogtor$getNameColor() != null) {
			MutableText mut = (MutableText)ci.getReturnValue();
			mut.setStyle(mut.getStyle().withColor(drogtor$getNameColor()));
		}
	}

	@Override
	public @Nullable String drogtor$getNickname() {
		return drogtor$nickname;
	}

	@Override
	public @Nullable Formatting drogtor$getNameColor() {
		return drogtor$namecolor;
	}
	
	@Override
	public void drogtor$setNameColor(@Nullable Formatting fmt) {
		drogtor$namecolor = fmt;
		drogtor$updatePlayerListEntries();
	}

	@Override
	public void drogtor$setNickname(@Nullable String nickname) {
		drogtor$nickname = nickname;
		drogtor$updatePlayerListEntries();
	}
	
	@Override
	public boolean drogtor$isActive() {
		return drogtor$namecolor != null || drogtor$nickname != null;
	}

	private void drogtor$updatePlayerListEntries() {
		ForgeHelper.refreshDisplayName((PlayerEntity)(Object)this);
		if (((PlayerEntity)(Object)this) instanceof ServerPlayerEntity) {
			world.getServer().getPlayerManager().sendToAll(new PlayerListS2CPacket(Action.UPDATE_DISPLAY_NAME, ((ServerPlayerEntity)(Object)this)));
		}
	}

}
