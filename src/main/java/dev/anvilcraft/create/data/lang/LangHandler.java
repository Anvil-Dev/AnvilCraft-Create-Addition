package dev.anvilcraft.create.data.lang;

import dev.anvilcraft.create.AncCreateAddon;
import dev.anvilcraft.create.config.AncCreateAddonServerConfig;
import dev.anvilcraft.lib.v2.config.ConfigData;
import dev.anvilcraft.lib.v2.registrum.providers.RegistrumLangProvider;

public class LangHandler {
    /**
     * 语言文件初始化
     *
     * @param provider 提供器
     */
    public static void init(RegistrumLangProvider provider) {
        ConfigData.readConfigClass(provider, AncCreateAddonServerConfig.class);
        provider.add("component_content.anvilcraft.mod_name." + AncCreateAddon.MOD_ID, AncCreateAddon.MOD_NAME);
    }
}
