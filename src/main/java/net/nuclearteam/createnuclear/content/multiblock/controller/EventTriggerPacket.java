package net.nuclearteam.createnuclear.content.multiblock.controller;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.nuclearteam.createnuclear.CreateNuclear;

/**
 * Packet sent from server to client to trigger a localized event overlay.
 */
public record EventTriggerPacket(int duration) implements CustomPacketPayload {
    public static final Type<EventTriggerPacket> TYPE = new Type<>(CreateNuclear.asResource("trigger_event_text_overlay"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EventTriggerPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, EventTriggerPacket::duration,
            EventTriggerPacket::new
    );

    @Override
    public Type<EventTriggerPacket> type() {
        return TYPE;
    }
}
