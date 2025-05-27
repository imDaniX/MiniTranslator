package me.imdanix.text;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand;
import static org.testng.Assert.assertEquals;

public class MiniTranslatorTest {
    @DataProvider
    public Object[][] toMiniData() {
        return new Object[][] {
                {
                        "&aA simple one",
                        "<green>A simple one"
                }, {
                        "F&#123456oo &x&6&5&4&3&2&1ba#12345r",
                        "F<#123456>oo <#654321>ba#12345r"
                }, {
                        "&@red-yellow-0@&lServer admin &9imDaniX &8> &#fff5d9&oHello world! YOLO",
                        "<gradient:red:yellow:black><b>Server admin <reset><blue>imDaniX <dark_gray>> <#fff5d9><i>Hello world! YOLO"
                }, {
                        "&a&lGreen bold, &cred normal",
                        "<green><b>Green bold, <reset><red>red normal"
                }, {
                        "&lBold &athen green",
                        "<b>Bold <green>then green"
                }, {
                        "§lJust bold",
                        "<b>Just bold"
                }, {
                        "&FHOW TO TURN OFF &C&LCAPS LOCK&#123ABC?!",
                        "<white>HOW TO TURN OFF <red><b>CAPS LOCK<reset><#123ABC>?!",
                }, {
                        "Invalid &jcolor",
                        "Invalid &jcolor"
                }, {
                        "&a&lStart from the&r scratch",
                        "<green><b>Start from the<reset> scratch"
                }, {
                        "&eYellow, &&astill yellow",
                        "<yellow>Yellow, &astill yellow"
                }
        };
    }

    @Test(dataProvider = "toMiniData")
    public void toMiniTest(String input, String expected) {
        assertEquals(MiniTranslator.toMini(input), expected);
    }

    @DataProvider
    public Object[][] toMiniStandaloneData() {
        return new Object[][] {
                {
                        "Player <#ff0000>imDaniX has joined the game. Their rank is #00ff00VIP. &#fff5d9Status: <color:#123456>Active",
                        "Player <#ff0000>imDaniX has joined the game. Their rank is <color:#00ff00>VIP. <color:#fff5d9>Status: <color:#123456>Active"
                }, {
                        "<color:#123456>Replace <color:#123456this <gradient:#123456:#123456>and :#123456:this, <#123456>ok? #123456>!!",
                        "<color:#123456>Replace <color:<color:#123456>this <gradient:#123456:#123456>and :#123456:this, <#123456>ok? <color:#123456>>!!",
                }, {
                        "#123456Edges#654321",
                        "<color:#123456>Edges<color:#654321>"
                }, {
                        "Should be parsed &&#123456anyway",
                        "Should be parsed &<color:#123456>anyway"
                }
        };
    }

    private static final Set<MiniTranslator.Option> STANDALONE = EnumSet.allOf(MiniTranslator.Option.class);
    static {
        STANDALONE.remove(MiniTranslator.Option.CLOSE_COLORS);
    }

    @Test(dataProvider = "toMiniStandaloneData")
    public void toMiniStandaloneTest(String input, String expected) {
        assertEquals(MiniTranslator.toMini(input, STANDALONE), expected);
    }

    private static final Set<MiniTranslator.Option> VERBOSE = EnumSet.allOf(MiniTranslator.Option.class);
    static {
        VERBOSE.remove(MiniTranslator.Option.FAST_RESET);
        VERBOSE.remove(MiniTranslator.Option.HEX_COLOR_STANDALONE);
    }

