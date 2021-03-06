package cn.fusionfish.core.actionbar;

import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

/**
 * @author JeremyHu
 */
@Data
public abstract class ActionBarMessage {
    private final TextComponent text;
}
