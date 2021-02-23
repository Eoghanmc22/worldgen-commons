package net.minestom.worldgenUtils;

import net.minestom.server.instance.Instance;

import java.util.Random;

public interface Context {

	Instance getInstance();

	Random getRNG();

}
