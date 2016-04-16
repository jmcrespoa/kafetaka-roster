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

import java.util.Objects;
import java.util.Stack;
import java.util.stream.Stream;

/**
 * This class represents a roster within our killer app kafetaka.
 * 
 * A Roster contains a stack of elements, that is, a last in, first out list.
 * 
 * A roster is univocally identified by its name, so two rosters with the same 
 * name are the same roster.
 * 
 * @author Norville Rogers
 */
public final class Roster {
    
    private final String name;
    private final Stack<String> elements = new Stack<>();

    /**
     * Creates a new Roster instance.
     * 
     * @param rosterName the roster name; must be neither null nor empty.
     * @throws IllegalArgumentException If the provided rosterName is null or empty
     */
    public Roster(String rosterName) {
        if (rosterName == null)
            throw new IllegalArgumentException("You are trying to create a Roster with a null name. The roster name must be not null");
        if (rosterName.isEmpty())
            throw new IllegalArgumentException("You are trying to create a Roster with an empty name. The roster name must be not empty");
        
        this.name = rosterName;
    }
    
    /**
     * This roster name get method.
     * 
     * @return This roster's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Adds an element to this roster.
     * 
     * @param element the element we are about to add to this roster; must be 
     * neither null nor empty
     * @return This roster
     * @throws IllegalArgumentException If the given element is null or empty.
     */
    public Roster push(String element) {
        if (element == null)
            throw new IllegalArgumentException("You are trying to add a null element to this Roster. The element must be not null");
        if (element.isEmpty())
            throw new IllegalArgumentException("You are trying to add an empty element to this Roster. The element must be not empty");
        
        this.elements.push(element);
        return this;
    }

    /**
     * Retrieves the last element added to this Roster collection of elements.
     * 
     * @return The last addition to this Roster
     */
    public String pop() {
        return this.elements.pop();
    }
    
    /**
     * Returns this Roster element collection size.
     * 
     * @return An int with this Roster collection size.
     */
    public int size() {
        return this.elements.size();
    }
    
    /**
     * Tests if this Roster has no elements.
     * 
     * @return true if and only if this Roster has no elements, that is, 
     * its size is zero; false otherwise.
     */
    public boolean isEmpty() {
        return this.elements.isEmpty();
    }
    
    /**
     * Allows access to this Roster collection of elements stream.
     * 
     * @return this Roster element stream.
     */
    public Stream<String> stream() {
        return this.elements.stream();
    }
    
    /**
     * The hascode is calculated from this Roster name.
     * 
     * @return An int representing the hashcode.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.name);
        return hash;
    }

    /**
     * Two rosters are equal when they have the same name.
     * 
     * @param obj another Roster.
     * @return true if we are comparing Rosters with the same name, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Roster other = (Roster) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "{name:" + name + ", elements:[" + elements + "]}";
    }
    
}
