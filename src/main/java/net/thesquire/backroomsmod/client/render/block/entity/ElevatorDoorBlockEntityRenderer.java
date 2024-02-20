package net.thesquire.backroomsmod.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.block.ModBlocks;
import net.thesquire.backroomsmod.block.custom.ElevatorDoor;
import net.thesquire.backroomsmod.block.entity.ElevatorDoorBlockEntity;
import net.thesquire.backroomsmod.client.render.entity.model.ModEntityModelLayers;

@Environment(value= EnvType.CLIENT)

public class ElevatorDoorBlockEntityRenderer implements BlockEntityRenderer<ElevatorDoorBlockEntity> {

    @SuppressWarnings("deprecation")
    public static final SpriteIdentifier DOOR_TEXTURE_ALL = new SpriteIdentifier(
            SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, BackroomsMod.makeId("block/level_2/elevator_door"));

    public static final String DOOR = "door";
    public static final String FRAME = "frame";

    private static final ModelTransform MODEL_TRANSFORM = ModelTransform.pivot(8.0F, 16.0F, -2.0F);
    private static final float OPEN_DISTANCE = 12.0F;

    public static TexturedModelData getBottomLeftTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(DOOR, ModelPartBuilder.create().uv(0, 0).mirrored()
                .cuboid(-8.0F, -16.0F, 3.0F, 14.0F, 16.0F, 4.0F, new Dilation(0.0F)).mirrored(false),
                MODEL_TRANSFORM);
        modelPartData.addChild(FRAME, ModelPartBuilder.create().uv(0, 20)
                .cuboid(6.0F, -16.0F, 2.0F, 2.0F, 16.0F, 6.0F, new Dilation(0.0F)),
                MODEL_TRANSFORM);
        return TexturedModelData.of(modelData, 64, 64);
    }
    public static TexturedModelData getBottomRightTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(DOOR, ModelPartBuilder.create().uv(0, 0)
                .cuboid(-6.0F, -16.0F, 3.0F, 14.0F, 16.0F, 4.0F, new Dilation(0.0F)),
                MODEL_TRANSFORM);
        modelPartData.addChild(FRAME, ModelPartBuilder.create().uv(0, 20).mirrored()
                .cuboid(-8.0F, -16.0F, 2.0F, 2.0F, 16.0F, 6.0F, new Dilation(0.0F)).mirrored(false),
                MODEL_TRANSFORM);
        return TexturedModelData.of(modelData, 64, 64);
    }
    public static TexturedModelData getTopLeftTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(DOOR, ModelPartBuilder.create().uv(0, 0).mirrored()
                .cuboid(-8.0F, -16.0F, 3.0F, 14.0F, 14.0F, 4.0F, new Dilation(0.0F)).mirrored(false),
                MODEL_TRANSFORM);
        modelPartData.addChild(FRAME, ModelPartBuilder.create().uv(0, 20)
                .cuboid(-2.0F, -16.0F, 10.0F, 2.0F, 16.0F, 6.0F, new Dilation(0.0F))
                .uv(16, 20).mirrored()
                .cuboid(-16.0F, -2.0F, 10.0F, 14.0F, 2.0F, 6.0F, new Dilation(0.0F)).mirrored(false),
                ModelTransform.pivot(16.0F, 16.0F, -10.0F)); // 8, 24, -8
        return TexturedModelData.of(modelData, 64, 64);
    }
    public static TexturedModelData getTopRightTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(DOOR, ModelPartBuilder.create().uv(0, 0)
                .cuboid(-6.0F, -16.0F, 3.0F, 14.0F, 14.0F, 4.0F, new Dilation(0.0F)),
                MODEL_TRANSFORM);
        modelPartData.addChild(FRAME, ModelPartBuilder.create().uv(0, 20).mirrored()
                .cuboid(-16.0F, -16.0F, 10.0F, 2.0F, 16.0F, 6.0F, new Dilation(0.0F))
                .mirrored(false).uv(16, 20)
                .cuboid(-14.0F, -2.0F, 10.0F, 14.0F, 2.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.pivot(16.0F, 16.0F, -10.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    /////////////////////////////////////////////////////////////////////////////////

    private final ModelPart bottomLeftDoor;
    private final ModelPart bottomLeftFrame;
    private final ModelPart bottomRightDoor;
    private final ModelPart bottomRightFrame;
    private final ModelPart topLeftDoor;
    private final ModelPart topLeftFrame;
    private final ModelPart topRightDoor;
    private final ModelPart topRightFrame;

    private final float leftDoorPivotX;
    private final float rightDoorPivotX;

    public ElevatorDoorBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        ModelPart bottomLeftModel = context.getLayerModelPart(ModEntityModelLayers.ELEVATOR_DOOR_BOTTOM_LEFT);
        this.bottomLeftDoor = bottomLeftModel.getChild(DOOR);
        this.bottomLeftFrame = bottomLeftModel.getChild(FRAME);
        ModelPart bottomRightModel = context.getLayerModelPart(ModEntityModelLayers.ELEVATOR_DOOR_BOTTOM_RIGHT);
        this.bottomRightDoor = bottomRightModel.getChild(DOOR);
        this.bottomRightFrame = bottomRightModel.getChild(FRAME);
        ModelPart topLeftModel = context.getLayerModelPart(ModEntityModelLayers.ELEVATOR_DOOR_TOP_LEFT);
        this.topLeftDoor = topLeftModel.getChild(DOOR);
        this.topLeftFrame = topLeftModel.getChild(FRAME);
        ModelPart topRightModel = context.getLayerModelPart(ModEntityModelLayers.ELEVATOR_DOOR_TOP_RIGHT);
        this.topRightDoor = topRightModel.getChild(DOOR);
        this.topRightFrame = topRightModel.getChild(FRAME);

        this.leftDoorPivotX = this.bottomLeftDoor.pivotX;
        this.rightDoorPivotX = this.bottomRightDoor.pivotX;
    }

    @Override
    public void render(ElevatorDoorBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        boolean bl = world != null;
        BlockState blockState = bl ? entity.getCachedState() : ModBlocks.ELEVATOR_DOOR.getDefaultState();
        if (!(blockState.getBlock() instanceof ElevatorDoor)) return;

        // START RENDERING
        matrices.push();

        // set block facing rotation
        float f = blockState.get(DoorBlock.FACING).asRotation();
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-f));
        matrices.translate(-0.5f, -0.5f, -0.5f);

        // get animation progress
        // note that 0 <= g <= 1
        DoubleBlockProperties.PropertySource<ElevatorDoorBlockEntity> propertySource = DoubleBlockProperties.toPropertySource(
                ModBlockEntities.ELEVATOR_DOOR,
                ElevatorDoor::getDoorPart,
                ElevatorDoor::getOppositePartDirection,
                DoorBlock.FACING,
                blockState,
                world,
                entity.getPos(),
                (worldAccess, pos) -> false);
        float g = propertySource.apply(ElevatorDoor.getAnimationProgressRetriever(entity)).get(tickDelta);

        // render door according to block properties
        DoubleBlockHalf doorHalf = blockState.contains(DoorBlock.HALF) ? blockState.get(DoorBlock.HALF) : DoubleBlockHalf.LOWER;
        DoorHinge hingeSide = blockState.contains(DoorBlock.HINGE) ? blockState.get(DoorBlock.HINGE) : DoorHinge.LEFT;
        VertexConsumer vertexConsumer = DOOR_TEXTURE_ALL.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);
        if (doorHalf == DoubleBlockHalf.LOWER) {
            if (hingeSide == DoorHinge.LEFT)
                this.render(matrices, vertexConsumer, this.bottomLeftDoor, this.bottomLeftFrame, hingeSide, g, light, overlay);
            else
                this.render(matrices, vertexConsumer, this.bottomRightDoor, this.bottomRightFrame, hingeSide, g, light, overlay);
        }
        else {
            if (hingeSide == DoorHinge.LEFT)
                this.render(matrices, vertexConsumer, this.topLeftDoor, this.topLeftFrame, hingeSide, g, light, overlay);
            else
                this.render(matrices, vertexConsumer, this.topRightDoor, this.topRightFrame, hingeSide, g, light, overlay);
        }

        // END RENDERING
        matrices.pop();
    }

    private void render(MatrixStack matrices, VertexConsumer vertices, ModelPart door, ModelPart frame, DoorHinge hingeSide, float openFactor, int light, int overlay) {
        float delta = (hingeSide == DoorHinge.LEFT ? 1 : -1) * OPEN_DISTANCE * openFactor;
        door.pivotX = (hingeSide == DoorHinge.LEFT ? this.leftDoorPivotX : this.rightDoorPivotX) + delta;
        door.render(matrices, vertices, light, overlay);
        frame.render(matrices, vertices, light, overlay);
    }

}
