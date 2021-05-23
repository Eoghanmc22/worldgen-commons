package net.minestom.worldgenUtils;

import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.Position;
import net.minestom.server.utils.chunk.ChunkUtils;

import java.util.HashMap;
import java.util.Map;

import static net.minestom.worldgenUtils.SimpleBlockPosition.at;


/**
 * A faster, more optimized, BlockBatch that allows you to retrieve placed blocks in O(1) without hacky stuff.
 */
public class Batch {

	private final HashMap<ChunkPos, HashMap<SimpleBlockPosition, SimpleBlockData>> data = new HashMap<>();
	private final BlockPosition offset;

	public Batch(BlockPosition offset) {
		this.offset = offset;
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
		x += offset.getX();
		y += offset.getY();
		z += offset.getZ();
		final SimpleBlockPosition at = at(x, y, z);
		final ChunkPos chunk = at.getChunk();
		final HashMap<SimpleBlockPosition, SimpleBlockData> chunkData = data.get(chunk);
		if (chunkData != null) {
			chunkData.put(at, new SimpleBlockData(x,y,z,b));
		} else {
			final HashMap<SimpleBlockPosition, SimpleBlockData> newChunkData = new HashMap<>();
			newChunkData.put(at, new SimpleBlockData(x,y,z,b));
			data.put(chunk, newChunkData);
		}
	}

	public boolean hasBlockAt(Position pos) {
		return hasBlockAt((int)pos.getX(), (int)pos.getY(), (int)pos.getZ());
	}

	public boolean hasBlockAt(BlockPosition bpos) {
		return hasBlockAt(bpos.getX(), bpos.getY(), bpos.getZ());
	}

	public boolean hasBlockAt(int x, int y, int z) {
		x += offset.getX();
		y += offset.getY();
		z += offset.getZ();
		final SimpleBlockPosition at = at(x, y, z);
		return data.containsKey(at.getChunk()) && data.get(at.getChunk()).containsKey(at);
	}

	public short getBlockIdAt(Position pos) {
		return getBlockIdAt((int)pos.getX(), (int)pos.getY(), (int)pos.getZ());
	}

	public short getBlockIdAt(BlockPosition bpos) {
		return getBlockIdAt(bpos.getX(), bpos.getY(), bpos.getZ());
	}

	public short getBlockIdAt(int x, int y, int z) {
		x += offset.getX();
		y += offset.getY();
		z += offset.getZ();
		final SimpleBlockPosition at = at(x, y, z);
		if (data.containsKey(at.getChunk())) {
			HashMap<SimpleBlockPosition, SimpleBlockData> data = this.data.get(at.getChunk());
			final SimpleBlockData simpleBlockData = data.get(at);
			if (simpleBlockData != null) {
				return simpleBlockData.blockStateId;
			}
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

	public void apply(Context generationContext) {
		for (final Map.Entry<ChunkPos, HashMap<SimpleBlockPosition, SimpleBlockData>> chunkdata : this.data.entrySet()) {
			Batch.applyChunk(generationContext, chunkdata.getKey(), chunkdata.getValue());
		}
		generationContext.getInstance().refreshLastBlockChangeTime();
	}

	public static void applyChunk(Context generationContext, ChunkPos cpos, HashMap<SimpleBlockPosition, SimpleBlockData> data) {
		final Chunk chunk = cpos.toChunk(generationContext);
		if (ChunkUtils.isLoaded(chunk)) {
			synchronized (chunk) {
				for (final SimpleBlockData bd : data.values()) {
					bd.apply(chunk);
				}
				// TODO remove
				generationContext.getInstance().scheduleNextTick(instance -> {
					chunk.sendChunk();
				});
			}
		}
	}

	public HashMap<ChunkPos, HashMap<SimpleBlockPosition, SimpleBlockData>> getData() {
		return data;
	}

}
