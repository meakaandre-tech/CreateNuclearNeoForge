package net.nuclearteam.createnuclear;

import com.zurrtum.create.api.registry.CreateRegistries;
import com.zurrtum.create.foundation.gui.menu.MenuType;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.nuclearteam.createnuclear.content.multiblock.bluePrintItem.ReactorBluePrintMenu;
import net.nuclearteam.createnuclear.content.multiblock.input.ReactorInputEntity;
import net.nuclearteam.createnuclear.content.multiblock.input.ReactorInputMenu;

// the screens are registered by the client entrypoint
public class CNMenus {
    public static final MenuType<ItemStack> REACTOR_BLUEPRINT_MENU = Registry.register(CreateRegistries.MENU_TYPE,
            CreateNuclear.asResource("reactor_blueprint_menu"), ReactorBluePrintMenu::new);
    public static final MenuType<ReactorInputEntity> SLOT_ITEM_STORAGE = Registry.register(CreateRegistries.MENU_TYPE,
            CreateNuclear.asResource("slot_item_menu"), ReactorInputMenu::new);

    public static void register() {}
}
