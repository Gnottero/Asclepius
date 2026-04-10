package com.gnottero.asclepius.mixin.client;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.BiConsumer;

@Mixin(ItemModelGenerators.class)
public interface ItemModelGeneratorsAccessor {
    @Accessor("itemModelOutput")
    ItemModelOutput getItemModelOutput();

    @Accessor("modelOutput")
    BiConsumer<Identifier, ModelInstance> getModelOutput();
}
