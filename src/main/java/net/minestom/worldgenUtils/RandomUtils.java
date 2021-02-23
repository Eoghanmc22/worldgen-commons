package net.minestom.worldgenUtils;

public class RandomUtils {

    public static int randomIntBetween(Context ctx, int min, int max) {
        int range = max - min;
        return ctx.getRNG().nextInt(range) + min;
    }

    public static float randomFloatBetween(Context ctx, float min, float max) {
        float range = max - min;
        return ctx.getRNG().nextFloat() * range + min;
    }

    public static double randomDoubleBetween(Context ctx, double min, double max) {
        double range = max - min;
        return ctx.getRNG().nextDouble() * range + min;
    }
}
