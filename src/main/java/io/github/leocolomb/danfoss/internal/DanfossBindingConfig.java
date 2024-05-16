package io.github.leocolomb.danfoss.internal;

import java.util.HashMap;

import jakarta.xml.bind.DatatypeConverter;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.sonic_amiga.opensdg.java.SDG;

public class DanfossBindingConfig extends HashMap<String, String> {
    private static final Logger logger = LoggerFactory.getLogger(DanfossBindingConfig.class);
    private static DanfossBindingConfig g_Config = new DanfossBindingConfig();

    public String privateKey;
    public String publicKey;
    public String userName;

    public static DanfossBindingConfig get() {
        return g_Config;
    }

    private void update(DanfossBindingConfig newConfig) {
        String newKey = newConfig.privateKey;
        byte[] newPrivkey;

        userName = newConfig.userName;

        if (newKey == null || newKey.isEmpty()) {
            newPrivkey = SDG.createPrivateKey();
            newKey = DatatypeConverter.printHexBinary(newPrivkey);

            logger.trace("Created new private key: {}", newKey);
        } else if (newKey.equals(privateKey)) {
            return;
        } else {
            // Validate the new key and revert back to the old one if validation fails
            // It is rather dangerous to inadvertently damage it, you'll lose all
            // your thermostats and probably have to set everything up from scratch.
            newPrivkey = SDGUtils.ParseKey(newKey);

            if (newPrivkey == null) {
                logger.warn("Invalid private key configured: {}; reverting back to old one", newKey);
                return;
            }

            logger.trace("Got private key from configuration: {}", newKey);
        }

        privateKey = newKey;
        publicKey = DatatypeConverter.printHexBinary(SDG.calcPublicKey(newPrivkey));
        logger.trace("Got public key from generator: {}", publicKey);
        userName = newConfig.userName;

        GridConnectionKeeper.UpdatePrivateKey(newKey);
    }

    public static void update(@NonNull String privateKey, String userName) {
        DanfossBindingConfig newConfig = new DanfossBindingConfig();
        newConfig.privateKey = privateKey;
        newConfig.userName = userName;

        // Kludge for OpenHAB 2.4. Early development versions of this binding didn't have
        // this parameter. OpenHAB apparently cached parameter structure and doesn't present
        // the new option in binding config. Consequently, the field in DeviRegBindingConfig
        // object stays null.
        if (newConfig.userName == null) {
            newConfig.userName = "OpenSDG test";
        }

        g_Config.update(newConfig);
    }
}
