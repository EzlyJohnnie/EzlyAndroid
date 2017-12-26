package com.ezly.ezly_android.TestCase;

import com.ezly.ezly_android.Utils.TextUtils;

import org.junit.Test;
import org.w3c.dom.Text;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TextUtilsTest {
    @Test
    public void testIsValidEmail() throws Exception {
        //TODO: is this valid?
//        assertTrue(TextUtils.isValidEmailAddress("1.1@1.com.1"));
//        assertTrue(TextUtils.isValidEmailAddress("a.a+1@1.com"));

        assertTrue(TextUtils.isValidEmailAddress("1@a.com"));
        assertTrue(TextUtils.isValidEmailAddress("a@a.com"));
        assertTrue(TextUtils.isValidEmailAddress("1@1.com"));
        assertTrue(TextUtils.isValidEmailAddress("a@1.com"));

        assertTrue(TextUtils.isValidEmailAddress("1.a@a.com"));
        assertTrue(TextUtils.isValidEmailAddress("a.1@a.com"));

        assertTrue(TextUtils.isValidEmailAddress("a.a@1.com"));
        assertTrue(TextUtils.isValidEmailAddress("1.1@1.com"));
        assertTrue(TextUtils.isValidEmailAddress("1.1@1.com.nz"));

        assertTrue(TextUtils.isValidEmailAddress("1.1-1@1.com"));

        assertFalse(TextUtils.isValidEmailAddress("1.1?1@1.com"));
        assertFalse(TextUtils.isValidEmailAddress("1.1?1@1.com"));
        assertFalse(TextUtils.isValidEmailAddress("1.1@1@1.com"));
        assertFalse(TextUtils.isValidEmailAddress("aa"));
        assertFalse(TextUtils.isValidEmailAddress("11"));
        assertFalse(TextUtils.isValidEmailAddress("aa.a.1"));
        assertFalse(TextUtils.isValidEmailAddress("aa.a.1"));
    }

    @Test
    public void testCapitalize() throws Exception {
        assertEquals("A", TextUtils.capitalize("a"));
        assertEquals("1", TextUtils.capitalize("1"));
        assertEquals("$", TextUtils.capitalize("$"));

        assertEquals("Aa", TextUtils.capitalize("aa"));
        assertEquals("Aa", TextUtils.capitalize("AA"));
        assertEquals("Aa", TextUtils.capitalize("Aa"));
        assertEquals("Aa", TextUtils.capitalize("aA"));

        assertEquals("A B C", TextUtils.capitalize("a b c"));
        assertEquals("1 2 3", TextUtils.capitalize("1 2 3"));

        assertEquals("Aa Bb Cc", TextUtils.capitalize("aa bb cc"));
        assertEquals("Aa Bb Cc", TextUtils.capitalize("aA bB cC"));
        assertEquals("Aa Bb Cc", TextUtils.capitalize("Aa Bb Cc"));
        assertEquals("Aa Bb Cc", TextUtils.capitalize("AA BB CC"));

        assertEquals("A1 B2 C3", TextUtils.capitalize("a1 b2 c3"));
        assertEquals("1a 2b 3c", TextUtils.capitalize("1a 2b 3c"));

        assertEquals("?a *b $c", TextUtils.capitalize("?a *b $c"));
        assertEquals("A? B* C$", TextUtils.capitalize("a? b* c$"));
        assertEquals("?a *b $c", TextUtils.capitalize("?A *B $C"));
        assertEquals("A? B* C$", TextUtils.capitalize("A? B* C$"));

        assertEquals("A", TextUtils.capitalize(" a "));
        assertEquals("Aa", TextUtils.capitalize(" aA "));

        assertEquals("", TextUtils.capitalize(" "));
        assertEquals(null, TextUtils.capitalize(null));
    }

}
