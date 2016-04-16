/*
This is free and unencumbered software released into the public domain.

Anyone is free to copy, modify, publish, use, compile, sell, or
distribute this software, either in source code form or as a compiled
binary, for any purpose, commercial or non-commercial, and by any
means.

In jurisdictions that recognize copyright laws, the author or authors
of this software dedicate any and all copyright interest in the
software to the public domain. We make this dedication for the benefit
of the public at large and to the detriment of our heirs and
successors. We intend this dedication to be an overt act of
relinquishment in perpetuity of all present and future rights to this
software under copyright law.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

For more information, please refer to <http://unlicense.org>*/

package com.itfraud.kafetaka.roster;

import java.util.stream.Collectors;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 * This class shows how a Roster class is supposed to work.
 * 
 * @author Norville Rogers
 */
public class RosterTest {
    
    private static final String ROSTER_NAME = "my roster";
    private static final String AN_ELEMENT = "my element";
    private static final String ANOTHER_ELEMENT = "my second element";
    private final Roster roster = new Roster(ROSTER_NAME);
    
    /**
     * We add an element to the Roster, and we verify that it is there.
     */
    @Test
    public void addElementToRoster() {
        roster.push(AN_ELEMENT);
        assertThat("The RosterManager contains only one Roster", 
                roster.size(), is(1));
        assertThat("The RosterManager contains the Roster we provided", 
                roster.stream().collect(Collectors.toList()), hasItems(AN_ELEMENT));
    }
    
    /**
     * We can retrieve elements from a Roster.
     */
    @Test
    public void pollElementFromRoster() {
        roster.push(AN_ELEMENT);
        String element = roster.pop();
        assertThat("The Roster is empty", 
                roster.size(), is(0));
        assertThat("The retrieved element is the one we added", 
                element, is(AN_ELEMENT));
    }
    
    /**
     * We verify that we retrieve the last element added to the list of elements.
     */
    @Test
    public void verifyPopIsLIFO() {
        roster.push(AN_ELEMENT);
        roster.push(ANOTHER_ELEMENT);
        String element = roster.pop();
        assertThat("The Roster contains only one Roster", 
                roster.size(), is(1));
        assertThat("The retrieved element is the last one we added", 
                element, is(ANOTHER_ELEMENT));
    }

}
