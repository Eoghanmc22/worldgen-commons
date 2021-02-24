package net.minestom.worldgenUtils;

import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;

import java.util.Random;

public interface Context {

	InstanceContainer getInstance();

	Random getRNG();

}
