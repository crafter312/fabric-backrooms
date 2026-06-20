package net.thesquire.backroomsmod.world.activity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.BackroomsMod;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.*;
import java.util.BitSet;

public class DatabaseManager {

    private static Connection connection;
    private static PreparedStatement blockPlacementStatement;
    private static PreparedStatement blockBreakStatement;
    private static PreparedStatement serverTickStatement;
    private static PreparedStatement getBlocksPlacedStatement;
    private static PreparedStatement getTicksSpentStatement;
    private static PreparedStatement getBlockMaskStatement;

    public static void initialize(File worldDir) {
        try {
            File dbFile = new File(worldDir, "backrooms_activity.db");
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            connection = DriverManager.getConnection(url);

            // Tell SQLite to wait up to 5000 milliseconds for a lock to clear before throwing an error
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA busy_timeout = 5000;");
                stmt.execute("PRAGMA journal_mode = WAL;");
                stmt.execute("PRAGMA synchronous = NORMAL;");
            }

            createTable();
            prepareStatements();

            BackroomsMod.LOGGER.info("Successfully initialized SQL chunk section activity database for {}", BackroomsMod.MOD_ID);
        }
        catch (SQLException e) {
            BackroomsMod.LOGGER.error("[SQL Database]: error while initializing database", e);
        }
    }

    /**
     * Closes the database connection cleanly when the server shuts down
     */
    public static void close() {
        try {
            if (blockPlacementStatement != null) blockPlacementStatement.close();
            if (blockBreakStatement != null) blockBreakStatement.close();
            if (serverTickStatement != null) serverTickStatement.close();
            if (getBlocksPlacedStatement != null) getBlocksPlacedStatement.close();
            if (getTicksSpentStatement != null) getTicksSpentStatement.close();
            if (getBlockMaskStatement != null) getBlockMaskStatement.close();

            if (connection != null && !connection.isClosed()) {
                connection.close();
                BackroomsMod.LOGGER.info("Database connection closed safely for {}", BackroomsMod.MOD_ID);
            }
        }
        catch (SQLException e) {
            BackroomsMod.LOGGER.error("[SQL Database]: error while closing database", e);
        }
        finally {
            blockPlacementStatement = null;
            blockBreakStatement = null;
            serverTickStatement = null;
            getBlocksPlacedStatement = null;
            getTicksSpentStatement = null;
            getBlockMaskStatement = null;
            connection = null;
        }
    }

    /**
     * Sets up the data columns (like defining a custom Class/Object structure in SQL)
     */
    private static void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS chunk_activity (" +
                "x INTEGER, " +
                "y INTEGER, " +
                "z INTEGER, " +
                "blocks_placed INTEGER DEFAULT 0, " +
                "ticks_spent INTEGER DEFAULT 0, " +
                "block_mask BLOB, " +
                "PRIMARY KEY (x, y, z)" +
                ");";

        // Create a temporary statement runner, execute the text, and close it
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void prepareStatements() throws SQLException {
        // Template for when a block is placed:
        // Try to insert a fresh row with 1 block placed. If x, y, and z already exist, just add 1 to blocks_placed.
        String blockPlaceSql = "INSERT INTO chunk_activity (x, y, z, blocks_placed, ticks_spent, block_mask) VALUES (?, ?, ?, 1, 0, ?) " +
                "ON CONFLICT(x, y, z) DO UPDATE SET blocks_placed = blocks_placed + 1, block_mask = ?;";
        blockPlacementStatement = connection.prepareStatement(blockPlaceSql);

        // Template for when a block is broken:
        // Try to insert a fresh row with 0 blocks placed. If x, y, and z already exist, subtract a block, but never
        // go below zero.
        String blockBreakSql = "INSERT INTO chunk_activity (x, y, z, blocks_placed, ticks_spent, block_mask) VALUES (?, ?, ?, 0, 0, ?) " +
                "ON CONFLICT(x, y, z) DO UPDATE SET blocks_placed = MAX(0, blocks_placed - 1), block_mask = ?;";
        blockBreakStatement = connection.prepareStatement(blockBreakSql);

        // Template for when a chunk section is ticked:
        // Try to insert a fresh row with 1 tick spent. If x, y, and z already exists, just add 1 to ticks_spent.
        String tickSql = "INSERT INTO chunk_activity (x, y, z, blocks_placed, ticks_spent) VALUES (?, ?, ?, 0, 1) " +
                "ON CONFLICT(x, y, z) DO UPDATE SET ticks_spent = ticks_spent + 1;";
        serverTickStatement = connection.prepareStatement(tickSql);

        // Template to look up blocks_placed for a specific x, y, and z coordinate
        String selectBlockSql = "SELECT blocks_placed FROM chunk_activity WHERE x = ? AND y = ? AND z = ?;";
        getBlocksPlacedStatement = connection.prepareStatement(selectBlockSql);

        // Template to look up ticks_spent for a specific x, y, and z coordinate
        String selectTickSql = "SELECT ticks_spent FROM chunk_activity WHERE x = ? AND y = ? AND z = ?;";
        getTicksSpentStatement = connection.prepareStatement(selectTickSql);

        // Template to look up block_mask for a specitic x, y, and z coordinate
        String selectBlockMaskSql = "SELECT block_mask FROM chunk_activity WHERE x = ? AND y = ? AND z = ?;";
        getBlockMaskStatement = connection.prepareStatement(selectBlockMaskSql);
    }

    public static void logBlockPlacement(ItemPlacementContext context) {
        BlockPos pos = context.getBlockPos();
        ChunkSectionPos sectionPos = ChunkSectionPos.from(pos);
        int x = sectionPos.getX();
        int y = sectionPos.getY();
        int z = sectionPos.getZ();
        try {

            // Get existing blocks placed mask if it exists
            BitSet bitSet;
            try (var rs = getBlockMaskStatement.executeQuery()) {
                if (rs.next()) {
                    byte[] bytes = rs.getBytes("block_mask");
                    bitSet = (bytes != null) ? BitSet.valueOf(bytes) : new BitSet(4096);
                }
                else {
                    bitSet = new BitSet(4096);
                }
            }

            // Calculate flat index and flip bit to true
            short bitIndex = ChunkSectionPos.packLocal(pos);
            bitSet.set(bitIndex, true);

            // Copy the variable-length BitSet bytes into our fixed 512-byte buffer (4096 bits, for size of chunk section)
            byte[] updatedBytes = new byte[512];
            byte[] rawBitSetBytes = bitSet.toByteArray();
            System.arraycopy(rawBitSetBytes, 0, updatedBytes, 0, Math.min(rawBitSetBytes.length, updatedBytes.length));

            // Plug values to write into the question marks: 1st ?, 2nd ?, 3rd ?, 4th ?, and 5th ?
            blockPlacementStatement.setInt(1, x);
            blockPlacementStatement.setInt(2, y);
            blockPlacementStatement.setInt(3, z);
            blockPlacementStatement.setBytes(4, updatedBytes);
            blockPlacementStatement.setBytes(5, updatedBytes);
            blockPlacementStatement.executeUpdate();
        } catch (SQLException e) {
            BackroomsMod.LOGGER.error("[SQL Database]: error while logging block placement", e);
        }

        //int blocksPlaced = getBlocksPlaced(sectionPos);
        //if ((blocksPlaced % 5) == 0)
        //    BackroomsMod.LOGGER.info("[SQL Database]: {} blocks placed in {}", blocksPlaced, sectionPos);
    }

    public static void logPlayerBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        ChunkSectionPos sectionPos = ChunkSectionPos.from(pos);
        int x = sectionPos.getX();
        int y = sectionPos.getY();
        int z = sectionPos.getZ();
        try {

            // Get existing blocks placed mask if it exists
            BitSet bitSet;
            try (var rs = getBlockMaskStatement.executeQuery()) {
                if (rs.next()) {
                    byte[] bytes = rs.getBytes("block_mask");
                    bitSet = (bytes != null) ? BitSet.valueOf(bytes) : new BitSet(4096);
                }
                else {
                    bitSet = new BitSet(4096);
                }
            }

            // Calculate flat index and flip bit to false
            short bitIndex = ChunkSectionPos.packLocal(pos);
            bitSet.set(bitIndex, false);

            // Copy the variable-length BitSet bytes into our fixed 512-byte buffer (4096 bits, for size of chunk section)
            byte[] updatedBytes = new byte[512];
            byte[] rawBitSetBytes = bitSet.toByteArray();
            System.arraycopy(rawBitSetBytes, 0, updatedBytes, 0, Math.min(rawBitSetBytes.length, updatedBytes.length));

            // Plug values to write into the question marks: 1st ?, 2nd ?, 3rd ?, 4th ?, and 5th ?
            blockBreakStatement.setInt(1, x);
            blockBreakStatement.setInt(2, y);
            blockBreakStatement.setInt(3, z);
            blockBreakStatement.setBytes(4, updatedBytes);
            blockBreakStatement.setBytes(5, updatedBytes);
            blockBreakStatement.executeUpdate();
        } catch (SQLException e) {
            BackroomsMod.LOGGER.error("[SQL Database]: error while logging player block break", e);
        }

        //int blocksPlaced = getBlocksPlaced(sectionPos);
        //if ((blocksPlaced % 5) == 0)
        //    BackroomsMod.LOGGER.info("[SQL Database]: {} blocks placed in {}", blocksPlaced, sectionPos);
    }

    public static void logTickSpent(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            ChunkSectionPos sectionPos = ChunkSectionPos.from(player.getPos());
            try {
                // Plug your coordinates into the question marks
                serverTickStatement.setInt(1, sectionPos.getX());
                serverTickStatement.setInt(2, sectionPos.getY());
                serverTickStatement.setInt(3, sectionPos.getZ());

                // Tell Java to execute the template
                serverTickStatement.executeUpdate();
            } catch (SQLException e) {
                BackroomsMod.LOGGER.error("[SQL Database]: error while logging player tick spent in chunk section", e);
            }

            //int ticksSpent = getTicksSpent(sectionPos);
            //if ((ticksSpent % 100) == 0)
            //    BackroomsMod.LOGGER.info("[SQL Database]: {} ticks spent by all players in {}", ticksSpent, sectionPos);
        }
    }

    /**
     * Looks up the number of blocks placed in a specific chunk section.
     * Returns 0 if no data exists yet.
     */
    private static int getBlocksPlaced(ChunkSectionPos sectionPos) {
        try {
            // Plug the coordinates into the query template
            getBlocksPlacedStatement.setInt(1, sectionPos.getX());
            getBlocksPlacedStatement.setInt(2, sectionPos.getY());
            getBlocksPlacedStatement.setInt(3, sectionPos.getZ());

            // Execute the query and get a "ResultSet" (a table of results)
            try (var resultSet = getBlocksPlacedStatement.executeQuery()) {
                // If the database found a row matching these coordinates
                if (resultSet.next()) {
                    // Grab the integer from the "blocks_placed" column
                    return resultSet.getInt("blocks_placed");
                }
            }
        } catch (SQLException e) {
            BackroomsMod.LOGGER.error("[SQL Database]: error while getting blocks placed data", e);
        }
        return 0;
    }

    /**
     * Looks up the number of ticks spent by all players in a specific
     * chunk section. Returns 0 if no data exists yet.
     */
    private static int getTicksSpent(ChunkSectionPos sectionPos) {
        try {
            // Plug the coordinates into the query template
            getTicksSpentStatement.setInt(1, sectionPos.getX());
            getTicksSpentStatement.setInt(2, sectionPos.getY());
            getTicksSpentStatement.setInt(3, sectionPos.getZ());

            // Execute the query and get a "ResultSet" (a table of results)
            try (var resultSet = getTicksSpentStatement.executeQuery()) {
                // If the database found a row matching these coordinates
                if (resultSet.next()) {
                    // Grab the integer from the "ticks_spent" column
                    return resultSet.getInt("ticks_spent");
                }
            }
        } catch (SQLException e) {
            BackroomsMod.LOGGER.error("[SQL Database]: error while getting ticks spent data", e);
        }
        return 0;
    }

}
