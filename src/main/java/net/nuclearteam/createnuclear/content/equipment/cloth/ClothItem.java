package net.nuclearteam.createnuclear.content.equipment.cloth;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.nuclearteam.createnuclear.CNItems;
import net.nuclearteam.createnuclear.registry.entry.ItemEntry;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;


@SuppressWarnings("unused")
public class ClothItem extends Item {

    private final DyeColor color;

    public ClothItem(Item.Properties properties, DyeColor color) {
        super(properties);
        this.color = color;
    }

    public static class DyeItemList<T extends Item> implements Iterable<ItemEntry<T>> {

        private static final int COLOR_AMOUNT = DyeColor.values().length;

        private final ItemEntry<?>[] entry = new ItemEntry<?>[COLOR_AMOUNT];

        public DyeItemList(Function<DyeColor, ItemEntry<? extends T>> filler) {
            for (DyeColor color : DyeColor.values()) {
                entry[color.ordinal()] = filler.apply(color);
            }
        }

        @SuppressWarnings("unchecked")
        public ItemEntry<T> get(DyeColor color) {
            return (ItemEntry<T>) entry[color.ordinal()];
        }

        public boolean contains(Item block) {
            for (ItemEntry<?> entry : entry) {
                if (entry.is(block)) return true;
            }
            return false;
        }

        @SuppressWarnings("unchecked")
        public ItemEntry<T>[] toArray() {
            return (ItemEntry<T>[]) Arrays.copyOf(entry, entry.length);
        }

        @Override
        public Iterator<ItemEntry<T>> iterator() {
            return new Iterator<>() {
                private int index = 0;
                @Override
                public boolean hasNext() {
                    return index < entry.length;
                }
                @SuppressWarnings("unchecked")
                @Override
                public ItemEntry<T> next() {
                    if (!hasNext()) throw new NoSuchElementException();
                    return (ItemEntry<T>) entry[index++];
                }
            };
        }

    }

    public enum Cloths {
        WHITE_CLOTH(DyeColor.WHITE),
        YELLOW_CLOTH(DyeColor.YELLOW),
        RED_CLOTH(DyeColor.RED),
        BLUE_CLOTH(DyeColor.BLUE),
        GREEN_CLOTH(DyeColor.GREEN),
        BLACK_CLOTH(DyeColor.BLACK),
        ORANGE_CLOTH(DyeColor.ORANGE),
        PURPLE_CLOTH(DyeColor.PURPLE),
        BROWN_CLOTH(DyeColor.BROWN),
        PINK_CLOTH(DyeColor.PINK),
        CYAN_CLOTH(DyeColor.CYAN),
        LIGHT_GRAY_CLOTH(DyeColor.LIGHT_GRAY),
        GRAY_CLOTH(DyeColor.GRAY),
        LIGHT_BLUE_CLOTH(DyeColor.LIGHT_BLUE),
        LIME_CLOTH(DyeColor.LIME),
        MAGENTA_CLOTH(DyeColor.MAGENTA);

        private static final Map<DyeColor, ItemEntry<ClothItem>> clothMap = new EnumMap<>(DyeColor.class);

        static {
            for (DyeColor color : DyeColor.values()) {
                clothMap.put(color, CNItems.CLOTHS.get(color));
            }
        }

        private final DyeColor color;

        Cloths(DyeColor color) {
            this.color = color;
        }

        public ItemEntry<ClothItem> getItem() {
            return clothMap.get(this.color);
        }

        public static ItemEntry<ClothItem> getByColor(DyeColor color) {
            return clothMap.get(color);
        }

    }
}