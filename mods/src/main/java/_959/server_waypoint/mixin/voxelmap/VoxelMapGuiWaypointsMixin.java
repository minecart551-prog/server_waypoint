//? if voxelmap {
package _959.server_waypoint.mixin.voxelmap;

import _959.server_waypoint.common.util.SyncedWaypointName;
import com.mamiyaotaru.voxelmap.util.Waypoint;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.mamiyaotaru.voxelmap.gui.GuiWaypoints;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static _959.server_waypoint.common.util.SyncedWaypointName.isVoxelMapSyncedWaypointName;

@Mixin(value = GuiWaypoints.class, remap = false)
public class VoxelMapGuiWaypointsMixin {
    @Shadow
    protected Waypoint selectedWaypoint;

    @Redirect(
            method = "deleteClicked",
            at = @At(value = "FIELD", target = "Lcom/mamiyaotaru/voxelmap/util/Waypoint;name:Ljava/lang/String;", opcode = Opcodes.GETFIELD),
            remap = false
    )
    private String sw$displaySyncedWaypointName(Waypoint waypoint) {
        return SyncedWaypointName.toDisplayVoxelMapWaypointName(waypoint.name);
    }

    /**
     * Server waypoints are edited by editing the server's file, so Edit does nothing for one. The button used to open server_waypoint's editor instead of VoxelMap's local one, which is exactly the client-side editing being taken away.
     */
    @Inject(
            method = "editWaypoint",
            at = @At("HEAD"),
            cancellable = true, remap = false)
    private void sw$blockServerWaypointEdit(Waypoint waypoint, CallbackInfo ci) {
        if (isVoxelMapSyncedWaypointName(waypoint.name)) {
            ci.cancel();
        }
    }

    /**
     * Delete does nothing for a server waypoint either; it is removed by editing the server's file. Cancelling here also skips the confirmation screen, so no part of the delete flow runs.
     */
    @Inject(
            method = "deleteClicked",
            at = @At("HEAD"),
            cancellable = true, remap = false)
    private void sw$blockServerWaypointDelete(CallbackInfo ci) {
        if (this.selectedWaypoint != null && isVoxelMapSyncedWaypointName(this.selectedWaypoint.name)) {
            ci.cancel();
        }
    }
}
//?}
