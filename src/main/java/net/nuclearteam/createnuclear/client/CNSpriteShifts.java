package net.nuclearteam.createnuclear.client;

import com.zurrtum.create.client.foundation.block.connected.AllCTTypes;
import com.zurrtum.create.client.foundation.block.connected.CTSpriteShiftEntry;
import com.zurrtum.create.client.foundation.block.connected.CTSpriteShifter;
import com.zurrtum.create.client.foundation.block.connected.CTType;
import net.minecraft.resources.Identifier;
import net.nuclearteam.createnuclear.CreateNuclear;

public class CNSpriteShifts {
    public static final CTSpriteShiftEntry REACTOR_CASING = omni("reactor/casing/reactor_casing");
    public static final CTSpriteShiftEntry REACTOR_GLASS = omni("reactor/reinforced/glass");

    // PaletteBlockPattern.CTs of the autunite stone type
    public static final CTSpriteShiftEntry AUTUNITE_PILLAR = palette(AllCTTypes.RECTANGLE, "autunite", "pillar");
    public static final CTSpriteShiftEntry AUTUNITE_CAP = palette(AllCTTypes.OMNIDIRECTIONAL, "autunite", "cap");
    public static final CTSpriteShiftEntry AUTUNITE_LAYERED = palette(AllCTTypes.HORIZONTAL_KRYPPERS, "autunite", "layered");

    private static CTSpriteShiftEntry omni(String name) {
        return getCT(AllCTTypes.OMNIDIRECTIONAL, name);
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(type, CreateNuclear.asResource("block/" + blockTextureName),
                CreateNuclear.asResource("block/" + connectedTextureName + "_connected"));
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName) {
        return getCT(type, blockTextureName, blockTextureName);
    }

    // PaletteBlockPattern.toLocation
    private static CTSpriteShiftEntry palette(CTType type, String variant, String texture) {
        Identifier location = CreateNuclear.asResource(
                String.format("block/palettes/stone_types/%s/%s", texture, variant + (texture.equals("cut") ? "_" : "_cut_") + texture));
        return CTSpriteShifter.getCT(type, location, location.withSuffix("_connected"));
    }

    public static void init() {}
}
