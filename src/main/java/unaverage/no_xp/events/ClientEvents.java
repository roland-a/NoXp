package unaverage.no_xp.events;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import unaverage.no_xp.NoXp;
import unaverage.no_xp.config.ClientConfig;
import unaverage.no_xp.repair.ItemWrapper;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.profiler.IProfiler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.resource.IResourceType;
import net.minecraft.resources.IResourceManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.client.renderer.model.BakedQuad;
import java.util.List;

import net.minecraft.client.renderer.model.ItemCameraTransforms;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.Minecraft;
import unaverage.no_xp.util.Field;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = NoXp.MOD_ID, value = Dist.CLIENT)
public final class ClientEvents {
    private static final Field<IngameGui, ItemRenderer> MC_GUI_IR = new Field<>(
        IngameGui.class,
        "field_73841_b"
    );

    private static final Field<Screen, ItemRenderer> SCREEN_IR = new Field<>(
        Screen.class,
        "field_230707_j_"
    );

    @SubscribeEvent
    public static void hackGui(GuiScreenEvent.InitGuiEvent ev) {
        if (!ClientConfig.overrideDurabilityBar) return;

        ItemRenderer hack = new ItemRendererHack(ev.getGui());

        MC_GUI_IR.set(Minecraft.getInstance().gui, hack);
        SCREEN_IR.set(ev.getGui(), hack);
    }

