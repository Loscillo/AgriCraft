package com.InfinityRaider.AgriCraft.farming.cropplant;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Random;

/**
 * Generic abstract implementation of CropPlantTall for two-blocks tall plants
 */
public abstract class CropPlantTallGeneric extends CropPlantTall {
    private final ItemSeeds seed;
    private final ArrayList<ItemStack> fruits;

    public CropPlantTallGeneric(ItemSeeds seed) {
        this.seed = seed;
        this.fruits = OreDictHelper.getFruitsFromOreDict(getSeed());
    }

    @Override
    public int tier() {
        return 3;
    }

    @Override
    public ItemStack getSeed() {
        return new ItemStack(seed);
    }

    @Override
    public Block getBlock() {
        return seed.getPlant(null, null).getBlock();
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        return new ArrayList<>(fruits);
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        ArrayList<ItemStack> list = fruits;
        if(list!=null && list.size()>0) {
            return list.get(rand.nextInt(list.size())).copy();
        }
        return null;
    }

    @Override
    public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
        int amount = (int) (Math.ceil((gain + 0.00) / 3));
        ArrayList<ItemStack> list = new ArrayList<>();
        while(amount>0) {
            list.add(getRandomFruit(rand));
            amount--;
        }
        return list;
    }

    @Override
    public boolean canBonemeal() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getHeight(int meta) {
        return (meta>maxMetaBottomBlock()?2:1)*Constants.UNIT*13;
    }
}
