package com.webl.keyvaluestore.server;

public interface ServerInterface {
    default void get() {
        System.out.println("get called");
    }

    default void set() {
        System.out.println("set called");
    }
}
