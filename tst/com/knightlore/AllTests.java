package com.knightlore;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.knightlore.game.area.MapTest;
import com.knightlore.game.area.RoomTest;
import com.knightlore.game.area.generation.PerlinNoiseGeneratorTest;
import com.knightlore.game.area.generation.RoomConnectionTest;
import com.knightlore.game.area.generation.RoomGeneratorTest;
import com.knightlore.game.manager.AIManagerTest;
import com.knightlore.leveleditor.PenTest;
import com.knightlore.leveleditor.TileButtonTest;
import com.knightlore.network.TCPConnection;
import com.knightlore.network.TCPConnectionTest;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.network.protocol.NetworkUtilsTest;
import com.knightlore.network.server.ReceiveTest;
import com.knightlore.utils.pathfinding.PathFinderTest;

@RunWith(Suite.class)
@SuiteClasses({
    PerlinNoiseGeneratorTest.class,
    RoomConnectionTest.class,
    RoomGeneratorTest.class,
    MapTest.class,
    RoomTest.class,
    AIManagerTest.class,
    PenTest.class,
    TileButtonTest.class,
    PathFinderTest.class,
    NetworkUtilsTest.class,
    ReceiveTest.class,
    TCPConnectionTest.class
})

public class AllTests {}
