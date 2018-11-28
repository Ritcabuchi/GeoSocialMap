package com.example.ritcabcuhi.geosocialmapv2;

class FirebaseAuth {
    private static Object instance;

    public static Object getInstance() {
        return instance;
    }

    public static void setInstance(Object instance) {
        FirebaseAuth.instance = instance;
    }
}
