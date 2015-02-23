/*
 *  Copyright (C) 2005 Johannes Heinonen <johannes.heinonen@iki.fi>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package codegen.test;

import junit.framework.TestCase;
import java.util.Random;

import codegen.Generator;

/**
 * Unit tests for the password generator.
 * @author Johannes Heinonen <johannes.heinonen@iki.fi>
 */
public class GeneratorTest extends TestCase {

    private static final String[] TRIM_DATA = {
            "foox", "fox",
            " a b c b a ", "abc",
            "", "",
            "666", "6",
            null, null
    };
    
    public void testTrimDuplicates() {
        for (int i = 0; i + 1 < TRIM_DATA.length; i+=2) {
            assertEquals(Generator.trimWhitespaceAndDuplicates(TRIM_DATA[i]), TRIM_DATA[i+1]);
        }        
    }
    
    public void testPow() {
        assertEquals(Generator.pow(2, 4), 16);
        assertEquals(Generator.pow(2, 0), 1);
        assertEquals(Generator.pow(1, 40), 1);
        assertEquals(Generator.pow(1000, 1000), Long.MAX_VALUE);
        assertEquals(Generator.pow(-10, 10), Long.MIN_VALUE);
        assertEquals(Generator.pow(10, -10), Long.MIN_VALUE);
    }
    
    public void testGenerateSingleCharacterDistribution() {
        String text = "1234567890";
        int rounds = 1000*1000;
        float margin = 0.01f;
        
        Random r = new Random();
        char[] chars = text.toCharArray();
        int[] hits = new int[text.length()];
        for (int i = 0; i < rounds; i++) {
            int val = text.indexOf(Generator.generate(r, chars, 1));
            assertTrue(val >= 0 && val < chars.length);
            hits[val]++;
        }        
        float lowerBound = 1.0f - margin;
        float upperBound = 1.0f + margin;
        for (int i = 0; i < chars.length; i++) {
            float share = 1.0f * hits[i] / rounds;
            float ratio = share / (1.0f / chars.length);
            assertTrue(ratio >= lowerBound && ratio <= upperBound);
        }
    }
    
    public void testGenerateMultiTrivialCases() {
        assertNull(Generator.generateMulti(null, 5, 5, true)); // no charset
        assertNull(Generator.generateMulti("abc", 0, 5, true)); // zero length
        assertNull(Generator.generateMulti("abc", 5, 0, true)); // zero count
        
        assertNull(Generator.generateMulti("a", 3, 50, true)); // unique ids exhausted
        assertEquals(Generator.generateMulti("a", 3, 50, false).length, 50); // non-unique 
    }    
    
    public void testGenerateMultiUniqueness() {
        String text = "1234567890";
        int length = 4;        
        
        long maxCount = Generator.pow(text.length(), length);
        assertTrue(maxCount != Long.MAX_VALUE);
        assertTrue(maxCount <= (long) Integer.MAX_VALUE);

        String[] s = Generator.generateMulti(text, length, (int)maxCount, true);        
        assertNotNull(s);
        assertNull(Generator.generateMulti(text, length, (int)maxCount + 1, true)); // unique, shoud fail
        assertNotNull(Generator.generateMulti(text, length, (int)maxCount + 1, false)); // not unique, must succeed
        
        boolean ok = true;
        for (int i = 0; i < maxCount; i++) {
            for (int k = i + 1; k < maxCount; k++) {
                ok &= s[i].compareTo(s[k]) != 0;
            }
        }
        assertTrue(ok);
    }
}
