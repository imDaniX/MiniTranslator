/*
    MIT License

    Copyright (c) 2022 Daniil Z. (idanix@list.ru)

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
 */

package me.imdanix.message;

import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MiniTranslator {

    private static final Set<Option> ALL_OPTIONS = Collections.unmodifiableSet(EnumSet.allOf(Option.class));

    private static final Pattern HEX_COLOR = Pattern.compile("([\\da-f]{6})");
    private static final Pattern LEGACY_HEX_COLOR = Pattern.compile("&([\\da-f])&([\\da-f])&([\\da-f])&([\\da-f])&([\\da-f])&([\\da-f])");

    private MiniTranslator() {}

    public static String toMini(String text) {
        return toMini(text, ALL_OPTIONS);
    }

    public static String toMini(String text, Option... options) {
        return toMini(text, EnumSet.copyOf(List.of(options)));
    }

    public static String toMini(String text, Collection<Option> options) {
        List<String> closeOrder = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < text.length(); index++) {
            char ch = text.charAt(index);
            if (ch != '&') {
                builder.append(ch);
            } else  {
                if (text.length() == ++index) {
                    builder.append('&');
                    break;
                }
                ch = text.charAt(index);
                if (ch == '/') {
                    if (!options.contains(Option.END_TAGS) || text.length() == ++index) {
                        builder.append("&/");
                        continue;
                    }
                    ch = text.charAt(index);
                    if (ch == 'r' && options.contains(Option.FORMAT)) {
                        continue;
                    }
                    String tag = tagByChar(ch, options);
                    if (tag == null || handleClosing(tag, closeOrder)) {
                        builder.append("&/").append(ch);
                        continue;
                    }
                    builder.append("</").append(tag).append('>');
                } else {
                    String tag = tagByChar(ch, options);
                    if (tag == null) {
                        builder.append('&').append(ch);
                        continue;
                    }
                    switch (tag) {
                        case "color" -> {
                            if (ch == '#') {
                                if (text.length() > index + 6) {
                                    String color = text.substring(index + 1, index + 7);
                                    if (HEX_COLOR.matcher(color).matches()) {
                                        builder.append("<color:#").append(color).append('>');
                                        index += 6;
                                        closeOrder.add(tag);
                                        continue;
                                    }
                                }
                            } else {
                                if (text.length() > index + 12) {
                                    String color = text.substring(index + 1, index + 13);
                                    Matcher colorMatcher = LEGACY_HEX_COLOR.matcher(color);
                                    if (colorMatcher.matches()) {
                                        builder.append("<color:").append(colorMatcher.replaceAll("#$1$2$3$4$5$6")).append('>');
                                        index += 12;
                                        closeOrder.add(tag);
                                        continue;
                                    }
                                }
                            }
                            builder.append('&').append(ch);
                        }
                        case "gradient" -> {
                            int endIndex = -1;
                            for (int inner = index + 1; inner < text.length(); inner++) {
                                char inCh = text.charAt(inner);
                                if (inCh == '&') {
                                    endIndex = inner;
                                    break;
                                } else if (!(('a' <= inCh && inCh <= 'z') || ('0' <= inCh && inCh <= '9') || inCh == '#' || inCh == '-')) {
                                    break;
                                }
                            }
                            String[] split;
                            if (endIndex == -1 || (split = text.substring(index + 1, endIndex).split("-")).length == 1) {
                                builder.append("&@");
                                continue;
                            }
                            List<String> colors = new ArrayList<>(split.length);
                            for (String color : split) {
                                if (color.length() == 1) {
                                    color = colorByChar(color.charAt(0));
                                    if (color == null) break;
                                } else if (color.startsWith("#")) {
                                    if (!HEX_COLOR.matcher(color.substring(1)).matches()) {
                                        break;
                                    }
                                } else if (NamedTextColor.NAMES.value(color) == null) {
                                    break;
                                }
                                colors.add(color);
                            }
                            if (colors.size() == split.length) {
                                index = endIndex;
                                builder.append("<gradient:").append(String.join(":", colors)).append('>');
                                closeOrder.add(tag);
                            }
                        }
                        case "reset" -> {
                            closeOrder.clear();
                            builder.append("<reset>");
                        }
                        default -> {
                            closeOrder.add(tag);
                            builder.append('<').append(tag).append('>');
                        }
                    }
                }
            }
        }
        return builder.toString();
    }

    private static boolean handleClosing(String tag, List<String> order) {
        int index = order.lastIndexOf(tag);
        if (index == -1) {
            return true;
        }
        order.subList(index, order.size()).clear();
        return false;
    }

    private static String colorByChar(char ch) {
        return switch (ch) {
            case '0' -> "black";
            case '1' -> "dark_blue";
            case '2' -> "dark_green";
            case '3' -> "dark_aqua";
            case '4' -> "dark_red";
            case '5' -> "dark_purple";
            case '6' -> "gold";
            case '7' -> "gray";
            case '8' -> "dark_gray";
            case '9' -> "blue";
            case 'a' -> "green";
            case 'b' -> "aqua";
            case 'c' -> "red";
            case 'd' -> "light_purple";
            case 'e' -> "yellow";
            case 'f' -> "white";

            default -> null;
        };
    }

    public static String tagByChar(char ch, Collection<Option> options) {
        if (('0' <= ch && ch <= '9') || ('a' <= ch && ch <= 'f') || ch == '#' || ch == 'x') {
            if (!options.contains(Option.COLOR)) return null;
            return switch (ch) {
                case 'x', '#' -> "color";
                default -> colorByChar(ch);
            };
        } else if (('k' <= ch && ch <= 'o') || ch == 'r') {
            if (!options.contains(Option.FORMAT)) return null;
            return switch (ch) {
                case 'r' -> "reset";
                case 'l' -> "b";
                case 'n' -> "u";
                case 'm' -> "st";
                case 'o' -> "i";
                case 'k' -> "obf";

                default -> null;
            };
        } else if (ch == '@' && options.contains(Option.GRADIENT)) {
            return "gradient";
        }
        return null;
    }

    public enum Option {
        COLOR, FORMAT, GRADIENT, END_TAGS
    }
}
