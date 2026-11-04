# GeyserGardens

> **This is a fork of [GeyserMC/Geyser](https://github.com/GeyserMC/Geyser), maintained by [@BrightSteel](https://github.com/BrightSteel) for the [GardensMC](https://gardensmc.com) server.** It lets Bedrock Edition players see and use the server's custom blocks, furniture and items, which come from [GardensFurniture](https://github.com/BrightSteel/gardens-furniture). The fork tracks upstream closely and merges new Geyser releases regularly. The original Geyser README follows below.

## What this fork adds

**Custom block and item support for Bedrock players**
- Renders GardensFurniture custom blocks and items on Bedrock, including held blocks, icon-only blocks and dyeable items ([5ffcc54](../../commit/5ffcc545), [319894f](../../commit/319894fd), [481ff19](../../commit/481ff19f), [fc3c0d3](../../commit/fc3c0d3c))
- Adds culling geometry support for custom blocks ([17065dc](../../commit/17065dcd))
- Fixes custom blocks reverting to vanilla after section block updates ([ce0827f](../../commit/ce0827fb))
- Translates tag badge glyphs to emoji in chat and item text ([a22dfda](../../commit/a22dfdaa))
- Points the mappings submodule at a [fork with the server's custom biomes](https://github.com/BrightSteel/mappings) ([c731f1a](../../commit/c731f1ac))

**Keeping the fork easy to maintain**
- Refactored all fork changes into a thin-shim pattern, so the Gardens-specific code lives in a few small hooks and upstream merges stay low-conflict ([e88f7f3](../../commit/e88f7f39))

**Automated deploys**
- Added a GitHub Actions workflow ([gardens-deploy.yml](.github/workflows/gardens-deploy.yml)) that builds Geyser-Spigot on Java 25 and uploads it over SFTP ([eabcdba](../../commit/eabcdba9), [32026ec](../../commit/32026ec8))
- The deploy writes into `plugins/update` instead of over the live jar, so the server picks up the new build on its next restart instead of being swapped mid-run ([92b7d16](../../commit/92b7d160))

---

<img src="https://geysermc.org/img/geyser-1760-860.png" alt="Geyser" width="600"/>

[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Discord](https://img.shields.io/discord/613163671870242838.svg?color=%237289da&label=discord)](https://discord.gg/geysermc)
[![Crowdin](https://badges.crowdin.net/e/51361b7f8a01644a238d0fe8f3bddc62/localized.svg)](https://translate.geysermc.org/)

Geyser is a bridge between Minecraft: Bedrock Edition and Minecraft: Java Edition, closing the gap from those wanting to play true cross-platform.

Geyser is an [Open Collaboration](https://opencollaboration.dev/) project.

## What is Geyser?
Geyser is a proxy, bridging the gap between Minecraft: Bedrock Edition and Minecraft: Java Edition servers.
The ultimate goal of this project is to allow Minecraft: Bedrock Edition users to join Minecraft: Java Edition servers as seamlessly as possible. However, due to the nature of Geyser translating packets over the network of two different games, *do not expect everything to work perfectly!*

Special thanks to the DragonProxy project for being a trailblazer in protocol translation and for all the team members who have joined us here!

## Supported Versions

| Edition | Supported Versions                                                                                |
|---------|---------------------------------------------------------------------------------------------------|
| Bedrock | 26.40, 26.41, 26.42, 26.43, 26.44, 26.45, 26.50, 26.51, 26.52, 26.60                              |
| Java    | 26.3 (For older versions, [see this guide](https://geysermc.org/wiki/geyser/supported-versions/)) |

## Setting Up
Take a look [here](https://geysermc.org/wiki/geyser/setup/) for how to set up Geyser.

## Links:
- Website: https://geysermc.org
- Docs: https://geysermc.org/wiki/geyser/
- Download: https://geysermc.org/download
- Discord: https://discord.gg/geysermc
- Donate: https://opencollective.com/geysermc
- Test Server: `test.geysermc.org` port `25565` for Java and `19132` for Bedrock

## What's Left to be Added/Fixed
- Near-perfect movement (to the point where anticheat on large servers is unlikely to ban you)
- Some Entity Flags

## What can't be fixed
There are a few things Geyser is unable to support due to various differences between Minecraft Bedrock and Java. For a list of these limitations, see the [Current Limitations](https://geysermc.org/wiki/geyser/current-limitations/) page.

## Compiling
1. Clone the repo to your computer
2. Navigate to the Geyser root directory and run `git submodule update --init --recursive`. This command downloads all the needed submodules for Geyser and is a crucial step in this process.
3. Run `gradlew build` and locate to `bootstrap/build` folder.

## Contributing
Any contributions are appreciated. Please feel free to reach out to us on [Discord](https://discord.gg/geysermc) if
you're interested in helping out with Geyser.

## Libraries Used:
- [Adventure Text Library](https://github.com/KyoriPowered/adventure)
- [CloudburstMC Bedrock Protocol Library](https://github.com/CloudburstMC/Protocol)
- [GeyserMC's Java Protocol Library](https://github.com/GeyserMC/MCProtocolLib)
- [TerminalConsoleAppender](https://github.com/Minecrell/TerminalConsoleAppender)
- [Simple Logging Facade for Java (slf4j)](https://github.com/qos-ch/slf4j)
