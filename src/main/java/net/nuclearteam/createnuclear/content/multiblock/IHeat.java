package net.nuclearteam.createnuclear.content.multiblock;

import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import net.minecraft.ChatFormatting;


public interface IHeat extends IWrenchable {
    enum HeatLevel {
        NONE(ChatFormatting.DARK_GRAY, 0x000000),
        SAFETY(ChatFormatting.GREEN, 0x68CC03),
        CAUTION(ChatFormatting.YELLOW, 0xC9CC03),
        WARNING(ChatFormatting.GOLD, 0xFF6A00),
        DANGER(ChatFormatting.RED, 0xFF6A00),
        ;

        private final ChatFormatting color;
        private final Integer intColor;
        private final int colorCode;

        HeatLevel(ChatFormatting textColor, int colorCode) {
            this.color = textColor;
            this.intColor = null;
            this.colorCode = colorCode;
        }

        HeatLevel(int intColor, int colorCode) {
            this.color = null;
            this.intColor = intColor;
            this.colorCode = colorCode;
        }

        public ChatFormatting getTextColor() {
            return color;
        }

        public int getColorCode() {
            return colorCode;
        }

        public int getHeatValue() {
            return switch (this) {
                case CAUTION -> 1;
                case WARNING -> 2;
                case DANGER -> 3;
                default -> 0;
            };
        }

        public static HeatLevel of(int heat) {
            if (heat < 0) return NONE;

            heat = Math.abs(heat);

            if (heat > 0 && heat < 500) return SAFETY;
            if (heat >= 501 && heat <= 800) return CAUTION;
            if (heat >= 801 && heat <= 1000) return WARNING;
            if (heat >= 1001) return DANGER;

            return NONE;
        }

        // the formatted goggle texts (LangBuilder is client only in Create Fly) are in client.CNTooltips
    }
}
