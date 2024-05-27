package me.unreference.core.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtil {
  private static final Pattern PATTERN_LEGACY = Pattern.compile("&[a-fA-F0-9klmnor]");
  private static final Pattern PATTERN_HEX = Pattern.compile("#[a-fA-F0-9]{6}");
  private static final Pattern PATTERN_FORMAT = Pattern.compile("&[a-fA-F0-9klmnor]|#[a-fA-F0-9]{6}");

  public static Component getFormattedComponent(Component component) {
    String plainText = PlainTextComponentSerializer.plainText().serialize(component);
    if (isOnlyFormatting(plainText)) {
      return Component.text(plainText);
    }

    TextComponent.Builder builder = Component.text();
    formatComponent(component, builder);
    return builder.build();
  }

  public static Component getHexFormattedComponent(Component component) {
    String plainText = PlainTextComponentSerializer.plainText().serialize(component);
    if (isOnlyFormatting(plainText)) {
      return Component.text(plainText);
    }

    TextComponent.Builder builder = Component.text();
    formatHexComponent(component, builder);
    return builder.build();
  }

  public static Component getLegacyFormattedComponent(Component component) {
    String plainText = PlainTextComponentSerializer.plainText().serialize(component);
    if (isOnlyFormatting(plainText)) {
      return Component.text(plainText);
    }

    TextComponent.Builder builder = Component.text();
    formatLegacyComponent(component, builder);
    return builder.build();
  }

  public static Component getFormattedComponent(String message, Object... args) {
    message = String.format(message, args);
    if (isOnlyFormatting(message)) {
      return Component.text(message);
    }

    TextComponent.Builder builder = Component.text();
    formatComponent(message, builder);
    return builder.build();
  }

  public static Component getHexFormattedComponent(String message, Object... args) {
    message = String.format(message, args);
    if (isOnlyFormatting(message)) {
      return Component.text(message);
    }

    TextComponent.Builder builder = Component.text();
    formatHexComponent(message, builder);
    return builder.build();
  }

  public static Component getLegacyFormattedComponent(String message, Object... args) {
    message = String.format(message, args);
    if (isOnlyFormatting(message)) {
      return Component.text(message);
    }

    TextComponent.Builder builder = Component.text();
    formatHexComponent(message, builder);
    return builder.build();
  }

  private static void formatComponent(Component component, TextComponent.Builder builder) {
    if (component instanceof TextComponent) {
      String content = ((TextComponent) component).content();
      int lastEnd = 0;
      Style currentStyle = Style.empty();

      Matcher matcher = PATTERN_FORMAT.matcher(content);
      while (matcher.find()) {
        builder.append(Component.text(content.substring(lastEnd, matcher.start()), currentStyle));

        String match = matcher.group();
        if (PATTERN_LEGACY.matcher(match).matches()) {
          char legacyCode = match.charAt(1);
          if (legacyCode == 'r') {
            currentStyle = Style.empty().color(NamedTextColor.WHITE);
          } else {
            NamedTextColor namedColor = getColorFromLegacy(legacyCode);
            if (namedColor != null) {
              currentStyle = currentStyle.color(namedColor);
            } else {
              TextDecoration decoration = getDecorationFromLegacy(legacyCode);
              if (decoration != null) {
                currentStyle = currentStyle.decorate(decoration);
              }
            }
          }
        } else if (PATTERN_HEX.matcher(match).matches()) {
          TextColor hexColor = TextColor.fromHexString(match);
          currentStyle = currentStyle.color(hexColor);
        }

        lastEnd = matcher.end();
      }

      builder.append(Component.text(content.substring(lastEnd), currentStyle));
    }

    for (Component child : component.children()) {
      formatComponent(child, builder);
    }
  }

  private static void formatHexComponent(Component component, TextComponent.Builder builder) {
    if (component instanceof TextComponent) {
      String content = ((TextComponent) component).content();
      int lastEnd = 0;
      Style currentStyle = Style.empty();

      Matcher matcher = PATTERN_HEX.matcher(content);
      while (matcher.find()) {
        builder.append(Component.text(content.substring(lastEnd, matcher.start()), currentStyle));

        String match = matcher.group();
        ;
        TextColor hexColor = TextColor.fromHexString(match);
        currentStyle = currentStyle.color(hexColor);

        lastEnd = matcher.end();
      }

      builder.append(Component.text(content.substring(lastEnd), currentStyle));
    }

    for (Component child : component.children()) {
      formatHexComponent(child, builder);
    }
  }

  private static void formatLegacyComponent(Component component, TextComponent.Builder builder) {
    if (component instanceof TextComponent) {
      String content = ((TextComponent) component).content();
      int lastEnd = 0;
      Style currentStyle = Style.empty();

      Matcher matcher = PATTERN_LEGACY.matcher(content);
      while (matcher.find()) {
        builder.append(Component.text(content.substring(lastEnd, matcher.start()), currentStyle));

        String match = matcher.group();
        if (PATTERN_LEGACY.matcher(match).matches()) {
          char legacyCode = match.charAt(1);
          if (legacyCode == 'r') {
            currentStyle = Style.empty().color(NamedTextColor.WHITE);
          } else {
            NamedTextColor namedColor = getColorFromLegacy(legacyCode);
            if (namedColor != null) {
              currentStyle = currentStyle.color(namedColor);
            } else {
              TextDecoration decoration = getDecorationFromLegacy(legacyCode);
              if (decoration != null) {
                currentStyle = currentStyle.decorate(decoration);
              }
            }
          }
        }

        lastEnd = matcher.end();
      }

      builder.append(Component.text(content.substring(lastEnd), currentStyle));
    }

    for (Component child : component.children()) {
      formatLegacyComponent(child, builder);
    }
  }

  private static void formatComponent(String message, TextComponent.Builder builder) {
    int lastEnd = 0;
    Style currentStyle = Style.empty();

    Matcher matcher = PATTERN_FORMAT.matcher(message);
    while (matcher.find()) {
      builder.append(Component.text(message.substring(lastEnd, matcher.start()), currentStyle));

      String match = matcher.group();
      if (PATTERN_LEGACY.matcher(match).matches()) {
        char legacyCode = match.charAt(1);
        if (legacyCode == 'r') {
          currentStyle = Style.empty().color(NamedTextColor.WHITE);
        } else {
          NamedTextColor namedColor = getColorFromLegacy(legacyCode);
          if (namedColor != null) {
            currentStyle = currentStyle.color(namedColor);
          } else {
            TextDecoration decoration = getDecorationFromLegacy(legacyCode);
            if (decoration != null) {
              currentStyle = currentStyle.decorate(decoration);
            }
          }
        }
      } else if (PATTERN_HEX.matcher(match).matches()) {
        TextColor hexColor = TextColor.fromHexString(match);
        currentStyle = currentStyle.color(hexColor);
      }

      lastEnd = matcher.end();
    }

    builder.append(Component.text(message.substring(lastEnd), currentStyle));
  }

  private static void formatHexComponent(String message, TextComponent.Builder builder) {
    int lastEnd = 0;
    Style currentStyle = Style.empty();

    Matcher matcher = PATTERN_HEX.matcher(message);
    while (matcher.find()) {
      builder.append(Component.text(message.substring(lastEnd, matcher.start()), currentStyle));

      String match = matcher.group();
      TextColor hexColor = TextColor.fromHexString(match);
      currentStyle = currentStyle.color(hexColor);

      lastEnd = matcher.end();
    }

    builder.append(Component.text(message.substring(lastEnd), currentStyle));
  }

  private static void formatLegacyComponent(String message, TextComponent.Builder builder) {
    int lastEnd = 0;
    Style currentStyle = Style.empty();

    Matcher matcher = FormatUtil.PATTERN_LEGACY.matcher(message);
    while (matcher.find()) {
      builder.append(Component.text(message.substring(lastEnd, matcher.start()), currentStyle));

      String match = matcher.group();
      char legacyCode = match.charAt(1);
      if (legacyCode == 'r') {
        currentStyle = Style.empty().color(NamedTextColor.WHITE);
      } else {
        NamedTextColor namedColor = getColorFromLegacy(legacyCode);
        if (namedColor != null) {
          currentStyle = currentStyle.color(namedColor);
        } else {
          TextDecoration decoration = getDecorationFromLegacy(legacyCode);
          if (decoration != null) {
            currentStyle = currentStyle.decorate(decoration);
          }
        }
      }

      lastEnd = matcher.end();
    }

    builder.append(Component.text(message.substring(lastEnd), currentStyle));
  }

  private static boolean isOnlyFormatting(String message) {
    String strippedMessage = PATTERN_FORMAT.matcher(message).replaceAll("").trim();
    return strippedMessage.isEmpty();
  }

  private static NamedTextColor getColorFromLegacy(char legacyCode) {
    switch (legacyCode) {
      case '0':
        return NamedTextColor.BLACK;
      case '1':
        return NamedTextColor.DARK_BLUE;
      case '2':
        return NamedTextColor.DARK_GREEN;
      case '3':
        return NamedTextColor.DARK_AQUA;
      case '4':
        return NamedTextColor.DARK_RED;
      case '5':
        return NamedTextColor.DARK_PURPLE;
      case '6':
        return NamedTextColor.GOLD;
      case '7':
        return NamedTextColor.GRAY;
      case '8':
        return NamedTextColor.DARK_GRAY;
      case '9':
        return NamedTextColor.BLUE;
      case 'a':
        return NamedTextColor.GREEN;
      case 'b':
        return NamedTextColor.AQUA;
      case 'c':
        return NamedTextColor.RED;
      case 'd':
        return NamedTextColor.LIGHT_PURPLE;
      case 'e':
        return NamedTextColor.YELLOW;
      case 'f':
        return NamedTextColor.WHITE;
      default:
        return null;
    }
  }

  private static TextDecoration getDecorationFromLegacy(char legacyCode) {
    switch (legacyCode) {
      case 'k':
        return TextDecoration.OBFUSCATED;
      case 'l':
        return TextDecoration.BOLD;
      case 'm':
        return TextDecoration.STRIKETHROUGH;
      case 'n':
        return TextDecoration.UNDERLINED;
      case 'o':
        return TextDecoration.ITALIC;
      default:
        return null;
    }
  }
}
