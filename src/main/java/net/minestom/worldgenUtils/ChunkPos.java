package net.minestom.worldgenUtils;

import net.minestom.server.instance.Chunk;

public class ChunkPos {

	int x, z;

	public ChunkPos() {

	}

	public ChunkPos(final int x, final int z) {
		this.x = x;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public void setX(final int x) {
		this.x = x;
	}

	public void setZ(final int z) {
		this.z = z;
	}
	public void setXZ(final int x, final int z) {
		this.x = x;
		this.z = z;
	}

	public Chunk toChunk(GenerationContext generationContext) {
		return generationContext.getInstance().getChunk(x, z);
	}

	public static ChunkPos fromChunk(Chunk c) {
		return new ChunkPos(c.getChunkX(), c.getChunkZ());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ChunkPos that = (ChunkPos) o;

		if (getX() != that.getX()) return false;
		return getZ() == that.getZ();
	}

	@Override
	public int hashCode() {
		int result = getX();
		result = 31 * result + getZ();
		return result;
	}

}
