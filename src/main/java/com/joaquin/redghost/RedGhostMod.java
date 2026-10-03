package com.joaquin.redghost;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(RedGhostMod.MODID)
public class RedGhostMod {

    public static final String MODID = "redghost";

    /** Pestaña propia en el inventario creativo */
    public static final ItemGroup TAB = new ItemGroup("redghost") {
        @Override
        public ItemStack makeIcon() {
            // Icono temporal: tinte rojo. Más adelante se puede cambiar por el huevo o la espada.
            return new ItemStack(net.minecraft.item.Items.RED_DYE);
        }
    };

    // ========== REGISTROS DIFERIDOS (forma correcta en Forge 1.16.5) ==========

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    /** Tipo de entidad del Red Ghost */
    public static final RegistryObject<EntityType<RedGhostEntity>> RED_GHOST = ENTITIES.register(
            "red_ghost",
            () -> EntityType.Builder
                    .<RedGhostEntity>of(RedGhostEntity::new, EntityClassification.MONSTER)
                    .sized(0.5F, 0.9F)          // Pequeño: aprox. hasta donde empieza la cabeza de Steve
                    .clientTrackingRange(10)
                    .updateInterval(2)
                    .build(new ResourceLocation(MODID, "red_ghost").toString())
    );

    /**
     * Huevo de aparición.
     * Usamos ForgeSpawnEggItem porque acepta un Supplier y evita el error
     * de "entity todavía es null" que tiene el SpawnEggItem normal.
     */
    public static final RegistryObject<Item> RED_GHOST_SPAWN_EGG = ITEMS.register(
            "red_ghost_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    RED_GHOST,          // Supplier del EntityType
                    0x8B0000,           // color primario (rojo oscuro)
                    0xFF4444,           // color secundario (rojo claro)
                    new Item.Properties().tab(TAB)
            )
    );

    // ========== CONSTRUCTOR ==========

    public RedGhostMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ENTITIES.register(modBus);
        ITEMS.register(modBus);

        modBus.addListener(this::commonSetup);
        modBus.addListener(this::onEntityAttributeCreation);

        // Eventos del servidor / juego (comandos, etc.)
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Por ahora vacío. Aquí irían cosas que se ejecutan una sola vez al cargar el mod.
    }

    /** Obligatorio: registra los atributos (vida, daño, velocidad...) de la entidad */
    private void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(RED_GHOST.get(), RedGhostEntity.createAttributes().build());
    }

    /** Registra el comando /sun cuando el servidor carga los comandos */
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        GhostCommands.register(event.getDispatcher());
    }
}