    @DataProvider
    public Object[][] toMiniVerboseData() {
        return new Object[][] {
                {
                        "&aA simple one",
                        "<green>A simple one</green>"
                }, {
                        "F&#123456oo &x&6&5&4&3&2&1ba#12345r",
                        "F<color:#123456>oo </color:#123456><color:#654321>ba#12345r</color:#654321>"
                }, {
                        "&@red-yellow-0@&lServer admin &9imDaniX &8> &#fff5d9&oHello world! YOLO",
                        "<gradient:red:yellow:black><b>Server admin </b></gradient><blue>imDaniX </blue><dark_gray>> </dark_gray><color:#fff5d9><i>Hello world! YOLO</i></color:#fff5d9>"
                }, {
                        "&a&lGreen bold, &cred normal",
                        "<green><b>Green bold, </b></green><red>red normal</red>"
                }, {
                        "&lBold &athen green",
                        "<b>Bold </b><green>then green</green>"
                }, {
                        "§lJust bold",
                        "<b>Just bold</b>"
                }, {
                        "&FHOW TO TURN OFF &C&LCAPS LOCK&#123ABC?!",
                        "<white>HOW TO TURN OFF </white><red><b>CAPS LOCK</b></red><color:#123ABC>?!</color:#123ABC>",
                }, {
                        "Invalid &jcolor",
                        "Invalid &jcolor"
                }, {
                        "&a&lStart from the&r scratch",
                        "<green><b>Start from the<reset> scratch"
                }, {
                        "&eYellow, &&astill yellow",
                        "<yellow>Yellow, &astill yellow</yellow>"
                }
        };
    }

    @Test(dataProvider = "toMiniVerboseData")
    public void toMiniVerboseTest(String input, String expected) {
        assertEquals(MiniTranslator.toMini(input, VERBOSE), expected);
    }

    private static final String SYMBOLS = "&@-02468#abcdef";
    private static final Set<MiniTranslator.Option> VERBOSE_STANDALONE = EnumSet.copyOf(VERBOSE);
    static {
        VERBOSE_STANDALONE.add(MiniTranslator.Option.HEX_COLOR_STANDALONE);
    }

    @Test(description = "Basically hoping that we'll get no exceptions while parsing a hot stinky mess")
    public void randomTest() {
        RandomGenerator rng = ThreadLocalRandom.current();
        for (int i = 0; i < 1024; i++) {
            StringBuilder builder = new StringBuilder(256);
            for (int j = 0; j < 512; j++) {
                builder.append(SYMBOLS.charAt(rng.nextInt(SYMBOLS.length())));
            }
            try {
                MiniTranslator.toMini(builder.toString(), VERBOSE_STANDALONE);
            } catch (Throwable throwable) {
                System.out.println(builder);
                throw throwable;
            }
        }
    }
    @DataProvider
    public Object[][] performanceData() {
        return new Object[][] {
                {
                        "&aA simple one"
                }, {
                        "F&#123456oo &x&6&5&4&3&2&1ba#12345r"
                }, {
                        "&c&lServer admin &9imDaniX &8> &#fff5d9&oHello world! YOLO",
                }, {
                        "&a&lGreen bold, &cred normal",
                }, {
                        "&lBold &athen green",
                }, {
                        "&FHOW TO TURN OFF &C&LCAPS LOCK&#123ABC?!",
                }, {
                        "Invalid &jcolor",
                }, {
                        "&a&lStart from the&r scratch",
                }
        };
    }

    @Test(
            dataProvider = "performanceData",
            description = "The \"test\" exists purely for getting a *rough* idea of performance",
            enabled = false
    )
    public void performanceTest(String text) { // TODO Proper JMH
        int warmup = 100000;
        int test = 10000;

        var legacyAmpersand = legacyAmpersand();
        var miniMessage = miniMessage();

        for (int i = 0; i < warmup; i++) {
            MiniTranslator.toMini(text);
            miniMessage.serialize(legacyAmpersand.deserialize(text));
        }

        long start, end;

        start = System.nanoTime();
        for (int i = 0; i < test; i++) {
            MiniTranslator.toMini(text);
        }
        end = System.nanoTime();
        System.out.println((end - start) / test);

        start = System.nanoTime();
        for (int i = 0; i < test; i++) {
            miniMessage.serialize(legacyAmpersand.deserialize(text));
        }
        end = System.nanoTime();
        System.out.println((end - start) / test);

        System.out.println("Translator: " + MiniTranslator.toMini(text));
        System.out.println("<reset>");
        System.out.println("Serializers: " + miniMessage.serialize(legacyAmpersand.deserialize(text)));
    }
}