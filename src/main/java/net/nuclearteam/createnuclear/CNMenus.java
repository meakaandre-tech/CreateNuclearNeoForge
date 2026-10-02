package net.nuclearteam.createnuclear;

import com.zurrtum.create.AllMenuTypes;
import com.zurrtum.create.api.registry.CreateRegistries;
import com.zurrtum.create.foundation.gui.menu.MenuType;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.nuclearteam.createnuclear.content.multiblock.bluePrintItem.ReactorBluePrintMenu;
import net.nuclearteam.createnuclear.content.multiblock.input.ReactorInputEntity;
import net.nuclearteam.createnuclear.content.multiblock.input.ReactorInputMenu;

// the screens are registered by the client entrypoint
public class CNMenus {
    static {
        // Create Fly sends menu types by their raw id in its own (unsynced) registry, so Create's types must be
        // registered before ours on both sides, whatever order the loader runs the two mods' entrypoints in.
        AllMenuTypes.register();
    }

    public static final MenuType<ItemStack> REACTOR_BLUEPRINT_MENU = Registry.register(CreateRegistries.MENU_TYPE,
            CreateNuclear.asResource("reactor_blueprint_menu"), ReactorBluePrintMenu::new);
    public static final MenuType<ReactorInputEntity> SLOT_ITEM_STORAGE = Registry.register(CreateRegistries.MENU_TYPE,
            CreateNuclear.asResource("slot_item_menu"), ReactorInputMenu::new);

    public static void register() {}
}
