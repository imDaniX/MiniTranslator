package me.imdanix.message;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class MiniTranslatorTest {
    @DataProvider
    public Object[][] textExamples() {
        return new Object[][] {
                {"&cFoo&/c b&aar", "<red>Foo</red> b<green>ar"},
                {"&lFoo&/l &/lbar", "<b>Foo</b> &/lbar"},
                {"F&#123456oo &x&6&5&4&3&2&1ba#12345r", "F<color:#123456>oo <color:#654321>ba#12345r"},
                {
                        "&@red-yellow-0&&lServer&r admin &9imDaniX &8> &#fff5d9&oHello world!&/# YOLO",
                        "<gradient:red:yellow:black><b>Server<reset> admin <blue>imDaniX <dark_gray>> <color:#fff5d9><i>Hello world!</color> YOLO"
                }
        };
    }

    @Test(dataProvider = "textExamples")
    public void testToMini(String input, String expected) {
        assertEquals(MiniTranslator.toMini(input), expected);
    }
}