package cn.fusionfish.core.actionbar;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.kyori.adventure.text.Component;

/**
 * @author JeremyHu
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class TempActionBarMessage extends ActionBarMessage {

    private final int duration;

    public TempActionBarMessage(Component text, int duration) {
        super(text);
        this.duration = duration;
    }
}
