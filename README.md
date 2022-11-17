# Tinkered

![](https://github.com/Lylythii/Tinkered/blob/1.12/resources/logo.png)

[Tinkered](https://curseforge.com/minecraft/mc-mods/tinkered) is a fork of [Binkers' Bonstruct]](https://curseforge.com/minecraft/mc-mods/binkers-bonstruct), which in itself is a fork of [Tinkers' Construct](https://curseforge.com/minecraft/mc-mods/tinkers-construct), aiming to expand upon the mod TerraFirmaCraft.

**List of changes:**
* Configurable ingot/block/nugget/... values
* Master switches for enabling/disabling melting and casting of respective types
* Configurable clear glass breaking behavior (silk touch)
* Mattocks are full-featured axes and shovels in one tool

*More will come as I see fit...*

**Notice: The Tinkers' Construct development team takes no responsibility for user support queries concerning this fork.**

### IMC
Tinkers' Construct supports several IMCs to allow mods to integrate themselves. The [Wiki](https://github.com/SlimeKnights/TinkersConstruct/wiki/IMC) contains a page with further information.
Anything that is not possible via IMC has to be integrated via Code through the API/library package.

## Setting up a Workspace/Compiling from Source
Note: Git MUST be installed and in the system path to use our scripts.
* Setup: Run [gradle]in the repository root: `gradlew[.bat] [setupDevWorkspace|setupDecompWorkspace] [eclipse|idea]`
* Build: Run [gradle]in the repository root: `gradlew[.bat] build`
* If obscure Gradle issues are found try running `gradlew clean` and `gradlew cleanCache`

## Licenses
Code, Textures and binaries are licensed under the [MIT License](https://tldrlegal.com/license/mit-license).
