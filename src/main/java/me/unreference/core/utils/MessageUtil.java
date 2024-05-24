package me.unreference.core.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class MessageUtil {

    public static Component prefixedMessage(String prefix, String message, Object... args) {
        Component bodyPrefix = FormatUtil.getFormattedComponent(prefix, args).append(Component.text("> "));
        Component body = FormatUtil.getFormattedComponent(message, args);
        TextComponent.Builder builder = Component.text();

        builder.append(bodyPrefix.colorIfAbsent(NamedTextColor.BLUE));
        builder.append(body.colorIfAbsent(NamedTextColor.GRAY));
        return builder.build();
    }
}
