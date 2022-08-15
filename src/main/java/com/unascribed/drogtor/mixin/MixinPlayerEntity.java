package com.unascribed.drogtor.mixin;

import com.unascribed.drogtor.DrogtorPlayer;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity implements DrogtorPlayer {

	protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}
	
	private String drogtor$nickname;
	private Formatting drogtor$namecolor;
	private String drogtor$bio;
	
	@Inject(at = @At("TAIL"), method = "writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V")
	public void writeCustomDataToNbt(NbtCompound tag, CallbackInfo ci) {
		if (drogtor$nickname != null) {
			tag.putString("drogtor:nickname", drogtor$nickname);
		}
		if (drogtor$namecolor != null) {
			tag.putString("drogtor:namecolor", drogtor$namecolor.getName());
		}
		if (drogtor$bio != null) {
			tag.putString("drogtor:bio", drogtor$bio);
		}
	}
	
	@Inject(at = @At("TAIL"), method = "readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V")
	public void readCustomDataFromNbt(NbtCompound tag, CallbackInfo ci) {
		drogtor$nickname = tag.contains("drogtor:nickname", NbtType.STRING) ? tag.getString("drogtor:nickname") : null;
		drogtor$namecolor = tag.contains("drogtor:namecolor", NbtType.STRING) ? Formatting.byName(tag.getString("drogtor:namecolor")) : null;
		drogtor$bio = tag.contains("drogtor:bio", NbtType.STRING) ? tag.getString("drogtor:bio") : null;
	}
	
	@Inject(at = @At("HEAD"), method = "getName()Lnet/minecraft/text/Text;", cancellable = true)
	public void getName(CallbackInfoReturnable<Text> ci) {
		String nick = drogtor$getNickname();
		if (nick != null) {
			ci.setReturnValue(Text.literal(nick));
		}
	}
	
	@Inject(at = @At("RETURN"), method = "getDisplayName()Lnet/minecraft/text/Text;")
	public void getDisplayName(CallbackInfoReturnable<Text> ci) {
		MutableText mut = (MutableText)ci.getReturnValue();
		if (drogtor$getNameColor() != null) {
			mut.setStyle(mut.getStyle().withColor(drogtor$getNameColor()));
		}
		if (drogtor$getBio() != null) {
			mut.setStyle(mut.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(drogtor$getBio()))));
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
	public @Nullable String drogtor$getBio() {
		return drogtor$bio;
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
	public void drogtor$setBio(@Nullable String bio) {
		drogtor$bio = bio;
	}
	
	@Override
	public boolean drogtor$isActive() {
		return drogtor$namecolor != null || drogtor$nickname != null;
	}

	private void drogtor$updatePlayerListEntries() {
		if (((PlayerEntity)(Object)this) instanceof ServerPlayerEntity) {
			world.getServer().getPlayerManager().sendToAll(new PlayerListS2CPacket(Action.UPDATE_DISPLAY_NAME, ((ServerPlayerEntity)(Object)this)));
		}
	}

}
