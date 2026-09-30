# bendy-lib (NeoForge 1.21.1 fork)

Локальный форк [KosmX/bendy-lib](https://github.com/KosmX/bendy-lib) для **Minecraft 1.21.1 / NeoForge 21.1**.

Основа — ветка `1.21` (мод уже на Mojmap и NeoForge, но собран под 1.21.4). Совместимость с 3D Skin Layers перенесена с ветки `3d_layer_compat` на API Skin Layers 1.11.x (`skinlayers3d`). Ветка `dev` старше и не использовалась как база.

Мод клиентский, id `bendylib`. 3D Skin Layers не обязателен: если мода нет, библиотека просто грузится без этого хука. Сборка NeoForge:

```
gradlew :forge:build
```

Готовый jar: `forge/build/libs/bendy-lib-5.1.1+1.21.1.jar`.

Для компиляции хука рядом должен лежать `libs/skinlayers3d-neoforge-1.11.3-mc1.21.1.jar` (в jar мода он не упаковывается).

Лицензия исходников KosmX: CC-BY-4.0.

# bendy-lib
FabricMC library

setup with gradle:

```groovy
dependencies {
    (...)
    repositories {
        mavenCentral()
        (...)
    }
    (...) 
    modImplementation "io.github.kosmx:bendy-lib:${project.bendylib_version}"
    include "io.github.kosmx:bendy-lib:${project.bendylib_version}"
    //you can find the latest version in GitHub packages
}
```

designed to be able to swap and bend cuboids.

The api provides a way to swap a cuboid with priorities, to be multi-mod compatible
(bend like in Mo'bends)

to swap, you have to create a class from MutableModelPart, and implement the methods.

You don't have to use existing bendableCuboid objects, you can create your own, BUT it's highly recommend (it's a lot's of work to code a bendable stuff)

The test mod (an older and modified version of [Emotecraft](https://github.com/kosmx/emotes))

Sorry for this not finished documentation...
You can find me on the Fabric discord server and on the Emotecraft discord server

and an example image:D  
![example](https://raw.githubusercontent.com/KosmX/bendy-lib/dev/example.png)  
  
The release branch contains the source of the latest release.
