package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityBase;
import com.InfinityRaider.AgriCraft.utility.ForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public abstract class RenderBlockBase extends TileEntitySpecialRenderer<TileEntityBase> {
    private static HashMap<Block, Integer> renderIds = new HashMap<>();
    public static final int COLOR_MULTIPLIER_STANDARD = 16777215;

    private final Block block;

    protected RenderBlockBase(Block block, boolean inventory) {
        this(block, null, inventory);
    }

    protected RenderBlockBase(Block block, TileEntityBase te, boolean inventory) {
        this.block = block;
        if(!renderIds.containsKey(block)) {
            this.registerRenderer(block, te);
        }
        if(inventory) {
            //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(block), this);
        }
    }

    private void registerRenderer(Block block, TileEntityBase te) {
        if(te!=null && this.shouldBehaveAsTESR()) {
            ClientRegistry.bindTileEntitySpecialRenderer(te.getClass(), this);
            renderIds.put(block, 2);
        }
        if(this.shouldBehaveAsISBRH()) {
            renderIds.put(block, 3);
        }
    }

    //WORLD
    //-----
    private boolean renderBlock(TessellatorV2 tessellator, IBlockAccess world, double x, double y, double z, Block block, TileEntity tile, float f, int modelId, boolean callFromTESR) {
        if (callFromTESR) {
            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);
        } else {
            tessellator.setRotation(0, 0, 0, 0);
            tessellator.addTranslation((float) x, (float) y, (float) z);
        }
        if (tile != null && tile instanceof TileEntityBase) {
            rotateMatrix((TileEntityBase) tile, tessellator, false);
        }

        tessellator.setBrightness(block.getMixedBrightnessForBlock(world, new BlockPos((int) x,  (int) y, (int) z)));
        tessellator.setColorRGBA_F(1, 1, 1, 1);

        boolean result = doWorldRender(tessellator, world, x, y, z, tile, block, f, modelId, callFromTESR);

        if (tile != null && tile instanceof TileEntityBase) {
            rotateMatrix((TileEntityBase) tile, tessellator, true);
        }
        if (callFromTESR) {
            GL11.glTranslated(-x, -y, -z);
            GL11.glPopMatrix();
        } else {
            tessellator.setRotation(0, 0, 0, 0);
            tessellator.addTranslation((float) -x, (float) -y, (float) -z);
        }

        return result;
    }

    @Override
    public final void renderTileEntityAt(TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage) {
        renderBlock(TessellatorV2.instance, te.getWorld(), x, y, z, te.getBlockType(), te, partialTicks, 0, true);
    }

    //Old ISBRH method callback
    public final boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId) {
        return renderBlock(TessellatorV2.instance, world, x, y, z, block, world.getTileEntity(new BlockPos(x, y, z)), 0, modelId, false);
    }

    protected abstract boolean doWorldRender(TessellatorV2 tessellator, IBlockAccess world, double x, double y, double z, TileEntity tile, Block block, float f, int modelId, boolean callFromTESR);


    //INVENTORY
    //---------
    /*
    @Override
    public final void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {}

    @Override
    public final boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public final boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public final boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public final void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        doInventoryRender(type, item, data);
        GL11.glPopMatrix();
    }

    protected abstract void doInventoryRender(ItemRenderType type, ItemStack item, Object... data);
    */


    //HELPER METHODS
    //--------------
    public final Block getBlock() {
        return this.block;
    }

    public abstract boolean shouldBehaveAsTESR();

    public abstract boolean shouldBehaveAsISBRH();

    public final int getRenderId() {
        return AgriCraft.proxy.getRenderId(this.getBlock());
    }

    public static int getRenderId(Block block) {
        return renderIds.containsKey(block)?renderIds.get(block):-1;
    }


    //UTILITY METHODS
    //---------------
    protected void rotateMatrix(TileEntityBase tileEntityBase, Tessellator tessellator, boolean inverse) {
        //+x = EAST
        //+z = SOUTH
        //-x = WEST
        //-z = NORTH
        if(!tileEntityBase.isRotatable()) {
            return;
        }
        float angle;
        switch(tileEntityBase.getOrientation()) {
            case SOUTH: angle = 180; break;
            case WEST: angle = 90; break;
            case NORTH: angle = 0; break;
            case EAST: angle = 270; break;
            default: return;
        }
        float dx = angle%270==0?0:-1;
        float dz = angle>90?-1:0;
        if(tessellator instanceof TessellatorV2) {
            TessellatorV2 tessellatorV2 = (TessellatorV2) tessellator;
            if(inverse) {
                tessellatorV2.addTranslation(-dx , 0, -dz);
                tessellatorV2.addRotation(-angle, 0, 1, 0);
            } else {
                tessellatorV2.addRotation(angle, 0, 1, 0);
                tessellatorV2.addTranslation(dx, 0, dz);
            }
        } else {
            if (inverse) {
                GL11.glTranslatef(-dx, 0, -dz);
                GL11.glRotatef(-angle, 0, 1, 0);
            } else {
                GL11.glRotatef(angle, 0, 1, 0);
                GL11.glTranslatef(dx, 0, dz);
            }
        }
    }

    //adds a vertex to the tessellator scaled with 1/16th of a block
    protected void addScaledVertexWithUV(TessellatorV2 tessellator, float x, float y, float z, float u, float v) {
        float unit = Constants.UNIT;
        tessellator.addVertexWithUV(x*unit, y*unit, z*unit, u*unit, v*unit);
    }

    protected void drawScaledFaceDoubleXY(TessellatorV2 tessellator, float minX, float minY, float maxX, float maxY, float z) {
        z = z*16.0F;
        float minU = 0;
        float maxU = 16;
        float minV = 0;
        float maxV = 16;
        //front
        addScaledVertexWithUV(tessellator, maxX, maxY, z, maxU, minV);
        addScaledVertexWithUV(tessellator, maxX, minY, z, maxU, maxV);
        addScaledVertexWithUV(tessellator, minX, minY, z, minU, maxV);
        addScaledVertexWithUV(tessellator, minX, maxY, z, minU, minV);
        //back
        addScaledVertexWithUV(tessellator, maxX, maxY, z, maxU, minV);
        addScaledVertexWithUV(tessellator, minX, maxY, z, minU, minV);
        addScaledVertexWithUV(tessellator, minX, minY, z, minU, maxV);
        addScaledVertexWithUV(tessellator, maxX, minY, z, maxU, maxV);
    }

    protected void drawScaledFaceDoubleXZ(TessellatorV2 tessellator, float minX, float minZ, float maxX, float maxZ, float y) {
        y = y*16.0F;
        float minU = 0;
        float maxU = 16;
        float minV = 0;
        float maxV = 16;
        //front
        addScaledVertexWithUV(tessellator, maxX, y, maxZ, maxU, maxV);
        addScaledVertexWithUV(tessellator, maxX, y, minZ, maxU, minV);
        addScaledVertexWithUV(tessellator, minX, y, minZ, minU, minV);
        addScaledVertexWithUV(tessellator, minX, y, maxZ, minU, maxV);
        //back
        addScaledVertexWithUV(tessellator, maxX, y, maxZ, maxU, maxV);
        addScaledVertexWithUV(tessellator, minX, y, maxZ, minU, maxV);
        addScaledVertexWithUV(tessellator, minX, y, minZ, minU, minV);
        addScaledVertexWithUV(tessellator, maxX, y, minZ, maxU, minV);
    }


    protected void drawScaledFaceDoubleYZ(TessellatorV2 tessellator, float minY, float minZ, float maxY, float maxZ, float x) {
        x = x*16.0F;
        float minU = 0;
        float maxU = 16;
        float minV = 0;
        float maxV = 16;
        //front
        addScaledVertexWithUV(tessellator, x, maxY, maxZ, maxU, minV);
        addScaledVertexWithUV(tessellator, x, minY, maxZ, maxU, maxV);
        addScaledVertexWithUV(tessellator, x, minY, minZ, minU, maxV);
        addScaledVertexWithUV(tessellator, x, maxY, minZ, minU, minV);
        //back
        addScaledVertexWithUV(tessellator, x, maxY, maxZ, maxU, minV);
        addScaledVertexWithUV(tessellator, x, maxY, minZ, minU, minV);
        addScaledVertexWithUV(tessellator, x, minY, minZ, minU, maxV);
        addScaledVertexWithUV(tessellator, x, minY, maxZ, maxU, maxV);
    }

    /**
     * Draws a prism.
     * <p>
     * The prism coordinates range from 0 to {@link Constants#WHOLE}.
     * </p><p>
     * The prism is relative to the bottom left corner of the block.
     * </p>
     */
    protected void drawScaledPrism(TessellatorV2 tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int colorMultiplier, ForgeDirection direction) {
        float adj[] = rotatePrism(minX, minY, minZ, maxX, maxY, maxZ, direction);
        drawScaledPrism(tessellator, adj[0], adj[1], adj[2], adj[3], adj[4], adj[5], colorMultiplier);
    }
    
    //draws a rectangular prism
    protected void drawScaledPrism(TessellatorV2 tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int colorMultiplier) {
        //bottom
        drawScaledFaceBackXZ(tessellator, minX, minZ, maxX, maxZ, minY / 16.0F, colorMultiplier);
        //top
        drawScaledFaceFrontXZ(tessellator, minX, minZ, maxX, maxZ, maxY / 16.0F, colorMultiplier);
        //back
        drawScaledFaceBackXY(tessellator, minX, minY, maxX, maxY, minZ / 16.0F, colorMultiplier);
        //front
        drawScaledFaceFrontXY(tessellator, minX, minY, maxX, maxY, maxZ / 16.0F, colorMultiplier);
        //left
        drawScaledFaceBackYZ(tessellator, minY, minZ, maxY, maxZ, minX / 16.0F, colorMultiplier);
        //right
        drawScaledFaceFrontYZ(tessellator, minY, minZ, maxY, maxZ, maxX / 16.0F, colorMultiplier);

    }

    protected void drawScaledFaceFrontXY(TessellatorV2 tessellator, float minX, float minY, float maxX, float maxY, float z, int colorMultiplier) {
        z = z*16.0F;
        float minV = 16-maxY;
        float maxV = 16-minY;
        applyColorMultiplier(tessellator, colorMultiplier, ForgeDirection.SOUTH);
        addScaledVertexWithUV(tessellator, maxX, maxY, z, maxX, minV);
        addScaledVertexWithUV(tessellator, minX, maxY, z, minX, minV);
        addScaledVertexWithUV(tessellator, minX, minY, z, minX, maxV);
        addScaledVertexWithUV(tessellator, maxX, minY, z, maxX, maxV);
    }

    protected void drawScaledFaceFrontXZ(TessellatorV2 tessellator, float minX, float minZ, float maxX, float maxZ, float y, int colorMultiplier) {
        y = y*16.0F;
        applyColorMultiplier(tessellator, colorMultiplier, ForgeDirection.UP);
        addScaledVertexWithUV(tessellator, maxX, y, maxZ, maxX, maxZ);
        addScaledVertexWithUV(tessellator, maxX, y, minZ, maxX, minZ);
        addScaledVertexWithUV(tessellator, minX, y, minZ, minX, minZ);
        addScaledVertexWithUV(tessellator, minX, y, maxZ, minX, maxZ);
    }

    protected void drawScaledFaceFrontYZ(TessellatorV2 tessellator, float minY, float minZ, float maxY, float maxZ, float x, int colorMultiplier) {
        x = x*16.0F;
        float minV = 16-maxY;
        float maxV = 16-minY;
        applyColorMultiplier(tessellator, colorMultiplier, ForgeDirection.EAST);
        addScaledVertexWithUV(tessellator, x, maxY, maxZ, maxZ, minV);
        addScaledVertexWithUV(tessellator, x, minY, maxZ, maxZ, maxV);
        addScaledVertexWithUV(tessellator, x, minY, minZ, minZ, maxV);
        addScaledVertexWithUV(tessellator, x, maxY, minZ, minZ, minV);
    }

    protected void drawScaledFaceBackXY(TessellatorV2 tessellator, float minX, float minY, float maxX, float maxY, float z, int colorMultiplier) {
        z = z*16.0F;
        float minV = 16 - maxY;
        float maxV = 16 - minY;
        applyColorMultiplier(tessellator, colorMultiplier, ForgeDirection.NORTH);
        addScaledVertexWithUV(tessellator, maxX, maxY, z, maxX, minV);
        addScaledVertexWithUV(tessellator, maxX, minY, z, maxX, maxV);
        addScaledVertexWithUV(tessellator, minX, minY, z, minX, maxV);
        addScaledVertexWithUV(tessellator, minX, maxY, z, minX, minV);
    }

    protected void drawScaledFaceBackXZ(TessellatorV2 tessellator, float minX, float minZ, float maxX, float maxZ, float y, int colorMultiplier) {
        y = y*16.0F;
        applyColorMultiplier(tessellator, colorMultiplier, ForgeDirection.DOWN);
        addScaledVertexWithUV(tessellator, maxX, y, maxZ, maxX, maxZ);
        addScaledVertexWithUV(tessellator, minX, y, maxZ, minX, maxZ);
        addScaledVertexWithUV(tessellator, minX, y, minZ, minX, minZ);
        addScaledVertexWithUV(tessellator, maxX, y, minZ, maxX, minZ);
    }

    protected void drawScaledFaceBackYZ(TessellatorV2 tessellator, float minY, float minZ, float maxY, float maxZ, float x, int colorMultiplier) {
        x = x*16.0F;
        float minV = 16 - maxY;
        float maxV = 16 - minY;
        applyColorMultiplier(tessellator, colorMultiplier, ForgeDirection.WEST);
        addScaledVertexWithUV(tessellator, x, maxY, maxZ, maxZ, minV);
        addScaledVertexWithUV(tessellator, x, maxY, minZ, minZ, minV);
        addScaledVertexWithUV(tessellator, x, minY, minZ, minZ, maxV);
        addScaledVertexWithUV(tessellator, x, minY, maxZ, maxZ, maxV);
    }

    protected void applyColorMultiplier(TessellatorV2 tessellator, int colorMultiplier, ForgeDirection side) {
        float preMultiplier = getMultiplier(transformSide(tessellator, side));
        float r = preMultiplier * ((float) (colorMultiplier >> 16 & 255) / 255.0F);
        float g = preMultiplier * ((float) (colorMultiplier >> 8 & 255) / 255.0F);
        float b = preMultiplier * ((float) (colorMultiplier & 255) / 255.0F);
        tessellator.setColorOpaque_F(r, g, b);
    }

    protected ForgeDirection transformSide(TessellatorV2 tessellator, ForgeDirection dir) {
        if(dir==ForgeDirection.UNKNOWN) {
            return dir;
        }
        double[] coords = tessellator.getTransformationMatrix().transform(dir.offsetX, dir.offsetY, dir.offsetZ);
        double[] translation = tessellator.getTransformationMatrix().getTranslation();
        coords[0] = coords[0] - translation[0];
        coords[1] = coords[1] - translation[1];
        coords[2] = coords[2] - translation[2];
        double x = Math.abs(coords[0]);
        double y = Math.abs(coords[1]);
        double z = Math.abs(coords[2]);
        if(x > z) {
            if(x > y) {
                return coords[0] > 0 ? ForgeDirection.EAST : ForgeDirection.WEST;
            }
        } else {
            if(z > y) {
                return coords[2] > 0 ? ForgeDirection.SOUTH : ForgeDirection.NORTH;
            }
        }
        return coords[1] > 0 ? ForgeDirection.UP : ForgeDirection.DOWN;
    }

    protected float getMultiplier(ForgeDirection side) {
        switch(side) {
            case DOWN: return 0.5F;
            case NORTH:
            case SOUTH: return 0.8F;
            case EAST:
            case WEST: return 0.6F;
            default: return 1;
        }
    }
    
	protected void drawPlane(TessellatorV2 tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, ForgeDirection direction) {
		float[] rot = rotatePrism(minX, minY, minZ, maxX, maxY, maxZ, direction);
		drawPlane(tessellator, rot[0], rot[1], rot[2], rot[3], rot[4], rot[5]);
	}


	private void drawPlane(TessellatorV2 tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		addScaledVertexWithUV(tessellator, maxX, minY, maxZ, maxX, maxZ);
		addScaledVertexWithUV(tessellator, maxX, maxY, minZ, maxX, minZ);
		addScaledVertexWithUV(tessellator, minX, maxY, minZ, minX, minZ);
		addScaledVertexWithUV(tessellator, minX, minY, maxZ, minX, maxZ);
	}

    /**
     * Rotates a plane. This is impressively useful, but may not be impressively efficient.
     * Always returns a 6-element array. Defaults to the north direction (the base direction).
     * <p>
     * This is for use up to the point that a way to rotate lower down is found. (IE. OpenGL).
     * </p>
     *
     * TODO: Test up/down rotations more thoroughly.
     */
    public float[] rotatePrism(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, ForgeDirection direction) {
        float adj[] = new float[6];

        switch (direction) {
            default:
            case NORTH:
                adj[0] = minX; //-x
                adj[1] = minY; //-y
                adj[2] = minZ; //-z
                adj[3] = maxX; //+x
                adj[4] = maxY; //+y
                adj[5] = maxZ; //+z
                break;
            case EAST:
                adj[0] = Constants.WHOLE - maxZ; //-x
                adj[1] = minY; //-y
                adj[2] = minX; //-z
                adj[3] = Constants.WHOLE - minZ; //+x
                adj[4] = maxY; //+y
                adj[5] = maxX; //+z
                break;
            case SOUTH:
                adj[0] = minX; //-x
                adj[1] = minY; //-y
                adj[2] = Constants.WHOLE - maxZ; //-z
                adj[3] = maxX; //+x
                adj[4] = maxY; //+y
                adj[5] = Constants.WHOLE - minZ; //+z
                break;
            case WEST:
                adj[0] = minZ; //-x
                adj[1] = minY; //-y
                adj[2] = minX; //-z
                adj[3] = maxZ; //+x
                adj[4] = maxY; //+y
                adj[5] = maxX; //+z
                break;
            case UP:
                adj[0] = minX; //-x
                adj[1] = Constants.WHOLE - maxZ; //-y
                adj[2] = minY; //-z
                adj[3] = maxX; //+x
                adj[4] = Constants.WHOLE - minZ; //+y
                adj[5] = maxY; //+z
            case DOWN:
                adj[0] = minX; //-x
                adj[1] = minZ; //-y
                adj[2] = minY; //-z
                adj[3] = maxX; //+x
                adj[4] = maxZ; //+y
                adj[5] = maxY; //+z
        }
        return adj;
    }


    /**
     * utility method used for debugging rendering
     */
    @SuppressWarnings("unused")
    protected void drawAxisSystem(boolean startDrawing) {
        TessellatorV2 tessellator = TessellatorV2.instance;

        if(startDrawing) {
            tessellator.startDrawingQuads();
        }

        tessellator.addVertexWithUV(-0.005F, 2, 0, 1, 0);
        tessellator.addVertexWithUV(0.005F, 2, 0, 0, 0);
        tessellator.addVertexWithUV(0.005F, -1, 0, 0, 1);
        tessellator.addVertexWithUV(-0.005F, -1, 0, 1, 1);

        tessellator.addVertexWithUV(2, -0.005F, 0, 1, 0);
        tessellator.addVertexWithUV(2, 0.005F, 0, 0, 0);
        tessellator.addVertexWithUV(-1, 0.005F, 0, 0, 1);
        tessellator.addVertexWithUV(-1, -0.005F, 0, 1, 1);

        tessellator.addVertexWithUV(0, -0.005F, 2, 1, 0);
        tessellator.addVertexWithUV(0, 0.005F, 2, 0, 0);
        tessellator.addVertexWithUV(0, 0.005F, -1, 0, 1);
        tessellator.addVertexWithUV(0, -0.005F, -1, 1, 1);

        if(startDrawing) {
            tessellator.draw();
        }
    }
}
