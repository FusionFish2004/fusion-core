# FusionCore

FusionCore是本人为了简化bukkit插件开发过程从而开发的一款前置插件。

## 功能

通过[Shadow](https://github.com/johnrengelman/shadow)插件打包了[Reflections](https://github.com/ronmamo/reflections)类库，从而实现了如下功能：

+ 插件启动时自动创建配置文件/插件文件夹/数据库本地db文件
+ 多个以FusionCore作为前置插件的插件可以共享一个SQLite数据库
+ 插件启动时自动注册含有[@FusionCommand](https://gitee.com/fusionfish/fusion-core/tree/master/src/main/java/cn/fusionfish/core/annotations/FusionCommand.java)注解的指令
+ 插件启动时自动注册含有[@FusionListener](https://gitee.com/fusionfish/fusion-core/tree/master/src/main/java/cn/fusionfish/core/annotations/FusionListener.java)注解的监听器
+ 插件启动时自动创建含有[@FusionHandler](https://gitee.com/fusionfish/fusion-core/tree/master/src/main/java/cn/fusionfish/core/annotations/FusionHandler.java)注解的Http对象

