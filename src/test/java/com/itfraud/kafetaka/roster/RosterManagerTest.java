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

For more information, please refer to <http://unlicense.org>
 */
package com.itfraud.kafetaka.roster;

import java.util.List;
import java.util.stream.IntStream;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 * This class shows how a RosterManager is supposed to work.
 * 
 * For more info, take a look at the RosterManager javadoc (and read it
 * thoroughly, it took me a while to write it!)
 * 
 * @author Norville Rogers
 */
public class RosterManagerTest {
    
    private static final int ROSTER_MAXIMUM_NUMBER_OF_ELEMENTS = 2;
    private static final String ROSTER_NAME = "first turn";
    private static final String ANOTHER_ROSTER_NAME = "second turn";
    
    private RosterManager rosterManager;
    private Roster providedRoster;
    private Roster anotherProvidedRoster;
    
    /**
     * Creates a roster manager with a maximum number of elements constraint,
     * and a couple of provided rosters.
     */
    @Before
    public void setUp() {
        rosterManager = new RosterManager(ROSTER_MAXIMUM_NUMBER_OF_ELEMENTS);
        providedRoster = new Roster(ROSTER_NAME);
        anotherProvidedRoster = new Roster(ANOTHER_ROSTER_NAME);
    }
    
    
    /**
     * We provide our RosterManager with a new Roster, so that it can 
     * manage it for us.
     * 
     * The managed Roster returned has the same name as the provided Roster.
     */
    @Test
    public void manageOneRoster() {
        rosterManager.manage(providedRoster);
        List<Roster> managedRosters = rosterManager.getManagedRosters();
        
        assertThat("The RosterManager contains only one Roster", 
                managedRosters, hasSize(1));
        assertThat("The RosterManager contains the Roster we provided", 
                managedRosters, hasItems(providedRoster));
    }
    
    /**
     * You can ask a RosterManager to manage a provided Roster only once.
     * 
     * Do not try to manage the same Roster twice or you'll get an ugly exception!
     */
    @Test(expected = IllegalArgumentException.class)
    public void tryingToAddAnAlreadyExistingRoster() {
        rosterManager
                .manage(providedRoster)
                .manage(providedRoster);
    }
    
    /**
     * When we have a couple of Rosters and the first one has one element
     * too many (one over the maximum size), one element from the first
     * Roster gets moved to the second one (we do not know which one, because
     * the RosterManager shuffles each Roster list of elements as part of the
     * rearrenging).
     */
    @Test
    public void takeElementsFromOneRosterToTheNextOne() {
        // Remember, on these tests, a managed roster maximum size is two elements
        providedRoster.push("one").push("two").push("three"); 
        anotherProvidedRoster.push("four");
        rosterManager.manage(providedRoster);
        rosterManager.manage(anotherProvidedRoster);
        List<Roster> managedRosters = rosterManager.getManagedRosters();
        
        assertThat("The RosterManager contains two Rosters", 
                managedRosters, hasSize(2));
        managedRosters.stream().forEach(eachRoster -> 
                assertThat("Each Roster contains two elements", 
                eachRoster.size(), is(2)));
    }
    
    
    /**
     * If, after applying the RosterManager constrains, not all elements 
     * associated to the provided Rosters fit into the managed Rosters, 
     * new Rosters are created to accommodate the letfovers.
     * 
     * For example, if we have two provided Rosters with three elements each,
     * and the RosterManager maximum number of elements is two, the two 
     * provided Rosters will be rearrenged to contain two elements.
     * That way only four elements fit within the two managed Rosters generated
     * from the provided Rosters. The two elements left will be added to a new 
     * Roster created by the manager.
     * Again, we will not know which ones, as the RosterManager shuffles the 
     * provided Roster elements.
     */
    @Test
    public void testCreateNewRosterForLeftovers() {
        // Remember, on these tests, a managed roster maximum size is two elements
        providedRoster.push("one").push("two").push("three");
        anotherProvidedRoster.push("four").push("five").push("six");
        rosterManager.manage(providedRoster);
        rosterManager.manage(anotherProvidedRoster);
        List<Roster> managedRosters = rosterManager.getManagedRosters();
        
        assertThat("The RosterManager contains three Rosters", 
                managedRosters, hasSize(3));
        managedRosters.stream().forEach(eachRoster -> 
                assertThat("Each Roster contains two elements", 
                eachRoster.size(), is(2)));
    }
    
    /**
     * Verifies that the managed Rosters elements have been shuffled.
     * 
     * Hey! We are not testing randomness here. That would be tough!
     * We delegate randomness to a third party library, and we expect it to work fine. 
     * We are only verifying that a random function is being applied.
     */
    @Test
    public void testManagedRosterElementsAreShuffled() {
        providedRoster.push("one").push("two");
        rosterManager.manage(providedRoster);
        // There is a shuffle every single time we invoke getManagedRosters
        // Check out the RosterManager javadoc
        long totalNumberOfOneElements = 
                IntStream.range(0, 1000)
                        .mapToObj(i -> 
                                rosterManager.getManagedRosters().iterator().next().pop())
                        .filter(element -> element.equals("one"))
                        .count();
        
        assertThat("The element one appears at least once", 
                totalNumberOfOneElements, is(not(0)));
        assertThat("The element two appears at least once", 
                totalNumberOfOneElements, is(not(1000)));
    }
    
}
