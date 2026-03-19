# AnvilCraft Create Addition

English | [中文](./README.md)

## Introduction

AnvilCraft Create Addition is an extension mod designed for the Minecraft Forge modding ecosystem. It provides deep integration between **AnvilCraft** and **Create**, two excellent mods, offering players new possibilities for mechanical crafting, power transmission, and magical enchantments.

## Mod Features

### Core Functionality

- **Cogwheel Amulet**: A unique item combining Create's precision mechanisms with AnvilCraft's amulet system
- **Create Mod Integration**: Deep integration with Create Mod, providing richer power and mechanical solutions
- **Amulet System Extension**: Extends AnvilCraft's amulet functionality through an amulet type system
- **Batch Crafter Unpacking**: Advanced unpacking solutions for Create's batch processing machines
- **Boiler Heater**: Additional boiler heating mechanisms
- **A new way of generating electricity**: Learn about the Faraday Disk Generator.

### Key Features

✨ **Deep Mod Integration** - Seamless fusion of AnvilCraft and Create mod features

⚙️ **Power System Extension** - New power transmission and stress management mechanisms

🛠️ **Crafting Enhancement** - More synthesis methods and crafting possibilities

🎨 **Beautiful Design** - Carefully designed item and block aesthetics

## Technical Information

### Versions

- **Mod Version**: 1.0.0
- **Minecraft Version**: 1.21.1
- **NeoForge Version**: 21.1.152+
- **Java Version**: 21+

### Core Dependencies

- **AnvilCraft**: 1.5.2+hotfix.1650
- **Create**: 6.0.6-107
- **AnvilLib**: 1.4.0+build.172
- **Flywheel**: 1.0.4-30
- **Ponder**: 1.0.56

## Installation Guide

### Requirements

1. **Minecraft**: 1.21.1
2. **NeoForge**: 21.1.152 or higher
3. **Required Mods**:
   - AnvilCraft
   - Create
   - AnvilLib (usually provided by AnvilCraft)

### Installation Steps

1. Download the latest `.jar` file
2. Place the JAR file in your `mods` folder
3. Ensure all required prerequisite mods are installed
4. Launch the game

## Configuration

The mod configuration file is located at `config/anvilcraft_create_addition-common.toml`

### Configuration Options

```toml
# The ratio of the relative linear speed of the copper block and the magnet 
# to the amount of charge generated
chargeGeneratedEfficiency = 0.72

# The mysterious coefficient, perhaps related to the stress required by the generator
stressDissipationCoefficient = 1.76
```

## Project Structure

```
AnvilCraft-Create-Addition/
├── src/main/
│   ├── java/dev/anvilcraft/addon/create/
│   │   ├── init/              # Initialization modules (items, blocks, amulets, etc.)
│   │   ├── integration/       # Integration with other mods
│   │   ├── client/            # Client-side code
│   │   ├── data/              # Data generation
│   │   └── util/              # Utility classes
│   └── resources/             # Resource files
├── gradle/                    # Gradle configuration
├── build.gradle              # Build configuration
└── dependencies.gradle       # Dependency definitions
```

## Development

### Environment Setup

This project uses **Gradle** as the build system and **NeoForge** as the mod loader.

### Building the Project

```bash
# Generate IDE configurations
./gradlew genSources

# Build the mod
./gradlew build

# Run development server
./gradlew runServer

# Run development client
./gradlew runClient
```

### Code Style

- Uses **Lombok** library to reduce boilerplate code
- Follows Java naming conventions
- Leverages Java 21 features

## License

### Code

The code in this project is licensed under [LGPL-3.0](./LICENSE) by default.

### Assets

Non-code assets (such as textures, models, etc.) are licensed under [ASSET_LICENSE](./ASSETS_LICENSE) (ARR).

## Contributing

We welcome contributions from the community! If you want to contribute code or report issues:

1. Fork this repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## FAQ

### Q: Can this mod be used standalone?
A: No, this is an extension mod and must be used together with AnvilCraft and Create.

### Q: Which versions does this mod support?
A: Currently only supports Minecraft 1.21.1 and NeoForge 21.1.152+.

### Q: How do I report a bug?
A: Please create an Issue in the GitHub repository with detailed steps to reproduce the problem.

## Related Projects

- [AnvilCraft](https://github.com/Anvil-Dev/AnvilCraft) - Core crafting and amulet system
- [Create](https://github.com/Creators-of-Create/Create) - Mechanical and power systems
- [AnvilLib](https://github.com/Anvil-Dev/AnvilLib) - Shared library

## Acknowledgments

Thanks to the following projects for inspiration and technical support:

- Create Mod team
- AnvilCraft development team
- Dubhe Studio
- Anvil development team

## Contact

- **GitHub**: [AnvilCraft-Create-Addition](https://github.com/Anvil-Dev/AnvilCraft-Create-Addition)
- **Maintainers**: Anvil-Dev Team

---

**Enjoy your game!** 🎮

