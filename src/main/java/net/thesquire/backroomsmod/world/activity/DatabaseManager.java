package net.thesquire.backroomsmod.world.activity;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkSectionPos;
import net.thesquire.backroomsmod.BackroomsMod;

import java.sql.*;

public class DatabaseManager {

    private static Connection connection;
    private static PreparedStatement blockPlacementStatement;
    private static PreparedStatement serverTickStatement;
    private static PreparedStatement getBlocksPlacedStatement;
    private static PreparedStatement getTicksSpentStatement;

    public static void initialize() {
        try {
            String url = "jdbc:sqlite::memory:";
            connection = DriverManager.getConnection(url);

            // Tell SQLite to wait up to 5000 milliseconds for a lock to clear before throwing an error
            connection.createStatement().execute("PRAGMA busy_timeout = 5000;");

            createTable();
            prepareStatements();

            BackroomsMod.LOGGER.info("Successfully initialized SQL database for chunk activity for {}", BackroomsMod.MOD_ID);
        }
        catch (SQLException e) {
            e.printStackTrace();
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
                "PRIMARY KEY (x, y, z)" +
                ");";

        // Create a temporary statement runner, execute the text, and close it
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void prepareStatements() throws SQLException {
        // Template for when a block is placed:
        // Try to insert a fresh row with 1 block placed. If x, y, and z already exists, just add 1 to blocks_placed.
        String blockSql = "INSERT INTO chunk_activity (x, y, z, blocks_placed, ticks_spent) VALUES (?, ?, ?, 1, 0) " +
                "ON CONFLICT(x, y, z) DO UPDATE SET blocks_placed = blocks_placed + 1;";
        blockPlacementStatement = connection.prepareStatement(blockSql);

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
    }

    public static void logBlockPlacement(ItemPlacementContext context) {
        ChunkSectionPos sectionPos = ChunkSectionPos.from(context.getBlockPos());
        try {
            // Plug your coordinates into the question marks: 1st ?, 2nd ?, and 3rd ?
            blockPlacementStatement.setInt(1, sectionPos.getX());
            blockPlacementStatement.setInt(2, sectionPos.getY());
            blockPlacementStatement.setInt(3, sectionPos.getZ());

            // Tell Java to execute the template
            blockPlacementStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int blocksPlaced = getBlocksPlaced(sectionPos);
        if ((blocksPlaced % 5) == 0)
            BackroomsMod.LOGGER.info("[SQL Database]: {} blocks placed in {}", blocksPlaced, sectionPos);
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
                e.printStackTrace();
            }

            int ticksSpent = getTicksSpent(sectionPos);
            if ((ticksSpent % 100) == 0)
                BackroomsMod.LOGGER.info("[SQL Database]: {} ticks spent by all players in {}", ticksSpent, sectionPos);
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return 0;
    }

}
