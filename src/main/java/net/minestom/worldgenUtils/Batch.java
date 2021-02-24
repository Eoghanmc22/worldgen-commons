package net.minestom.worldgenUtils;

import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.Position;

import java.util.HashMap;
import java.util.Map;

import static net.minestom.worldgenUtils.SimpleBlockPosition.at;


/**
 * A faster, more optimized, BlockBatch that allows you to retrieve placed blocks in O(1) without hacky stuff.
 */
public class Batch {

	private final HashMap<ChunkPos, HashMap<SimpleBlockPosition, SimpleBlockData>> data = new HashMap<>();

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

	public void setBlockId(int x, int y, int z, short b) {
		final SimpleBlockPosition at = at(x, y, z);
		final ChunkPos chunk = at.getChunk();
		HashMap<SimpleBlockPosition, SimpleBlockData> chunkData;
		if (data.containsKey(chunk)) {
			chunkData = data.get(chunk);
			chunkData.put(at, new SimpleBlockData(x,y,z,b));
		} else {
			chunkData = new HashMap<>();
			chunkData.put(at, new SimpleBlockData(x,y,z,b));
			data.put(chunk, chunkData);
		}
	}

	public boolean hasBlockAt(Position pos) {
		return hasBlockAt((int)pos.getX(), (int)pos.getY(), (int)pos.getZ());
	}

	public boolean hasBlockAt(BlockPosition bpos) {
		return hasBlockAt(bpos.getX(), bpos.getY(), bpos.getZ());
	}

	public boolean hasBlockAt(int x, int y, int z) {
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
		final SimpleBlockPosition at = at(x, y, z);
		if (data.containsKey(at.getChunk())) {
			HashMap<SimpleBlockPosition, SimpleBlockData> data = this.data.get(at.getChunk());
			if (data.containsKey(at)) {
				return data.get(at).blockStateId;
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

	public void apply(Context generationContext, BlockPosition offset) {
		for (final Map.Entry<ChunkPos, HashMap<SimpleBlockPosition, SimpleBlockData>> chunkdata : this.data.entrySet()) {
			Batch.applyChunk(generationContext, offset, chunkdata.getKey(), chunkdata.getValue());
		}
		generationContext.getInstance().refreshLastBlockChangeTime();
	}

	public static void applyChunk(Context generationContext, BlockPosition offset, ChunkPos cpos, HashMap<SimpleBlockPosition, SimpleBlockData> data) {
		if (cpos.toChunk(generationContext).isLoaded()) {
			for (final SimpleBlockData bd : data.values()) {
				bd.apply(generationContext.getInstance(), offset.getX(), offset.getY(), offset.getZ());
			}
			cpos.toChunk(generationContext).sendChunkUpdate();
		}
	}

	public HashMap<ChunkPos, HashMap<SimpleBlockPosition, SimpleBlockData>> getData() {
		return data;
	}

}
