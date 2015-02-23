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

package codegen;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Password generator. Utilizes the JDK's standard <code>java.util.Random</code>
 * for randomness. Should be straight-forward to replace with any other random
 * number generator. 
 * @author Johannes Heinonen <johannes.heinonen@iki.fi>
 */
public class Generator {

    /**
     * Generates set of passwords.
     * @param allowedCharacters String of all characters allowed in the passwords. 
     * Whitespace and duplicates are automatically pruned.
     * @param codeLength Desired length for generated passwords
     * @param count Number of passwords to create
     * @param unique Should the passwords be unique within the generated set
     * @return String array in which every slot contains one password or <code>null</code>
     * if the request was impossible to accomplish with given input parameters (f.i. negative
     * count, not enough unique codes available to fill the request, character set was empty).
     */
    public static String[] generateMulti(String allowedCharacters, int codeLength, int count, boolean unique) {

        // Bad parameters
        if (allowedCharacters == null || codeLength <= 0 || count <= 0)
            return null;
        
        char[] chars = trimWhitespaceAndDuplicates(allowedCharacters).toCharArray();
        long paramSpace = pow(chars.length, codeLength);

        // Request larger than given parameter space 
        if (count > paramSpace && unique)
            return null;        
        
        String[] codes = new String[count];
        Set hash = new HashSet();
        Random random = new Random();
        
        for (int i = 0; i < count; i++) {
            String newCode;
            boolean ok = true;
            do {
                newCode = generate(random, chars, codeLength);
                if (unique) {
                    ok = !hash.contains(newCode);
                    if (ok) hash.add(newCode);
                }
            } while (!ok);
            codes[i] = newCode;
        }
        
        return codes;
    }
    
    /**
     * Generates a single password.
     * @param random Random number generator
     * @param charSet Valid character set for generating a password
     * @param codeLength Desired length for the new password
     * @return
     */
    public static String generate(Random random, char[] charSet, int codeLength) {
        
        StringBuffer b = new StringBuffer();

        for (int i = 0; i < codeLength; i++) {
            b.append(charSet[random.nextInt(charSet.length)]);
        }
        
        return b.toString();
    }
    
    /**
     * Remove all whitespace (space, newline, tabulator) characters and any
     * duplicates from input string.
     * @param input Source string to be trimmed
     * @return Trimmed string with no whitespace and no duplicate characters.
     */
    public static String trimWhitespaceAndDuplicates(String input) {
        
        if (input == null)
            return null;
        
        StringBuffer b = new StringBuffer(input);
        int i = 0;
        while (i < b.length()) {
            char c = b.charAt(i++);
            if (Character.isWhitespace(c)) {
                b.deleteCharAt(--i);
                continue;
            }
            
            int k = i;
            while (k < b.length()) {
                if (b.charAt(k++) == c) {
                    b.deleteCharAt(--k);
                }
            }
        }
        return b.toString();
    }
    
    /**
     * Evaluate the power function for integer numbers. Base and exponent must be 
     * non-negative numbers (return <code>Long.MIN_VALUE</code> to signal an error in input
     * parameters). Detects overflows and returns <code>Long.MAX_VALUE</code> when 
     * <code>long</code> no longer is capable of handling the result.
     * @param base Non-negative integer base number
     * @param exponent Non-negative integer exponent
     * @return The result, <code>Long.MIN_VALUE</code> for erroneous input, or
     * <code>Long.MAX_VALUE</code> to indicate that an overflow has occured.
     */
    public static long pow(int base, int exponent) {
        long result = 1;
        
        if (base < 0 || exponent < 0) {
            return Long.MIN_VALUE;
        } else if (exponent == 0) {
            return 1;
        } else if (base == 0) {
            return 0;
        }
        
        while (exponent-- > 0) {
            long tmp = result * base;
            if (tmp < result) {
                // Overflow
                return Long.MAX_VALUE;
            }
            result = tmp;
        }
        return result;
    }
    
    /**
     * Launch the generator from command line.
     */
    public static void main(String[] args) {
        
        if (args.length != 3) {
            System.err.println("Anna komentorivillä sallitut merkit, salasanojen pituus ja lukumäärä!");
            return;
        }
        
        try {

            String[] codes = generateMulti(args[0], Integer.parseInt(args[1]),
                    Integer.parseInt(args[2]), true);
            
            for (int i = 0; i < codes.length; i++) {
                System.out.println(codes[i]);
            }
            
        } catch (NumberFormatException nfe) {
            System.err.println("Salasanojen pituus tai lukumäärä tulee ilmaista numerona!");
        } catch (NullPointerException npe) {
            System.err.println("Virhe parametrien valinnassa!");
        }
    }
}
