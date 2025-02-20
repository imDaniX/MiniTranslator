package me.imdanix.text;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.EnumSet;
import java.util.Set;

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
                        "F<color:#123456>oo <color:#654321>ba#12345r"
                }, {
                        "&@red-yellow-0&&lServer admin &9imDaniX &8> &#fff5d9&oHello world! YOLO",
                        "<gradient:red:yellow:black><b>Server admin <reset><blue>imDaniX <dark_gray>> <color:#fff5d9><i>Hello world! YOLO"
                }, {
                        "&a&lGreen bold, &cred normal",
                        "<green><b>Green bold, <reset><red>red normal"
                }, {
                        "&lBold &athen green",
                        "<b>Bold </b><green>then green"
                }, {
                        "§lJust bold",
                        "<b>Just bold</b>"
                }, {
                        "&FHOW TO TURN OFF &C&LCAPS LOCK&#123ABC?!",
                        "<white>HOW TO TURN OFF <red><b>CAPS LOCK<reset><color:#123ABC>?!",
                }, {
                        "Invalid &jcolor",
                        "Invalid &jcolor"
                }
        };
    }

    @Test(dataProvider = "toMiniData")
    public void toMiniTest(String input, String expected) {
        assertEquals(MiniTranslator.toMini(input), expected);
    }

    private static final Set<MiniTranslator.Option> VERBOSE = EnumSet.allOf(MiniTranslator.Option.class);
    static {
        VERBOSE.remove(MiniTranslator.Option.FAST_RESET);
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
                        "&@red-yellow-0&&lServer admin &9imDaniX &8> &#fff5d9&oHello world! YOLO",
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
                }
        };
    }

    @Test(dataProvider = "toMiniVerboseData")
    public void toMiniVerboseTest(String input, String expected) {
        assertEquals(MiniTranslator.toMini(input, VERBOSE), expected);
    }
}