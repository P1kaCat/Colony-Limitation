
# Colony Limitation

NeoForge mod for MineColonies that limits the total number of colonies on a server.

## Features

- Global colony cap (`maxColoniesGlobal`) with server config.
- Optional dev mode to bypass the MineColonies "one colony per player" check.
- When the global cap is exceeded, only the newly created Town Hall is removed and refunded to the player.
- English and French translations included.

## Compatibility

- Minecraft: `1.21.1`
- NeoForge: `21.1.219`
- MineColonies: `1.21.1` branch/builds

## Installation

1. Place `colonylimitation-<version>.jar` in your server/client `mods` folder.
2. Ensure MineColonies is also installed.
3. Start the game/server once to generate the config file.

## Configuration

File:

- `world/serverconfig/colonylimitation-server.toml`

Options:

- `maxColoniesGlobal` (int, default: `5`)
  - `0` disables the global limit.
  - Any value `> 0` enforces the total colony cap.
- `devModeAllowMultipleColoniesPerPlayer` (boolean, default: `false`)
  - `true` bypasses MineColonies owner checks so a single player can found multiple colonies (debug/dev use).
  - `false` keeps MineColonies default behavior.

## Build

From project root:

```bash
./gradlew build
```

Output jar:

- `build/libs/colonylimitation-<version>.jar`

## Notes

- This mod enforces the global limit at colony creation time.
- If the limit is exceeded, the created colony is removed and the Town Hall block is returned to the founder (or dropped if inventory is full/offline).
