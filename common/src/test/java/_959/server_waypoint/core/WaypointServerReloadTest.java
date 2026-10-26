package _959.server_waypoint.core;

import _959.server_waypoint.core.waypoint.SimpleWaypoint;
import _959.server_waypoint.core.waypoint.WaypointList;
import _959.server_waypoint.core.waypoint.WaypointPos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WaypointServerReloadTest {
    private static final String DIMENSION = "minecraft:overworld";

    @TempDir
    Path directory;

    @Test
    void reloadUsesEditedWaypointFileInsteadOfOverwritingItWithMemory() throws Exception {
        WaypointServerCore server = new WaypointServerCore(this.directory) { };
        server.initConfigAndLanguageResource();
        server.addWaypoint(DIMENSION, "Live", waypoint("before", 1), ignored -> { });
        server.saveWaypointFile(server.getWaypointFileManager(DIMENSION));

        WaypointFilesManagerCore editor = new WaypointFilesManagerCore(this.directory.resolve("waypoints"));
        editor.addWaypoint(DIMENSION, "Live", waypoint("after", 2), ignored -> { });
        editor.saveWaypointFile(editor.getWaypointFileManager(DIMENSION));

        server.reload();

        WaypointList loaded = server.getWaypointFileManager(DIMENSION).getWaypointListByName("Live");
        assertNull(loaded.getWaypointByName("before"));
        assertEquals(2, loaded.getWaypointByName("after").x());
        assertEquals(1, server.toWorldReplacementData().dimensions().size());
    }

    private static SimpleWaypoint waypoint(String name, int x) {
        return new SimpleWaypoint(name, name, name.substring(0, 1), new WaypointPos(x, 64, 0),
                0xFFFFFF, 0, false, List.of(), "", null);
    }
}
