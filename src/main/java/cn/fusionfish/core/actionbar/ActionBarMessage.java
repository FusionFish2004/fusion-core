package cn.fusionfish.core.actionbar;

import lombok.Data;
import net.kyori.adventure.text.Component;

/**
 * @author JeremyHu
 */
@Data
public abstract class ActionBarMessage {
    private final Component text;
}
