package com.unascribed.drogtor.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.drogtor.DrogtorPlayer;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
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
	private String drogtor$namecard;
	private Formatting drogtor$namecolor;
	
	@Inject(at = @At("TAIL"), method = "writeCustomDataToTag(Lnet/minecraft/nbt/CompoundTag;)V")
	public void writeCustomDataToTag(CompoundTag tag, CallbackInfo ci) {
		if (drogtor$nickname != null) {
			tag.putString("drogtor:nickname", drogtor$nickname);
		}
		if (drogtor$namecard != null) {
			tag.putString("drogtor:namecard", drogtor$namecard);
		}
		if (drogtor$namecolor != null) {
			tag.putString("drogtor:namecolor", drogtor$namecolor.getName());
		}
	}
	
	@Inject(at = @At("TAIL"), method = "readCustomDataFromTag(Lnet/minecraft/nbt/CompoundTag;)V")
	public void readCustomDataFromTag(CompoundTag tag, CallbackInfo ci) {
		drogtor$nickname = tag.contains("drogtor:nickname", NbtType.STRING) ? tag.getString("drogtor:nickname") : null;
		drogtor$namecard = tag.contains("drogtor:namecard", NbtType.STRING) ? tag.getString("drogtor:namecard") : null;
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
	public @Nullable String drogtor$getNamecard() {
		return drogtor$namecard;
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
	public void drogtor$setNamecard(String namecard) {
		this.drogtor$namecard = namecard;
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

	@Override
	public boolean drogtor$isNamecardActive() {
		return drogtor$namecard != null;
	}

	private void drogtor$updatePlayerListEntries() {
		if (((PlayerEntity)(Object)this) instanceof ServerPlayerEntity) {
			world.getServer().getPlayerManager().sendToAll(new PlayerListS2CPacket(Action.UPDATE_DISPLAY_NAME, ((ServerPlayerEntity)(Object)this)));
		}
	}

}
