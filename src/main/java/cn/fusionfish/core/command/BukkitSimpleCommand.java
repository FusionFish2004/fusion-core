package cn.fusionfish.core.command;

import cn.fusionfish.core.command.parser.Parser;
import cn.fusionfish.core.exception.command.CommandLackPermissionException;
import cn.fusionfish.core.exception.command.ParseException;
import cn.fusionfish.core.exception.command.WrongSenderException;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author JeremyHu
 */
public abstract class BukkitSimpleCommand extends BukkitCommand {
    protected BukkitSimpleCommand(@NotNull String name) {
        super(name);
    }

    @Override
    public final boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        List<Method> candidates = Arrays.stream(this.getClass().getDeclaredMethods())
                //获取含有指定注解的所有方法
                .filter(method -> method.isAnnotationPresent(Invoke.class))
                .toList();
        if (candidates.size() != 1) {
            //获取到的方法不唯一
            sender.sendMessage(Component.text("无法解析命令\"" + label + "\"！")
                    .color(NamedTextColor.RED)
            );
            return true;
        }

        Method method = candidates.get(0);
        Invoke annotation = method.getAnnotation(Invoke.class);
        try {

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

            Class<?>[] paramTypes = Arrays.stream(method.getParameterTypes())
                    .filter(clazz -> !clazz.equals(CommandSender.class))
                    .toArray(Class<?>[]::new);
            Object[] parsedArgs = Parser.parseArgs(args, paramTypes);

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

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Invoke {
        boolean playerCommand() default false;
        boolean adminCommand() default false;
        String permission() default "";
        String usage() default "";
    }
}
