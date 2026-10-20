package net.deadlydiamond98.archipelago.compat.create;

import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.Unique;

import java.lang.reflect.Method;

// Not quite an accessor
// Uses reflection to access some annoying private elements of FunnelBlockEntity
// Real Mixin Accessors don't work
public class CreateFunnelAccess {
    public static final Object PAUSED;
    public static final Method determineCurrentMode;

    @Unique
    private static <T extends Enum<T>> T getEnumEntry(String className, String name) {
        try {
            final var clazz = Class.forName(className);
            return Enum.valueOf((Class<T>) clazz, name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Unique
    private static Method getMethod(String className, String name) {
        try {
            final var clazz = Class.forName(className);
            Method method = clazz.getDeclaredMethod(name);
            method.setAccessible(true);
            return method;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        if (FabricLoader.getInstance().isModLoaded("create")) {
            PAUSED = getEnumEntry("com.simibubi.create.content.logistics.funnel.FunnelBlockEntity$Mode", "PAUSED");
            determineCurrentMode = getMethod("com.simibubi.create.content.logistics.funnel.FunnelBlockEntity", "determineCurrentMode");
        } else {
            PAUSED = null;
            determineCurrentMode = null;
        }
    }
}