    @SubscribeEvent
    public static void disableXpBar(RenderGameOverlayEvent e){
        if (e.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;

        e.setCanceled(true);
    }

    private static final class ItemRendererHack extends ItemRenderer{
        private static final Field<ItemRenderer, TextureManager> TEXTURE_MANAGER = new Field<>(
            ItemRenderer.class,
            "field_175057_n"
        );

        private static final Field<ItemRenderer, ItemColors> ITEM_COLORS = new Field<>(
            ItemRenderer.class,
            "field_184395_f"
        );

        private static final Field<ItemModelMesher, ModelManager> MODEL_MANAGER = new Field<>(
            ItemModelMesher.class,
            "field_178090_d"
        );

        private final ItemRenderer delegate;

        public ItemRendererHack(Screen screen) {
            this(SCREEN_IR.get(screen));
        }

        private ItemRendererHack(ItemRenderer delegate_){
            super(
                TEXTURE_MANAGER.get(delegate_),
                MODEL_MANAGER.get(delegate_.getItemModelShaper()),
                ITEM_COLORS.get(delegate_)
            );

            delegate = delegate_;
        }

        //copy and past of minecraft code with few edits
        public void renderGuiItemDecorations(FontRenderer p_180453_1_, ItemStack p_180453_2_, int p_180453_3_, int p_180453_4_, @Nullable String p_180453_5_) {
            if (!p_180453_2_.isEmpty()) {
                MatrixStack matrixstack = new MatrixStack();
                if (p_180453_2_.getCount() != 1 || p_180453_5_ != null) {
                    String s = p_180453_5_ == null ? String.valueOf(p_180453_2_.getCount()) : p_180453_5_;
                    matrixstack.translate(0.0D, 0.0D, (double)(this.blitOffset + 200.0F));
                    IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
                    p_180453_1_.drawInBatch(s, (float)(p_180453_3_ + 19 - 2 - p_180453_1_.width(s)), (float)(p_180453_4_ + 6 + 3), 16777215, true, matrixstack.last().pose(), irendertypebuffer$impl, false, 0, 15728880);
                    irendertypebuffer$impl.endBatch();
                }

                if (showDurabilityBar(p_180453_2_)) {
                    RenderSystem.disableDepthTest();
                    RenderSystem.disableTexture();
                    RenderSystem.disableAlphaTest();
                    RenderSystem.disableBlend();
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder bufferbuilder = tessellator.getBuilder();
                    double health = getDurabilityForDisplay(p_180453_2_);
                    int i = Math.round(13.0F - (float)health * 13.0F);
                    int j = getRGBDurabilityForDisplay(p_180453_2_);
                    this.fillRect(bufferbuilder, p_180453_3_ + 2, p_180453_4_ + 13, 13, 2, 0, 0, 0, 255);
                    this.fillRect(bufferbuilder, p_180453_3_ + 2, p_180453_4_ + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
                    RenderSystem.enableBlend();
                    RenderSystem.enableAlphaTest();
                    RenderSystem.enableTexture();
                    RenderSystem.enableDepthTest();
                }

                ClientPlayerEntity clientplayerentity = Minecraft.getInstance().player;
                float f3 = clientplayerentity == null ? 0.0F : clientplayerentity.getCooldowns().getCooldownPercent(p_180453_2_.getItem(), Minecraft.getInstance().getFrameTime());
                if (f3 > 0.0F) {
                    RenderSystem.disableDepthTest();
                    RenderSystem.disableTexture();
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    Tessellator tessellator1 = Tessellator.getInstance();
                    BufferBuilder bufferbuilder1 = tessellator1.getBuilder();
                    this.fillRect(bufferbuilder1, p_180453_3_, p_180453_4_ + MathHelper.floor(16.0F * (1.0F - f3)), 16, MathHelper.ceil(16.0F * f3), 255, 255, 255, 127);
                    RenderSystem.enableTexture();
                    RenderSystem.enableDepthTest();
                }
            }

        }

        private int getRGBDurabilityForDisplay(ItemStack stack) {
            ItemWrapper tool = new ItemWrapper(stack);

            if (!tool.canDecay()) {
                return stack.getItem().getRGBDurabilityForDisplay(stack);
            }
            return MathHelper.hsvToRgb(Math.max(0.0f, (float)(1.0 - this.getDurabilityForDisplay(stack))) / 3.0f, 1.0f, 1.0f);
        }

        private boolean showDurabilityBar(ItemStack stack) {
            ItemWrapper tool = new ItemWrapper(stack);

            if (!tool.canDecay()) {
                return stack.getItem().showDurabilityBar(stack);
            }
            return !tool.isFullyRepaired();
        }

        private double getDurabilityForDisplay(ItemStack stack) {
            ItemWrapper tool = new ItemWrapper(stack);

            if (!tool.canDecay()) {
                return stack.getItem().getDurabilityForDisplay(stack);
            }
            return 1.0 - tool.durabilityPercent();
        }

        private void fillRect(BufferBuilder p_181565_1_, int p_181565_2_, int p_181565_3_, int p_181565_4_, int p_181565_5_, int p_181565_6_, int p_181565_7_, int p_181565_8_, int p_181565_9_) {
            p_181565_1_.begin(7, DefaultVertexFormats.POSITION_COLOR);
            p_181565_1_.vertex((double)(p_181565_2_ + 0), (double)(p_181565_3_ + 0), 0.0D).color(p_181565_6_, p_181565_7_, p_181565_8_, p_181565_9_).endVertex();
            p_181565_1_.vertex((double)(p_181565_2_ + 0), (double)(p_181565_3_ + p_181565_5_), 0.0D).color(p_181565_6_, p_181565_7_, p_181565_8_, p_181565_9_).endVertex();
            p_181565_1_.vertex((double)(p_181565_2_ + p_181565_4_), (double)(p_181565_3_ + p_181565_5_), 0.0D).color(p_181565_6_, p_181565_7_, p_181565_8_, p_181565_9_).endVertex();
            p_181565_1_.vertex((double)(p_181565_2_ + p_181565_4_), (double)(p_181565_3_ + 0), 0.0D).color(p_181565_6_, p_181565_7_, p_181565_8_, p_181565_9_).endVertex();
            Tessellator.getInstance().end();
        }

        //region delegate

        /*
        @Override
        public void renderGuiItemDecorations(FontRenderer p_175030_1_, ItemStack p_175030_2_, int p_175030_3_, int p_175030_4_) {
            delegate.renderGuiItemDecorations(p_175030_1_, p_175030_2_, p_175030_3_, p_175030_4_, (String)null);
        }
         */

        @Override
        public ItemModelMesher getItemModelShaper() {
            return delegate.getItemModelShaper();
        }

        @Override
        public void renderModelLists(IBakedModel p_229114_1_, ItemStack p_229114_2_, int p_229114_3_, int p_229114_4_, MatrixStack p_229114_5_, IVertexBuilder p_229114_6_) {
            delegate.renderModelLists(p_229114_1_, p_229114_2_, p_229114_3_, p_229114_4_, p_229114_5_, p_229114_6_);
        }

        @Override
        public void render(ItemStack p_229111_1_, ItemCameraTransforms.TransformType p_229111_2_, boolean p_229111_3_, MatrixStack p_229111_4_, IRenderTypeBuffer p_229111_5_, int p_229111_6_, int p_229111_7_, IBakedModel p_229111_8_) {
            delegate.render(p_229111_1_, p_229111_2_, p_229111_3_, p_229111_4_, p_229111_5_, p_229111_6_, p_229111_7_, p_229111_8_);
        }

        @Override
        public void renderQuadList(MatrixStack p_229112_1_, IVertexBuilder p_229112_2_, List<BakedQuad> p_229112_3_, ItemStack p_229112_4_, int p_229112_5_, int p_229112_6_) {
            delegate.renderQuadList(p_229112_1_, p_229112_2_, p_229112_3_, p_229112_4_, p_229112_5_, p_229112_6_);
        }

        @Override
        public IBakedModel getModel(ItemStack p_184393_1_, @Nullable World p_184393_2_, @Nullable LivingEntity p_184393_3_) {
            return delegate.getModel(p_184393_1_, p_184393_2_, p_184393_3_);
        }

        @Override
        public void renderStatic(ItemStack p_229110_1_, ItemCameraTransforms.TransformType p_229110_2_, int p_229110_3_, int p_229110_4_, MatrixStack p_229110_5_, IRenderTypeBuffer p_229110_6_) {
            delegate.renderStatic(p_229110_1_, p_229110_2_, p_229110_3_, p_229110_4_, p_229110_5_, p_229110_6_);
        }

        @Override
        public void renderStatic(@Nullable LivingEntity p_229109_1_, ItemStack p_229109_2_, ItemCameraTransforms.TransformType p_229109_3_, boolean p_229109_4_, MatrixStack p_229109_5_, IRenderTypeBuffer p_229109_6_, @Nullable World p_229109_7_, int p_229109_8_, int p_229109_9_) {
            delegate.renderStatic(p_229109_1_, p_229109_2_, p_229109_3_, p_229109_4_, p_229109_5_, p_229109_6_, p_229109_7_, p_229109_8_, p_229109_9_);
        }

        @Override
        public void renderGuiItem(ItemStack p_175042_1_, int p_175042_2_, int p_175042_3_) {
            delegate.renderGuiItem(p_175042_1_, p_175042_2_, p_175042_3_);
        }

        @Override
        public void renderAndDecorateItem(ItemStack p_180450_1_, int p_180450_2_, int p_180450_3_) {
            delegate.renderAndDecorateItem(p_180450_1_, p_180450_2_, p_180450_3_);
        }

        @Override
        public void renderAndDecorateFakeItem(ItemStack p_239390_1_, int p_239390_2_, int p_239390_3_) {
            delegate.renderAndDecorateFakeItem(p_239390_1_, p_239390_2_, p_239390_3_);
        }

        @Override
        public void renderAndDecorateItem(LivingEntity p_184391_1_, ItemStack p_184391_2_, int p_184391_3_, int p_184391_4_) {
            delegate.renderAndDecorateItem(p_184391_1_, p_184391_2_, p_184391_3_, p_184391_4_);
        }

        @Override
        public void onResourceManagerReload(IResourceManager p_195410_1_) {
            delegate.onResourceManagerReload(p_195410_1_);
        }

        @Override
        public IResourceType getResourceType() {
            return delegate.getResourceType();
        }

        @Override
        public CompletableFuture<Void> reload(IStage p_215226_1_, IResourceManager p_215226_2_, IProfiler p_215226_3_, IProfiler p_215226_4_, Executor p_215226_5_, Executor p_215226_6_) {
            return delegate.reload(p_215226_1_, p_215226_2_, p_215226_3_, p_215226_4_, p_215226_5_, p_215226_6_);
        }

        @Override
        public String getName() {
            return delegate.getName();
        }
        //endregion
    }
}