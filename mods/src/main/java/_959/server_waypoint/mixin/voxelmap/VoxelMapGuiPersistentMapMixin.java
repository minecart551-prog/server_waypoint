//~ gui_graphics_26
//? if voxelmap {
package _959.server_waypoint.mixin.voxelmap;

import _959.server_waypoint.common.util.SyncedWaypointName;
import com.llamalad7.mixinextras.sugar.Local;
import com.mamiyaotaru.voxelmap.gui.overridden.Popup;
import com.mamiyaotaru.voxelmap.persistent.GuiPersistentMap;
import com.mamiyaotaru.voxelmap.util.Waypoint;
import java.util.ArrayList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(value = GuiPersistentMap.class, remap = false)
public abstract class VoxelMapGuiPersistentMapMixin {
    @Unique private Waypoint sw$popupWaypoint;
    @Shadow private Waypoint selectedWaypoint;

    @ModifyArg(method = "createPopup(IIII)V", at = @At(value = "INVOKE",
            //? if >=1.21.11 {
            target = "Lcom/mamiyaotaru/voxelmap/persistent/GuiPersistentMap;createPopup(IIIIILjava/util/ArrayList;)V"), index = 5,
            //?} else {
            /*target = "Lcom/mamiyaotaru/voxelmap/persistent/GuiPersistentMap;createPopup(IIIILjava/util/ArrayList;)V"), index = 4,
            *///?}
            remap = false)
    private ArrayList<Popup.PopupEntry> sw$serverActions(ArrayList<Popup.PopupEntry> entries
            //? if <1.21.11
            /*, @Local(name = "hovered") Waypoint hovered*/
    ) {
        //? if >=1.21.11 {
        this.sw$popupWaypoint = this.selectedWaypoint;
        //?} else {
        /*this.sw$popupWaypoint = hovered;
        *///?}
        // "Add to server" and "Edit on server" are not offered at all: server
        // waypoints are only ever edited by editing the server's file. The
        // remaining Edit and Delete entries are VoxelMap's own, and they are
        // dropped here too whenever the popup was opened over a server waypoint,
        // so nothing on the map can modify one.
        // VoxelMap's popup action IDs, see GuiPersistentMap.popupAction: 4 = Edit,
        // 5 = Delete.
        if (this.sw$popupWaypoint != null
                && SyncedWaypointName.isVoxelMapSyncedWaypointName(this.sw$popupWaypoint.name)) {
            entries.removeIf(popupEntry -> popupEntry.action == 4 || popupEntry.action == 5);
        }
        return entries;
    }
}
//?}
