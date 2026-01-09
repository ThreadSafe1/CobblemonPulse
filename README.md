# CobblemonPulse

**CobblemonPulse** is a Fabric mod for Minecraft that provides server-wide broadcast messages and notifications for Cobblemon Pokémon events, including wild spawns, captures, and defeats. It supports hover text with Pokémon details, custom sounds, and commands.

---

## Features

- Broadcast messages when Pokémon spawn, are captured, or defeated.
- Supports wild, shiny, legendary, ultrabeast and mythical Pokémon.
- Custom hover text showing Pokémon info (level, type, ability, form, nature, gender, world, biome, location).
- Optional sound effects for events.
- Custom commands triggered on events.
- Permission-based admin command for configuration reload.

---

## Installation

1. Install **Fabric Loader** and **Fabric API**.
2. Place the `CobblemonPulse` mod `.jar` into your `mods` folder.
3. Make sure **Cobblemon** mod is installed as well.
4. Start your server.

---

## Configuration

CobblemonPulse uses a JSON configuration file located at:

```
config/cobblemonpulse/config.json
```
```json
{
  "language": "en_us",
  "spawn": {
    "normal": {
      "broadcast_enabled": true,
      "broadcast_message": "&2A wild &6{pokemon} &2has appeared near &b{player}&2!",
      "pokemon_hover": [
        "&7Pokémon: &f{pokemon} lv.{level}",
        "&7Types: &a{type}",
        "&7Ability: &f{ability}",
        "&7Form: &a{form}",
        "&7Nature: &f{nature}",
        "&7Gender: &a{gender}",
        "&7World: &f{world}",
        "&7Biome: &a{biome}",
        "&7Location: &f{x} {y} {z}"
      ],
      "sound_enabled": true,
      "sound": "minecraft:entity.experience_orb.pickup",
      "volume": 1.0,
      "pitch": 1.0,
      "command_enabled": true,
      "commands": []
    },
    "shiny": { ... },
    "legendary": { ... },
    "mythical": { ... },
    "ultrabeast": { ... }
  },
  "capture": { ... },
  "defeat": { ... }
}
```

You can reload the configuration in-game using:

```
/cobblemonpulse
```

Only users with the permission `cobblemonpulse.admin` can execute this command.

---

## Default Configuration

The default configuration includes spawn, capture, and defeat settings for normal, shiny, legendary, ultrabeast and mythical Pokémon.

(Full JSON configuration omitted for brevity. Refer to the default `cobblemonpulse.json` in the mod for complete details.)

---

## Placeholders

| Placeholder | Description |
|-------------|-------------|
| `{pokemon}` | Pokémon name |
| `{player}`  | Player name |
| `{level}`   | Pokémon level |
| `{type}`    | Pokémon type(s) |
| `{ability}` | Pokémon ability |
| `{form}`    | Pokémon form |
| `{nature}`  | Pokémon nature |
| `{gender}`  | Pokémon gender |
| `{world}`   | World name |
| `{biome}`   | Biome name |
| `{x}` `{y}` `{z}` | Pokémon coordinates |

---

## Permissions

- `cobblemonpulse.admin` – Required to run `/cobblemonpulse` to reload the configuration.

---

## Events Handled

- **Spawn** – When a wild Pokémon appears.
- **Capture** – When a Pokémon is caught by a player.
- **Defeat** – When a Pokémon faints in battle.

---
