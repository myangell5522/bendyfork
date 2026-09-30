package io.github.kosmx.bendylib.impl.accessors;


import net.minecraft.client.model.geom.ModelPart;

/**
 * For a shader fix. see {@link io.github.kosmx.bendylib.ModelPartAccessor.Workaround}
 */
public interface CuboidSideAccessor {
    ModelPart.Polygon[] getSides();

    void setSides(ModelPart.Polygon[] sides);

    void resetSides();

    void doSideSwapping();
}
