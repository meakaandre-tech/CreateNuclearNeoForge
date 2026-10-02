package net.nuclearteam.createnuclear;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.nuclearteam.createnuclear.content.multiblock.bluePrintItem.ReactorBluePrintItemPacket;
import net.nuclearteam.createnuclear.content.multiblock.controller.EventTriggerPacket;

public class CNPackets {
    public static void register() {
        // to server
        PayloadTypeRegistry.serverboundPlay().register(ReactorBluePrintItemPacket.TYPE, ReactorBluePrintItemPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ReactorBluePrintItemPacket.TYPE,
                (packet, ctx) -> ctx.server().execute(() -> packet.handle(ctx.player())));

        // to client; the receiver is registered by the client entrypoint
        PayloadTypeRegistry.clientboundPlay().register(EventTriggerPacket.TYPE, EventTriggerPacket.STREAM_CODEC);
    }
}
