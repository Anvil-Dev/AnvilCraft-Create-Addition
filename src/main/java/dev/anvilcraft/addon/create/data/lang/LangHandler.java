package dev.anvilcraft.addon.create.data.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import dev.anvilcraft.addon.create.AnvilCraftCreateAdditionConfig;
import dev.anvilcraft.lib.config.ConfigData;

public class LangHandler {

    /**
     * 语言文件初始化
     *
     * @param provider 提供器
     */
    public static void init(RegistrateLangProvider provider) {
        ConfigData.readConfigClass(provider, AnvilCraftCreateAdditionConfig.class);
    }
}
