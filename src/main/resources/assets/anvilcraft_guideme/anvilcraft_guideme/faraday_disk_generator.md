---
navigation:
  title: "法拉第轮盘发电机"
  icon: "anvilcraft:charge_collector"
  position: 101
  parent: anvilcraft_guideme:create_addition.md
---

# 法拉第轮盘发电机

要使用<ItemLink id="anvilcraft:charge_collector" />，除了使用<ItemLink id="anvilcraft:piezoelectric_crystal" />，
通常还使用[铜块和磁铁的摩擦](./power/basic_power_generation.md#摩擦生产电荷)来发电。

而现在有了**机械动力**！可以使用动态结构移动其一。使得二者产生相对运动而摩擦，从而产生[电荷](./power/basic_power_generation.md#电荷)。但是这会增加维持动态结构所需的应力。

> 举个例子，如果铜块被组装为动态结构，其经过磁铁块时就会产生电荷

<GameScene zoom="3" interactive={true}>
    <ImportStructure src="./structure/faraday_disk_generator.snbt" />
</GameScene>
