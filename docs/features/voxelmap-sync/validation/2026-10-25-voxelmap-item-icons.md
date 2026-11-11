# VoxelMap item icons — 2026-10-25

Server Waypoint icons that name a game item now appear on VoxelMap's maps on 1.20.1
Fabric. Before this change only the packaged `voxelmap:<suffix>` images crossed into
VoxelMap; an item icon such as `minecraft:diamond_sword` synced with the empty image
suffix and displayed VoxelMap's ordinary waypoint image on both the minimap and the
full map.

VoxelMap stores a waypoint icon as an image suffix resolved against its own texture
atlas, and it already understands item-backed icons: `VoxelMap` decodes the
`server_waypoint_item/<id>` suffix (see `ItemWaypointIcon` on the VoxelMap side) into
`Waypoint.itemIconId` and renders the item itself through `ItemWaypointRenderer`, on both
the full map and the minimap. The placement maths, hover radius, disabled dimming and the
old-north counter-rotation all live inside VoxelMap, so no mixin is needed at all: Server
Waypoint simply sends the item-suffixed name (`VoxelMapIconIds.toSuffix`) and VoxelMap does
the rest.

## Gating

No gating is required: the item suffix is plain data over the existing VoxelMap waypoint
sync, so every voxelmap-pinned target that receives the suffixed name renders item icons
natively and keeps the previous default-image behaviour when the suffix is empty.

## Verification

| Check | Result |
| --- | --- |
| `./gradlew :mods:1.20.1-fabric:compileJava` | passed |
| `./gradlew :mods:1.20.1-fabric:test` | 933 tests, 0 failures, 0 errors |
| `VoxelMapIconIdsTest` | still passes: plain suffixes and `server_waypoint_item/<id>` both round-trip |

## Remaining check

In-game verification on 1.20.1 Fabric with VoxelMap installed is still outstanding. It
needs a waypoint whose Server Waypoint icon is an item, then a visual check that the
item icon appears on the HUD minimap and on the full map, that a VoxelMap-image icon and
an icon-less waypoint still show VoxelMap's own images, that a disabled waypoint is
faded, and that the old-north option leaves the item upright.
