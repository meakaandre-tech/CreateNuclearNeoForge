package net.nuclearteam.createnuclear;

import com.zurrtum.create.AllRecipeSets;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.nuclearteam.createnuclear.content.kinetics.fan.processing.EnrichedRecipe;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "unchecked"})
public enum CNRecipeTypes implements StringRepresentable {
        ENRICHED(EnrichedRecipe.SERIALIZER)
    ;

    public static final Predicate<RecipeHolder<?>> CAN_BE_AUTOMATED = r -> !r.id().identifier()
        .getPath()
        .endsWith("_manual_only");

    private static final TagKey<RecipeSerializer<?>> AUTOMATION_IGNORE_TAG = TagKey.create(
        Registries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath("create", "automation_ignore"));

    /** The inputs of the enriching recipes, synced to clients (fan processing checks them on both sides). */
    public static final ResourceKey<RecipePropertySet> ENRICHED_INPUTS =
        ResourceKey.create(RecipePropertySet.TYPE_KEY, CreateNuclear.asResource("enriched"));

    public final Identifier id;
    private final RecipeSerializer<?> serializerObject;
    private final RecipeType<?> type;

    CNRecipeTypes(RecipeSerializer<?> serializer) {
        String name = name().toLowerCase(Locale.ROOT);
        id = CreateNuclear.asResource(name);
        serializerObject = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id, serializer);
        type = Registry.register(BuiltInRegistries.RECIPE_TYPE, id, new RecipeType<Recipe<?>>() {
            @Override
            public String toString() {
                return id.toString();
            }
        });
    }

    public static void register() {
        AllRecipeSets.ALL.put(ENRICHED_INPUTS,
            recipe -> recipe instanceof EnrichedRecipe r ? Optional.of(r.ingredient()) : Optional.empty());
    }

    public Identifier getId() {
        return id;
    }

    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializerObject;
    }

    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) type;
    }

    public <I extends RecipeInput, R extends Recipe<I>> Optional<RecipeHolder<R>> find(I inv, Level world) {
        if (!(world instanceof ServerLevel serverLevel))
            return Optional.empty();
        return serverLevel.recipeAccess()
                .getRecipeFor(getType(), inv, world);
    }

    public static boolean shouldIgnoreInAutomation(RecipeHolder<?> recipe) {
        RecipeSerializer<?> serializer = recipe.value().getSerializer();
        if (serializer != null && BuiltInRegistries.RECIPE_SERIALIZER.wrapAsHolder(serializer).is(AUTOMATION_IGNORE_TAG))
            return true;
        return !CAN_BE_AUTOMATED.test(recipe);
    }

    @Override
    public String getSerializedName() {
        return id.toString();
    }
}
