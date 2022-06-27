package cn.fusionfish.core.manager;

import cn.fusionfish.core.plugin.FusionPlugin;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author JeremyHu
 */
@Log4j2
public class ManagerManager implements Manager {

    private final Map<Class<? extends Manager>, Manager> managerMap = Maps.newHashMap();

    public ManagerManager() {

    }

    public void registerManagers(@NotNull FusionPlugin plugin) {
        Reflections reflections = plugin.getReflections();
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(AutoRegisterManager.class);

        classes.stream()
                .parallel()
                .map(clazz -> {
                    try {
                        Constructor<?> constructor = clazz.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        return (Manager) constructor.newInstance();
                    } catch (Exception e) {
                        String className = clazz.getName();
                        log.warn(className + "不是一个有效的Manager类.");
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(this::registerManager);
    }

    public Set<Manager> getManagers() {
        return Sets.newHashSet(managerMap.values());
    }

    public void registerManager(Manager manager) {
        managerMap.put(manager.getClass(), manager);
    }


    @SuppressWarnings("unchecked")
    public @Nullable <T extends Manager> T getManager(Class<T> clazz) {
        Manager manager = managerMap.get(clazz);
        if (manager == null) {
            return null;
        }

        return (T) manager;
    }
}
