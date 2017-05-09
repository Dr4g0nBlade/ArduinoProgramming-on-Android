package es.roboticafacil.dyor.tabbed.Models;

import java.util.ArrayList;

/**
 * Created by Dragos Dunareanu on 02-Apr-17.
 */

public class ComponentGroup {

    private String name;
    private ArrayList<Component> components = new ArrayList<>();

    public ComponentGroup() {
    }

    public ComponentGroup(String n) {
        this.name = n;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Component> getComponents() {
        return components;
    }
}
