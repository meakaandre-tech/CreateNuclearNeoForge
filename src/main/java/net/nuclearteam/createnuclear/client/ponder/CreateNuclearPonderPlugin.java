package net.nuclearteam.createnuclear.client.ponder;

import com.zurrtum.create.client.infrastructure.ponder.AllCreatePonderTags;
import com.zurrtum.create.client.ponder.api.registration.PonderPlugin;
import com.zurrtum.create.client.ponder.api.registration.PonderSceneRegistrationHelper;
import com.zurrtum.create.client.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;
import net.nuclearteam.createnuclear.CNBlocks;
import net.nuclearteam.createnuclear.CNItems;
import net.nuclearteam.createnuclear.CreateNuclear;

public class CreateNuclearPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return CreateNuclear.MOD_ID;
    }

    // was CNPonderIndex
    @Override
    public void registerScenes(PonderSceneRegistrationHelper<Identifier> helper) {
        PonderSceneRegistrationHelper<ItemLike> HELPER = helper.withKeyFunction(item -> BuiltInRegistries.ITEM.getKey(item.asItem()));

        // Reactor
        HELPER.forComponents(CNBlocks.REACTOR_CONTROLLER)
                .addStoryBoard("reactor/setup", CNPonderReactor::init)
                .addStoryBoard("reactor/setup", CNPonderReactor::enable);


        HELPER.forComponents(CNItems.REACTOR_BLUEPRINT)
                .addStoryBoard("reactor/setup", CNPonderReactor::enable);
    }

    // was CNCreateNuclearPonderTags; the original also re-registered Create's "kinetic sources" tag with the
    // reactor controller as icon and "Kinetic Nuclear" as title, which is left to Create here
    @Override
    public void registerTags(PonderTagRegistrationHelper<Identifier> helper) {
        PonderTagRegistrationHelper<ItemLike> itemHelper = helper.withKeyFunction(item -> BuiltInRegistries.ITEM.getKey(item.asItem()));

        itemHelper.addToTag(AllCreatePonderTags.KINETIC_SOURCES)
                .add(CNBlocks.REACTOR_CONTROLLER);
    }
}
