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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * A RosterManager takes a collection of Rosters (from now on, we will call
 * them provided Rosters) and rearranges them (we will call managed Rosters
 * to the rearranged Rosters) so that they can comply with a given set of 
 * constrains. 
 * 
 * So, basically, a RosterManager takes a bunch of provided Rosters, and turns them
 * into managed Rosters.
 * 
 * Right now, there are two constrains enforced by a RosterManager:
 * 1) The maximum size of any managed Roster.
 * 2) The order of the elements within a managed Roster. The RosterManager
 * shuffles the elements contained within the provided Roster that originated it.
 * 
 * Let's take a look at a RosterManager main features:
 *  - It allows you to manage one or more provided Rosters.
 *  - It doesn't allow you to manage the same Roster more that once.
 *  - It allows you to set up the maximum size for the managed Rosters.
 *  - It will create a managed Roster for each provided Roster and it will have the
 *    same name.
 *  - If a provided Roster size is greater than the RosterManager maximum, 
 *    the manager will take the surplus elements and it will 
 *    move those elements to another managed Roster where there is room left. 
 *    If there is no room left in any of the managed rosters, as many new Rosters 
 *    will be created as needed to allocate all the elements within the provided Rosters.
 *    That way, all elements within the provided Rosters will be in any of the managed
 *    Rosters.
 *  - The manager will always shuffle the provided Roster elements before 
 *    attempting to rearrenge them. Hence, whenever a provided Roster has extra
 *    elements, you will never know which ones are going to be moved to the next 
 *    managed Roster in the pool. Actually, a different set will be moved every single
 *    time you manage the provided Rosters.
 * 
 * @author Norville Rogers
 */
public class RosterManager {
    
    private static final String EXTRA_MANAGED_ROSTER_NAME_PREFIX = "Automatic Roster";
   
    private final int managedRosterSize;
    private final List<Roster> providedRosters = Collections.synchronizedList(new ArrayList<>());
    private List<Roster> managedRosters;

    /**
     * Creates a new RosterManager instance.
     * 
     * @param rosterMaxNumberOfElements This will be the maximum number of
     * elements this manager is going to allow for any roster managed by it; must be
     * greater than zero.
     * @throws IllegalArgumentException If the provided rosterMaxNumberOfElements
     * is not greater than zero.
     */
    public RosterManager(int rosterMaxNumberOfElements) {
        if (rosterMaxNumberOfElements <= 0)
            throw new IllegalArgumentException("You are trying to create a RosterManager with a roster max size of: " + rosterMaxNumberOfElements + ". It must be greater than zero");
        
        this.managedRosterSize = rosterMaxNumberOfElements;
    }

    /**
     * Adds a roster to the pool of managed rosters.
     * 
     * The same roster only can be added once.
     * 
     * @param roster the roster we want to add to the pool; must be not null
     * @return A reference to this RosterManager
     * @throws IllegalArgumentException If the provided roster is null
     * @throws IllegalArgumentException If the provided roster has been already added
     */
    public RosterManager manage(Roster roster) {
        if (roster == null)
            throw new IllegalArgumentException("You are trying to add a null roster to a RosterManager. The roster must be not null");
        
        if (this.providedRosters.contains(roster))
            throw new IllegalArgumentException("You are trying to add a roster twice. You can only add a roster once to a RosterManager");
        
        this.providedRosters.add(roster);
        return this;
    }

    /**
     * Returns the list of managed rosters.
     * 
     * @return A List containing Roster objects or an empty list if this RosterManager
     * does not manage any rosters.
     */
    public List<Roster> getManagedRosters() {
        setUpManagedRosters();
        shuffleProvidedRosters();
        Queue<String> leftovers = fitProvidedRostersToMaximumSize();
        if (thereAre(leftovers))
            allocateWithinManagedRosters(leftovers);
        if (thereAre(leftovers))
            createNewManagedRostersFor(leftovers);
        return Collections.unmodifiableList(this.managedRosters);
    }
    
    /*
    Cleans up any changes done previosly to the managed Rosters.
    */
    private void setUpManagedRosters() {
        this.managedRosters = Collections.synchronizedList(new ArrayList<>());
    }
    
    /*
    For each Roster within the managedRoster list, the shuffle method is invoked.
    */
    private void shuffleProvidedRosters() {
        this.providedRosters.stream().forEach(providedRoster -> {
            Roster managedRoster = new Roster(providedRoster.getName());
            List<String> providedElements = 
                    providedRoster.stream().collect(Collectors.toList());
            Collections.shuffle(providedElements);
            providedElements.stream().forEach(element -> managedRoster.push(element));
            this.managedRosters.add(managedRoster);
        });
    }

    /*
    Adjusts the existing rosters to "fit" with the max. number of elements constrain.
    */
    private Queue<String> fitProvidedRostersToMaximumSize() {
        Queue<String> leftovers = new LinkedList<>();
        this.managedRosters.stream().forEach(managedRoster -> {
            while (thereAreLeftoversOn(managedRoster)) 
                    leftovers.offer(managedRoster.pop());
        });
        return leftovers;
    }
    
    /*
    Allocates leftovers on the empty sits among the existing managed Rosters.
    */
    private void allocateWithinManagedRosters(Queue<String> leftovers) {
        this.managedRosters.stream().forEach(managedRoster -> {
            if (!managedRoster.isEmpty()) 
                while (thereAre(leftovers) && thereIsRoomOn(managedRoster)) 
                    managedRoster.push(leftovers.poll());
        });
    }
    
    /*
    Creates new managed Rosters for all those elements that didn't fit on the
    managed Rosters existing so far.
    */
    private void createNewManagedRostersFor(Queue<String> leftovers) {
        List<Roster> extraManagedRosters = new ArrayList<>();
        int counter = 0;
        while (thereAre(leftovers)) {
            Roster managedRoster = 
                    new Roster(EXTRA_MANAGED_ROSTER_NAME_PREFIX + ++counter);
            while (thereAre(leftovers) && thereIsRoomOn(managedRoster)) 
                managedRoster.push(leftovers.poll());
            
            extraManagedRosters.add(managedRoster);
        }
        this.managedRosters.addAll(extraManagedRosters);
    }
    
    private boolean thereAre(Queue<String> leftovers) {
        return !leftovers.isEmpty();
    }
    
    private boolean thereIsRoomOn(Roster roster) {
        return roster.size() < this.managedRosterSize;
    }
    
    private boolean thereAreLeftoversOn(Roster roster) {
        return roster.size() > this.managedRosterSize;
    }
    
}
