package io.github.leocolomb.danfoss.internal;

import io.github.sonic_amiga.opensdg.java.SDG;

public class SDGUtils {
    public static byte[] ParseKey(String keyStr) {
        if (keyStr == null) {
            return null;
        }

        byte[] key = SDG.hex2bin(keyStr);
        return key.length == 32 ? key : null;
    }
}
