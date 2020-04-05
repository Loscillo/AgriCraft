package com.infinityraider.agricraft.compat.wayla;

import com.infinityraider.agricraft.api.v1.misc.IAgriDisplayable;
import com.infinityraider.infinitylib.utility.WorldHelper;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class WailaCompatHandler implements IWailaDataProvider {

	@Nonnull
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		WorldHelper.getBlock(accessor.getWorld(), accessor.getPosition(), IAgriDisplayable.class).ifPresent(e -> e.addDisplayInfo(tooltip::add));
		WorldHelper.getTile(accessor.getWorld(), accessor.getPosition(), IAgriDisplayable.class).ifPresent(e -> e.addDisplayInfo(tooltip::add));

		return tooltip;
	}
}
