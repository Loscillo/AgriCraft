package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.apiimpl.PluginHandler;
import com.infinityraider.agricraft.apiimpl.StatRegistry;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.core.CoreHandler;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.handler.GuiHandler;
import com.infinityraider.agricraft.handler.PlayerInteractEventHandler;
import com.infinityraider.agricraft.init.AgriEntities;
import com.infinityraider.agricraft.init.AgriRecipes;
import com.infinityraider.agricraft.init.WorldGen;
import com.infinityraider.agricraft.utility.CustomWoodType;
import com.infinityraider.agricraft.utility.RenderLogger;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public interface IProxy extends IProxyBase {
    @Override
    default void preInitStart(FMLPreInitializationEvent event) {
        CoreHandler.preinit(event);
        MinecraftForge.EVENT_BUS.register(AgriCraft.instance);
        initConfiguration(event);
        StatRegistry.getInstance().registerAdapter(new PlantStats());
        PluginHandler.preInit(event);
    }

    @Override
    default void initStart(FMLInitializationEvent event) {
        this.registerEventHandlers();
        NetworkRegistry.INSTANCE.registerGuiHandler(AgriCraft.instance, new GuiHandler());
        AgriEntities.init();
        PluginHandler.init();
        initCustomWoodTypes();
    }
    @Override
    default void postInitStart(FMLPostInitializationEvent event) {
        CoreHandler.postInit(event);
        PluginHandler.postInit();
        AgriRecipes.init();
        GrowthRequirementHandler.init();
        WorldGen.init();
    }

    default void registerVillagerSkin(int id, String resource) {}

    default void initCustomWoodTypes() {
        CustomWoodType.init();
    }

    default void registerEventHandlers() {
        PlayerInteractEventHandler playerInteractEventHandler = new PlayerInteractEventHandler();
        MinecraftForge.EVENT_BUS.register(playerInteractEventHandler);

        if (AgriCraftConfig.debug) {
            FMLCommonHandler.instance().bus().register(new RenderLogger());
        }
    }

    default void initConfiguration(FMLPreInitializationEvent event) {}
}
