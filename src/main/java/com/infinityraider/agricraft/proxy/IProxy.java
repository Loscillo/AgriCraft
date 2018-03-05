package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.impl.v1.PluginHandler;
import com.infinityraider.agricraft.core.CoreHandler;
import com.infinityraider.agricraft.handler.GuiHandler;
import com.infinityraider.agricraft.init.AgriOreDict;
import com.infinityraider.agricraft.utility.CustomWoodTypeRegistry;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public interface IProxy extends IProxyBase {

    @Override
    default void preInitStart(FMLPreInitializationEvent event) {
        CoreHandler.preInit(event);
        registerEventHandler(AgriCraft.instance);
        PluginHandler.preInit(event);
    }

    @Override
    default void initStart(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(AgriCraft.instance, new GuiHandler());
        PluginHandler.init();
        initCustomWoodTypes();
    }

    @Override
    default void initEnd(FMLInitializationEvent event) {
        CoreHandler.init();
    }

    @Override
    default void postInitStart(FMLPostInitializationEvent event) {
        PluginHandler.postInit();
        AgriOreDict.upgradeOreDict();
    }

    default void registerVillagerSkin(int id, String resource) {
    }

    default void initCustomWoodTypes() {
        CustomWoodTypeRegistry.init();
    }

    @Override
    default void registerCapabilities() {
    }

    @Override
    default void registerEventHandlers() {
    }

    @Override
    default void activateRequiredModules() {
    }

    @Override
    default void initConfiguration(FMLPreInitializationEvent event) {
    }

    // Since apparently translation is now client-side only.
    // This is why we can't have nice things.
    default String translateToLocal(String string) {
        return string;
    }

    default String getLocale() {
        // Whatever...
        return "en_US";
    }
}
