package net.minestom.worldgenUtils;

import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.Position;

import java.util.HashMap;

import static net.minestom.worldgenUtils.SimpleBlockPosition.at;


/**
 * A faster, more optimized, ChunkBatch that allows you to retrieve placed blocks in O(1) without hacky stuff.
 */
public class SingleChunkBatch {

	private final HashMap<SimpleBlockPosition, SimpleBlockData> data = new HashMap<>();
	private final Chunk chunk;

	public static SingleChunkBatch fromChunkBatch(Context ctx, int chunkX, int chunkZ) {
		return new SingleChunkBatch(ctx.getInstance().getChunk(chunkX, chunkZ));
	}

	public SingleChunkBatch(Chunk chunk) {
		this.chunk = chunk;
	}

	public void setBlock(Position pos, Block b) {
		setBlock((int)pos.getX(), (int)pos.getY(), (int)pos.getZ(), b);
	}

	public void setBlock(BlockPosition bpos, Block b) {
		setBlock(bpos.getX(), bpos.getY(), bpos.getZ(), b);
	}

	public void setBlock(int x, int y, int z, Block b) {
		setBlockId(x,y,z,b.getBlockId());
	}

	public void setBlockId(Position pos, short b) {
		setBlockId((int)pos.getX(), (int)pos.getY(), (int)pos.getZ(), b);
	}

	public void setBlockId(BlockPosition bpos, short b) {
		setBlockId(bpos.getX(), bpos.getY(), bpos.getZ(), b);
	}

	public void setBlockId(int x, int y, int z, final short b) {
		final SimpleBlockPosition at = at(x, y, z);
		data.put(at, new SimpleBlockData(x,y,z,b));
	}

	public boolean hasBlockAt(Position pos) {
		return hasBlockAt((int)pos.getX(), (int)pos.getY(), (int)pos.getZ());
	}

	public boolean hasBlockAt(BlockPosition bpos) {
		return hasBlockAt(bpos.getX(), bpos.getY(), bpos.getZ());
	}

	public boolean hasBlockAt(int x, int y, int z) {
		final SimpleBlockPosition at = at(x, y, z);
		return data.containsKey(at);
	}

	public short getBlockIdAt(Position pos) {
		return getBlockIdAt((int)pos.getX(), (int)pos.getY(), (int)pos.getZ());
	}

	public short getBlockIdAt(BlockPosition bpos) {
		return getBlockIdAt(bpos.getX(), bpos.getY(), bpos.getZ());
	}

	public short getBlockIdAt(int x, int y, int z) {
		final SimpleBlockPosition at = at(x, y, z);
		final SimpleBlockData simpleBlockData = data.get(at);
		if (simpleBlockData != null) {
			return simpleBlockData.blockStateId;
		}
		return 0;
	}

	public Block getBlockAt(Position pos) {
		return Block.fromStateId(getBlockIdAt(pos));
	}

	public Block getBlockAt(BlockPosition bpos) {
		return Block.fromStateId(getBlockIdAt(bpos));
	}

	public Block getBlockAt(int x, int y, int z) {
		return Block.fromStateId(getBlockIdAt(x, y, z));
	}

	public void apply() {
		for (final SimpleBlockData bd : data.values()) {
			bd.apply(chunk);
		}
		chunk.sendChunk();
	}

	public HashMap<SimpleBlockPosition, SimpleBlockData> getData() {
		return data;
	}

}
