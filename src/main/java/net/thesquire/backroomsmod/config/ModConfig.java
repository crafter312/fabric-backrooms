package net.thesquire.backroomsmod.config;

import reborncore.common.config.Config;

public class ModConfig {

    public static final int MAX_TELEPORT_DEST_SEARCH_DISTANCE = 100;

    @Config(config = "machines", category = "industrial_alloy_smelter", key = "IndustrialAlloySmelterMaxInput", comment = "Industrial Alloy Smelter Max Input (Energy per tick)")
    public static int industrialAlloySmelterMaxInput = 128;

    @Config(config = "machines", category = "industrial_alloy_smelter", key = "IndustrialAlloySmelterMaxEnergy", comment = "Industrial Alloy Smelter Max Energy")
    public static int industrialAlloySmelterMaxEnergy = 30_000;

    @Config(config = "machines", category = "magnetic_distortion_system_control_computer", key = "MagneticDistortionSystemControlComputerMaxInput", comment = "Magnetic Distortion System Max Input (Energy per tick)")
    public static int magneticDistortionSystemControlComputerMaxInput = 8192;

    @Config(config = "machines", category = "magnetic_distortion_system_control_computer", key = "MagneticDistortionSystemControlComputerMaxEnergy", comment = "Magnetic Distortion System Max Energy")
    public static int magneticDistortionSystemControlComputerMaxEnergy = 1_000_000;

    @Config(config = "machines", category = "magnetic_distortion_system_control_computer", key = "MagneticDistortionSystemControlComputerInitEnergyUsage", comment = "Magnetic Distortion System Initial Energy Usage")
    public static int magneticDistortionSystemControlComputerInitEnergyUsage = 500_000;

    @Config(config = "machines", category = "magnetic_distortion_system_control_computer", key = "MagneticDistortionSystemControlComputerEnergyUsage", comment = "Magnetic Distortion System Energy Usage")
    public static int magneticDistortionSystemControlComputerEnergyUsage = 4096;

    @Config(config = "events", category = "blackout", key = "MinBlackoutRange", comment = "Minimum range for a blackout GameEvent")
    public static int minBlackoutRange = 16;

    @Config(config = "events", category = "blackout", key = "MaxBlackoutRange", comment = "Maximum range for a blackout GameEvent")
    public static int maxBlackoutRange = 64;

    @Config(config = "events", category = "backrooms_memory", key = "DatabaseBlocksPlacedThreshold", comment = "Threshold for blocks placed by all players per chunk section above which the Backrooms 'remembers'")
    public static int databaseBlocksPlacedThreshold = 32;

    @Config(config = "events", category = "backrooms_memory", key = "DatabaseTicksSpentThreshold", comment = "Threshold for ticks spent by all players per chunk section above which the Backrooms 'remembers'")
    public static int databaseTicksSpentThreshold = 12000;

}
