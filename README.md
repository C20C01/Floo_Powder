# Floo Powder

## **A Minecraft Mod for "Forge-1.18.2"**

This Mod adds Floo Powder and other interesting things to enable you to set up your own portal points. You can teleport
not
only yourself but also other kinds of entity which you specify to these points from anywhere.

##  Jump around

* [Attention](#attention)
* [Learn More](#learn-more)
* [Download](#download)
* [TODO](#TODO)
* [Update Description](#update-description)

## Attention

* This Mod **IS STILL** on programming so the old and new versions may be [**incompatible**](#incompatible).
* Sorry for my limited English that there may be some mistakes or inadequate expression.

## Learn More

The information in the following pages may not be the lasted.

* [CurseForge](https://www.curseforge.com/minecraft/mc-mods/floo-powder) (Introduction in English by translation
  software)
* [McMod](https://www.mcmod.cn/class/6502.html) (Introduction in Chinese)
* [~~My Website~~]()(Coming soon)

## DownLoad

You can find most versions on [Releases Page](https://github.com/C20C01/Floo_Powder/releases). The Mod file should be
named like "FlooPowder-x.x.x.jar".

### **Incompatible**

The version 1.4.5(Jul 28, 2022) and earlier versions are **incompatible** with subsequent versions ! ! !

## TODO

* 接着添东西
* 添加指令系统
* 添加加好友的途径
* 添加合成表
* 写说明
* 国际化
* 新版本发布

## Update Description

The following is the latest version.

***

* Version 1.5.0(beta) :
    * Beta Version ! !
    * Change the ModID from "cc2001_floo_power" to "cc_fp" ! !
    * 将 ModID 从 “cc2001_floo_powder” 变为 “cc_fp” ! !
    * 重写并完善了传送点信息的读取与保存的相关部分（遂无法正常读取先前版本的传送点信息）! !
    * 注释掉了软件包“saveData”与“pos”内的文件，后续版本再进行删除
    * 注释掉了软件包“command”内的文件，后续版本再进行重写
    * 扩充了传送点信息的内容（见“PortalPoint”）
    * 添加了玩家与传送点间的权限信息（见“Permission”）
    * 重写并完善了传送的相关部分（见“TpTool”）
        * Learned a lot from another Mod: "WayStone"
        * 添加了“ServerTick”——实现传送与主循环同步
        * 添加了传送后的朝向与动量的设置
    * 添加了很多说明文档
    * 重写并完善了粒子效果的相关部分（见软件包“particle”）
    * 添加了两种粒子效果（见软件包“particles”）
    * 将客户端的初始化逻辑集中到“Setup”
    * 修改了飞路粉分发器中获取的传送点信息的逻辑，现在只会现实此玩家有权前往的所有传送点
    * 修改了传送核心添的使用方法
    * 添加了传送核心的GUI、不同状态下的材质
    * 添加了新物品——传送法杖（见“PortalWand”）
    * 修改了传送火石的使用逻辑以配合传送核心新的使用方法
    * 修改了传送点大全的功能
    * 添加了新物品——传送卷轴（见“FlooReel”）
    * 添加了新物品——拓展卷轴*10种（见“ExpansionReel”）
    * 添加了新物品——不灭粉（暂译）（见“LastingPowder”）
    * 添加了新方块——弹性火焰底座（见“FireBaseBlock”）
    * 添加了新方块——传送陷阱箱（见软件包“portalChest”）
    * 修改了传送核心的合成表
    * 整合了不必要的网络通道

  更多内容请到游戏内体验。

***
See more on [Commits Page](https://github.com/C20C01/Floo_Powder/commits).