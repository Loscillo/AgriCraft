package com.infinityraider.agricraft.compat.wayla;

import com.infinityraider.agricraft.tiles.TileEntityCrop;
import com.infinityraider.agricraft.tiles.analyzer.TileEntitySeedAnalyzer;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityChannel;
import com.infinityraider.agricraft.tiles.irrigation.TileEntitySprinkler;
import com.infinityraider.agricraft.tiles.storage.TileEntitySeedStorage;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;

@WailaPlugin
public class WaylaCompat implements IWailaPlugin {

	@Override
	public void register(IWailaRegistrar iWailaRegistrar) {
		WailaCompatHandler handler = new WailaCompatHandler();

		iWailaRegistrar.registerBodyProvider(handler, TileEntityChannel.class);
		iWailaRegistrar.registerBodyProvider(handler, TileEntityCrop.class);
		iWailaRegistrar.registerBodyProvider(handler, TileEntitySeedAnalyzer.class);
		iWailaRegistrar.registerBodyProvider(handler, TileEntitySeedStorage.class);
		iWailaRegistrar.registerBodyProvider(handler, TileEntitySprinkler.class);
	}
}
