package net.minestom.worldgenUtils;

import net.minestom.server.instance.Instance;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.Position;

import java.util.Random;

public class GenerationContext {

	public final Random rng;
	public final Batch batch;
	public Position origin;
	private final Instance instance;

	public GenerationContext(Instance instance) {
		this.instance = instance;
		this.rng = new Random();
		batch = new Batch();
		origin = new Position();
	}

	public void apply(BlockPosition offset) {
		batch.apply(this, offset);
	}

	public Instance getInstance() {
		return instance;
	}

}
