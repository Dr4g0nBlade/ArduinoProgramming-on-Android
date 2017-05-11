package es.roboticafacil.dyor.arduinosp.Models;

/**
 * Created by Dragos Dunareanu on 30-Mar-17.
 */

public class Component {

    private int id;
    private String[] types = {"board", "human-interface", "communication", "environment interaction"};
    private String type;
    private String name;

    public Component() {
    }

    public Component(String t, String n) {
        this.type = t;
        this.name = n;
    }

    public int getId() {
        return id;
    }

    public String[] getTypes() {
        return types;
    }

    public String getType() {
        return type;
    }

    public void setType(String t) {
        this.type = t;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
