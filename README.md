# AnvilCraft Create Addition

[English](./README.en.md) | 中文

## 简介

AnvilCraft Create Addition 是一个为 NeoForge 模组生态系统设计的扩展模组。它将 **AnvilCraft** 和 **Create** 两个优秀的模组深度融合，为玩家提供新的机械加工、动力传输和魔法工艺的可能性。

## 模组特性

### 核心功能

- **齿轮护符（Cogwheel Amulet）**：融合了 Create 精密机械与 AnvilCraft 护符系统的独特道具
- **Create 模组集成**：与 Create Mod 的深度整合，提供更丰富的动力和机械解决方案
- **护符系统扩展**：通过护符类型系统扩展 AnvilCraft 的护符功能
- **批量加工解包**：支持 Create 批处理机器的高级解包方案
- **锅炉加热器**：提供额外的锅炉加热方案
- **全新发电方式**：法拉第圆盘发电机了解一下

### 主要特点

✨ **深度模组融合** - 无缝整合 AnvilCraft 和 Create 两个模组的优秀特性

⚙️ **动力系统扩展** - 新增的动力传输和应力管理机制

🛠️ **工艺增强** - 更多的合成方式和工艺可能性

🎨 **美观设计** - 精心设计的物品和方块外观

## 技术信息

### 版本

- **模组版本**: 1.0.0
- **Minecraft 版本**: 1.21.1
- **NeoForge 版本**: 21.1.152+
- **Java 版本**: 21+

### 核心依赖

- **AnvilCraft**: 1.5.2+hotfix.1650
- **Create**: 6.0.6-107
- **AnvilLib**: 1.4.0+build.172
- **Flywheel**: 1.0.4-30
- **Ponder**: 1.0.56

## 安装指南

### 前置要求

1. **Minecraft**: 1.21.1
2. **NeoForge**: 21.1.152 或更高版本
3. **必需模组**：
   - AnvilCraft
   - Create
   - AnvilLib（通常由 AnvilCraft 提供）

### 安装步骤

1. 下载最新的 `.jar` 文件
2. 将 JAR 文件放入你的 `mods` 文件夹
3. 确保所有必需的前置模组已安装
4. 启动游戏

## 配置

模组配置文件位于 `config/anvilcraft_create_addition-common.toml`

### 可配置选项

```toml
# 相对线速度与充能量的效率比值
chargeGeneratedEfficiency = 0.72

# 神秘系数，可能与发电机所需应力相关
stressDissipationCoefficient = 1.76
```

## 项目结构

```
AnvilCraft-Create-Addition/
├── src/main/
│   ├── java/dev/anvilcraft/addon/create/
│   │   ├── init/              # 初始化模块（物品、方块、护符等）
│   │   ├── integration/       # 与其他模组的集成
│   │   ├── client/            # 客户端代码
│   │   ├── data/              # 数据生成
│   │   └── util/              # 工具类
│   └── resources/             # 资源文件
├── gradle/                    # Gradle 配置
├── build.gradle              # 构建配置
└── dependencies.gradle       # 依赖定义
```

## 开发

### 环境配置

本项目使用 **Gradle** 构建系统，采用 **NeoForge** 作为模组加载器。

### 构建项目

```bash
# 生成 IDE 配置
./gradlew genSources

# 构建模组
./gradlew build

# 运行开发服务器
./gradlew runServer

# 运行开发客户端
./gradlew runClient
```

### 代码风格

- 使用 **Lombok** 库来减少样板代码
- 遵循 Java 命名规范
- 使用 Java 21 的特性

## 许可证

### 代码

本项目的代码默认采用 [LGPL-3.0](./LICENSE) 许可证。

### 资源

非代码资源（如纹理、模型等）采用 [ASSET_LICENSE](./ASSETS_LICENSE) 许可证（ARR）。

## 贡献

我们欢迎社区的贡献！如果你想贡献代码或报告问题：

1. Fork 本仓库
2. 创建你的功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启一个 Pull Request

## 常见问题

### Q: 这个模组可以单独使用吗？
A: 不可以，这是一个扩展模组，必须与 AnvilCraft 和 Create 一起使用。

### Q: 模组兼容哪些版本？
A: 目前仅支持 Minecraft 1.21.1 和 NeoForge 21.1.152+。

### Q: 我发现了一个 bug，怎样报告？
A: 请在 GitHub 仓库中创建一个 Issue，并详细描述问题的复现步骤。

## 相关项目

- [AnvilCraft](https://github.com/Anvil-Dev/AnvilCraft) - 核心工艺和护符系统
- [Create](https://github.com/Creators-of-Create/Create) - 机械和动力系统
- [AnvilLib](https://github.com/Anvil-Dev/AnvilLib) - 共享库

## 致谢

感谢以下项目的灵感和技术支持：

- Create Mod 团队
- AnvilCraft 开发团队
- Dubhe 工作室
- Anvil 开发团队

## 联系方式

- **GitHub**: [AnvilCraft-Create-Addition](https://github.com/Anvil-Dev/AnvilCraft-Create-Addition)
- **维护者**: Anvil-Dev 团队

---

**祝你游戏愉快！** 🎮
