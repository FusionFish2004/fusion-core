package cn.fusionfish.core.command;

import cn.fusionfish.core.utils.parser.ParamParser;
import cn.fusionfish.core.exception.command.CommandLackPermissionException;
import cn.fusionfish.core.exception.command.ParseException;
import cn.fusionfish.core.exception.command.WrongSenderException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author JeremyHu
 */
public abstract class BukkitCompositeCommand extends BukkitCommand {

    private final Map<SubCommandPreState, Method> methodMap = Maps.newHashMap();

    protected BukkitCompositeCommand(@NotNull String name) {
        super(name);
        loadMethodMap();
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SubCommand {
        String command();
        boolean playerCommand() default false;
        boolean adminCommand() default false;
        String permission() default "";
        String usage() default "";
    }

    @Override
    public final boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {

        //解析到子命令
        StringJoiner commandAndArgs = new StringJoiner(".");
        Arrays.stream(args).forEach(commandAndArgs::add);

        //获取候选
        Map<SubCommandPreState, String[]> candidates = Maps.newHashMap();
        methodMap.keySet().stream()
                //匹配指定数量
                //如"subcommand.method.Double.String"便无法匹配"subcommand.method.Double.String.Double"
                .filter(subCommandPreState -> subCommandPreState.getTotalLength() == args.length)
                //匹配所有以指定命令前缀开头的指令
                //如"subcommand.method.Double.String"便可匹配"subcommand.method"
                .filter(subCommandPreState -> commandAndArgs.toString().startsWith(subCommandPreState.command()))
                .forEach(subCommandPreState -> {
                    String command = subCommandPreState.command();
                    //只保留输入的参数部分，去掉子命令部分
                    StringJoiner trimmedArgsStringJoiner = new StringJoiner(".");
                    for (String arg : args) {
                        trimmedArgsStringJoiner.add(arg);
                    }

                    //删除子命令部分
                    String trimmedArgsString = trimmedArgsStringJoiner.toString().replace(command + ".", "");

                    String[] trimmedArgs = trimmedArgsString.split("\\.");

                    //数量不同
                    if (trimmedArgs.length != subCommandPreState.getArgLength()) {
                        return;
                    }

                    candidates.put(subCommandPreState, trimmedArgs);
                });

        //匹配空参数指令
        methodMap.keySet().stream()
                //获得所有空参数指令
                .filter(candidate -> {
                    Method method = methodMap.get(candidate);

                    if(method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].equals(CommandSender.class)) {
                        return false;
                    }

                    StringJoiner commandStringJoiner = new StringJoiner(".");
                    for (String arg : args) {
                        commandStringJoiner.add(arg);
                    }
                    SubCommand annotation = method.getAnnotation(SubCommand.class);
                    return annotation.command().equalsIgnoreCase(commandStringJoiner.toString());
                })
                .forEach(candidate -> candidates.put(candidate, null));


        //若匹配到的指令数量不唯一
        if (candidates.size() != 1) {
            sender.sendMessage(Component.text("无法解析命令\"" + label + "\"！")
                    .color(NamedTextColor.RED)
            );
            return true;
        }

        SubCommandPreState candidate = candidates.keySet().stream()
                .findFirst()
                .orElseThrow();
        Method method = methodMap.get(candidate);
        SubCommand annotation = method.getAnnotation(SubCommand.class);

        try {
            String[] trimmedArgs = candidates.get(candidate);
            Object[] parsedArgs = ParamParser.parseArgs(trimmedArgs, candidate.getTypes());


            //检查执行者
            if (annotation.playerCommand()) {
                if (!(sender instanceof Player player)) {
                    throw new WrongSenderException("玩家");
                }

                //管理员指令
                if (annotation.adminCommand()) {
                    if (!player.isOp()) {
                        throw new WrongSenderException("管理员");
                    }
                }

                //检查权限
                String permission = annotation.permission();
                if (permission != null && !"".equals(permission)) {
                    if (!player.hasPermission(permission)) {
                        throw new CommandLackPermissionException(permission);
                    }
                }

            }

            Object[] params;
            if (parsedArgs == null) {
                params = new Object[1];
            } else {
                params = new Object[parsedArgs.length + 1];
                //错一位存入数组
                System.arraycopy(parsedArgs, 0, params, 1, parsedArgs.length);
            }

            //将CommandSender存入数组
            params[0] = sender;

            method.invoke(this, params);
        } catch (ParseException e) {
            //无法解析参数
            sender.sendMessage(Component.text("无法解析参数\"" + e.getArg() + "\"！")
                    .color(NamedTextColor.RED)
            );

            String usage;
            if (annotation.usage() != null && !"".equals(annotation.usage())) {
                usage = annotation.usage();
            } else {
                usage = getDefaultUsage(this, method);
            }
            sender.sendMessage(Component.text(usage)
                    .color(NamedTextColor.RED)
            );

        } catch (IllegalAccessException | InvocationTargetException e) {
            //出现内部错误
            e.printStackTrace();
            sender.sendMessage(Component.text("出现内部错误！")
                    .color(NamedTextColor.RED)
            );
        } catch (CommandLackPermissionException e) {
            //缺少权限
            sender.sendMessage(Component.text("您没有权限执行该指令！\n缺少权限：" + e.getPermission())
                    .color(NamedTextColor.RED)
            );
        } catch (WrongSenderException e) {
            //执行对象错误
            sender.sendMessage(Component.text("本命令只允许" + e.getExpected() + "执行！")
                    .color(NamedTextColor.RED)
            );
        }
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        List<String> options = Lists.newArrayList();

        //添加指令补全
        options.addAll(getNextCommandOption(args));

        //TODO 添加参数补全

        return options;
    }

    private List<String> getParamsOption(String @NotNull [] args) {
        return null;
    }

    private List<String> getNextCommandOption(String @NotNull [] args) {
        StringJoiner command = new StringJoiner(".");
        for (String arg : args) {
            command.add(arg);
        }

        return methodMap.keySet().stream()
                .parallel()
                .filter(candidate -> candidate.command().startsWith(command.toString()))
                .map(candidate -> {
                    try {
                        String[] candidateArgs = candidate.command().split("\\.");
                        return candidateArgs[args.length - 1];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted(String::compareTo)
                .collect(Collectors.toList());
    }

    /**
     * 将所有包含注解的方法解析成字符串Map以便后续解析
     */
    public void loadMethodMap() {
        //获取所有包含@SubCommand的方法
        methodMap.clear();
        Arrays.stream(this.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(SubCommand.class))
                .forEach(method -> {
                    SubCommand annotation = method.getAnnotation(SubCommand.class);
                    //指令字符串，例:create.name
                    String command = annotation.command();
                    //参数字符串，例：Double.String.Integer
                    StringJoiner params = new StringJoiner(".");
                    Arrays.stream(method.getParameterTypes())
                            //排除掉CommandSender参数
                            .filter(clazz -> !clazz.equals(CommandSender.class))
                            .map(Class::getSimpleName)
                            .forEach(params::add);
                    methodMap.put(new SubCommandPreState(command, params.toString()), method);



                });

    }

}
