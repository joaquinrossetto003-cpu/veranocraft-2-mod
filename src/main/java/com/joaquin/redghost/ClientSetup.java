package com.joaquin.redghost;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RedGhostMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() ->
                RenderingRegistry.registerEntityRenderingHandler(
                        RedGhostMod.RED_GHOST.get(),
                        GhostRenderer::new
                )
        );
    }

    public static class GhostRenderer extends MobRenderer<RedGhostEntity, GhostModel> {

        private static final ResourceLocation TEXTURE =
                new ResourceLocation(RedGhostMod.MODID, "textures/entity/red_ghost.png");

        public GhostRenderer(EntityRendererManager manager) {
            super(manager, new GhostModel(), 0.2F);
        }

        @Override
        public ResourceLocation getTextureLocation(RedGhostEntity entity) {
            return TEXTURE;
        }

        @Override
        public void render(RedGhostEntity entity, float entityYaw, float partialTicks,
                           MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
            boolean isDay = entity.level.isDay();
            this.model.setDayMode(isDay);
            int light = isDay ? packedLight : Math.max(packedLight, 0xF000F0);
            super.render(entity, entityYaw, partialTicks, matrixStack, buffer, light);
        }
    }

    public static class GhostModel extends EntityModel<RedGhostEntity> {

        private final ModelRenderer body;
        private final ModelRenderer bodyLower;
        private final ModelRenderer fistLeft;
        private final ModelRenderer fistRight;
        private final ModelRenderer eyeLeft;
        private final ModelRenderer eyeRight;
        private final ModelRenderer mouth;
        private boolean dayMode = false;

        public GhostModel() {
            this.texWidth = 64;
            this.texHeight = 64;

            body = new ModelRenderer(this);
            body.setPos(0.0F, 0.0F, 0.0F);
            body.texOffs(0, 0).addBox(-4.0F, -10.0F, -3.5F, 8, 8, 7);
            body.texOffs(0, 15).addBox(-3.5F, -3.0F, -3.0F, 7, 3, 6);

            bodyLower = new ModelRenderer(this);
            bodyLower.setPos(0.0F, 0.0F, 0.0F);
            bodyLower.texOffs(30, 0).addBox(-3.0F, 0.0F, -2.0F, 2, 2, 4);
            bodyLower.texOffs(30, 6).addBox(-1.0F, 0.5F, -2.0F, 2, 2, 4);
            bodyLower.texOffs(30, 12).addBox(1.0F, 0.0F, -2.0F, 2, 2, 4);

            fistLeft = new ModelRenderer(this);
            fistLeft.setPos(-6.5F, -5.0F, 0.0F);
            fistLeft.texOffs(0, 32).addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4);

            fistRight = new ModelRenderer(this);
            fistRight.setPos(6.5F, -5.0F, 0.0F);
            fistRight.texOffs(0, 32).addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4);

            eyeLeft = new ModelRenderer(this);
            eyeLeft.setPos(-1.8F, -7.2F, -3.6F);
            eyeLeft.texOffs(16, 32).addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2);

            eyeRight = new ModelRenderer(this);
            eyeRight.setPos(1.8F, -7.2F, -3.6F);
            eyeRight.texOffs(16, 32).addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2);

            mouth = new ModelRenderer(this);
            mouth.setPos(0.0F, -4.5F, -3.6F);
            mouth.texOffs(16, 38).addBox(-1.5F, -1.0F, -0.5F, 3, 2, 1);
        }

        public void setDayMode(boolean day) {
            this.dayMode = day;
        }

        @Override
        public void setupAnim(RedGhostEntity entity, float limbSwing, float limbSwingAmount,
                              float ageInTicks, float netHeadYaw, float headPitch) {
            float yaw = netHeadYaw * ((float) Math.PI / 180F);
            body.yRot = yaw;
            bodyLower.yRot = yaw;
            eyeLeft.yRot = yaw;
            eyeRight.yRot = yaw;
            mouth.yRot = yaw;

            float bob = MathHelper.sin(ageInTicks * 0.12F) * 0.2F;
            body.y = bob;
            bodyLower.y = bob;
            eyeLeft.y = -7.2F + bob;
            eyeRight.y = -7.2F + bob;
            mouth.y = -4.5F + bob;

            float fistBob = MathHelper.sin(ageInTicks * 0.18F) * 0.35F;
            float fistSwing = MathHelper.sin(ageInTicks * 0.25F) * 0.15F;
            boolean attacking = entity.getTarget() != null
                    && entity.distanceToSqr(entity.getTarget()) < 9.0D;
            float attackPush = attacking ? MathHelper.sin(ageInTicks * 1.2F) * 0.6F : 0.0F;

            fistLeft.y = -5.0F + fistBob + bob;
            fistRight.y = -5.0F - fistBob + bob;
            fistLeft.z = -fistSwing - attackPush;
            fistRight.z = fistSwing - attackPush;
            fistLeft.xRot = attacking ? -0.4F + MathHelper.sin(ageInTicks * 1.2F) * 0.5F : 0.0F;
            fistRight.xRot = attacking ? -0.4F + MathHelper.sin(ageInTicks * 1.2F + 0.5F) * 0.5F : 0.0F;
        }

        @Override
        public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer,
                                   int packedLight, int packedOverlay,
                                   float red, float green, float blue, float alpha) {
            if (dayMode) {
                eyeLeft.render(matrixStack, buffer, packedLight, packedOverlay, 0.05F, 0.05F, 0.05F, 1.0F);
                eyeRight.render(matrixStack, buffer, packedLight, packedOverlay, 0.05F, 0.05F, 0.05F, 1.0F);
            } else {
                body.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                bodyLower.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                fistLeft.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                fistRight.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                eyeLeft.render(matrixStack, buffer, packedLight, packedOverlay, 0.05F, 0.05F, 0.05F, 1.0F);
                eyeRight.render(matrixStack, buffer, packedLight, packedOverlay, 0.05F, 0.05F, 0.05F, 1.0F);
                mouth.render(matrixStack, buffer, packedLight, packedOverlay, 0.05F, 0.05F, 0.05F, 1.0F);
            }
        }
    }
}
