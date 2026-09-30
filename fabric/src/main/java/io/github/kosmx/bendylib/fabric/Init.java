package io.github.kosmx.bendylib.fabric;

import io.github.kosmx.bendylib.compat.tr7zw.TDSkinCompat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Init implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("bendy-lib");

    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("skinlayers3d")
                || FabricLoader.getInstance().isModLoaded("skinlayers")) {
            try {
                TDSkinCompat.init(LOGGER);
            } catch (NoClassDefFoundError | ClassNotFoundException e) {
                LOGGER.error("Failed to initialize 3D Skin Layers compatibility", e);
            }
        }
    }
}
