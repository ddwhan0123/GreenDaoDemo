package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DemoClass {
    public static void main(String[] asa) {
        Schema schema = new Schema(1, "com.example");
        addPerson(schema);
        try {
            new DaoGenerator().generateAll(schema, "/Users/jiajiewang/Desktop/SQLDemo/app/src/main/java-gen");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addPerson(Schema schema) {
        Entity entity = schema.addEntity("Person");
        entity.addIdProperty();
        entity.addStringProperty("Name").notNull();
        entity.addStringProperty("Age").notNull();
        entity.addStringProperty("Address");
    }
}
