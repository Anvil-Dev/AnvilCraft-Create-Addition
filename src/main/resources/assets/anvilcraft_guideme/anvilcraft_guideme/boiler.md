---
navigation:
  title: "加热锅炉"
  icon: "create:steam_engine"
  position: 2
  parent: anvilcraft_guideme:create_addition.md
---

# 加热锅炉

铁砧工艺的 <ItemLink id="anvilcraft:heater" /> 和 部分[高温方块](./feature/heated_block.md) 放置在锅炉底下，可以为锅炉提供热量，具体数值如下：

- 工作的<ItemLink id="anvilcraft:heater" /> ：1级
- <NeoColor id="aa2222">红热</NeoColor>的方块：1级
- <NeoColor id="cc8844">炽热</NeoColor>的方块：2级
- <NeoColor id="ffccaa">白炽</NeoColor>的方块：3级

> 高温方块可同时为*锅炉*和<ItemLink id="anvilcraft:heat_collector" />提供能量，一热二用！

<GameScene zoom="3" interactive={true}>
    <ImportStructure src="./structure/boiler.snbt" />
</GameScene>