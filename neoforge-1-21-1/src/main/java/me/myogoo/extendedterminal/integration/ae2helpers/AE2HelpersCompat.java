package me.myogoo.extendedterminal.integration.ae2helpers;

import net.neoforged.fml.ModList;
import org.slf4j.Logger;

/**
 * Loader-safe feature gate for AE2Helpers-family compatibility.
 *
 * <p>ExtendedTerminal keeps terminal recipe-transfer assistance native. Matrix result import belongs
 * to AE2HelpersMatrix's {@code ae2helpersmatrix:matrix_result_import_core}, which records accepted
 * Matrix pattern outputs from {@code ClusterAssemblerMatrix.pushCraftingJob} and imports adjacent
 * expected result inventories. ExtendedTerminal must not duplicate that Matrix hook.</p>
 */
public final class AE2HelpersCompat {
    public static final String AE2HELPERS_MOD_ID = "ae2helpers";
    public static final String AE2HELPERS_MATRIX_MOD_ID = "ae2helpersmatrix";
    public static final String EXTENDEDAE_MOD_ID = "extendedae";

    private AE2HelpersCompat() {
    }

    public static boolean isLoaded() {
        return isModLoaded(AE2HELPERS_MOD_ID);
    }

    public static boolean isMatrixHelperLoaded() {
        return isModLoaded(AE2HELPERS_MATRIX_MOD_ID);
    }

    public static boolean isExtendedAeLoaded() {
        return isModLoaded(EXTENDEDAE_MOD_ID);
    }

    /**
     * Terminal recipe-transfer assistance is an ExtendedTerminal-native behavior. It remains enabled
     * even when ae2helpers is not installed so JEI/EMI/AE2 fill paths keep working normally.
     */
    public static boolean shouldEnableTerminalAssist() {
        return true;
    }

    /**
     * Result-import-card compatibility is only safe when ae2helpers itself is present.
     */
    public static boolean shouldEnableResultImportCompat() {
        return isLoaded();
    }

    /**
     * True when Matrix result-import behavior should be left entirely to AE2HelpersMatrix.
     */
    public static boolean shouldDeferMatrixResultImportToAe2HelpersMatrix() {
        return isMatrixHelperLoaded();
    }

    public static void logDetectedState(Logger logger) {
        if (!isLoaded() && !isMatrixHelperLoaded() && !isExtendedAeLoaded()) {
            return;
        }
        logger.debug(
                "AE2Helpers compatibility: ae2helpers={}, ae2helpersmatrix={}, extendedae={}, terminalAssist={}, resultImportCompat={}, deferMatrixResultImportToAE2HelpersMatrix={}",
                isLoaded(),
                isMatrixHelperLoaded(),
                isExtendedAeLoaded(),
                shouldEnableTerminalAssist(),
                shouldEnableResultImportCompat(),
                shouldDeferMatrixResultImportToAe2HelpersMatrix());
    }

    private static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
