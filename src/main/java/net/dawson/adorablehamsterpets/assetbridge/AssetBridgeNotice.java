package net.dawson.adorablehamsterpets.assetbridge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Zeigt pro Spielstart einmal einen Hinweis, wenn die Original-Assets fehlen.
 * Bewusst ohne Lang-Keys, denn die Lang-Dateien stammen ja aus dem (fehlenden) Original.
 * Signaturen pruefen: AlertScreen(Runnable, Component, Component), Minecraft#gui.setScreen/screen (wie im Port genutzt).
 */
public final class AssetBridgeNotice {
    private static boolean shown;

    private AssetBridgeNotice() {}

    public static void showOnce() {
        if (shown) return;
        String problem = AssetBridge.errorMessage();
        if (problem == null) return;
        shown = true;

        Minecraft mc = Minecraft.getInstance();
        // Verzoegert, damit wir nicht mitten in TitleScreen#init den Screen wechseln.
        mc.execute(() -> {
            Screen back = mc.gui.screen();
            mc.gui.setScreen(new AlertScreen(
                    () -> mc.gui.setScreen(back),
                    Component.literal("Adorable Hamster Pets: original assets missing"),
                    Component.literal(problem + "\n\nDownload the original mod (Minecraft 1.21.1, Fabric, version " + OriginalAssetLocator.REQUIRED_ORIGINAL_VERSION + ") "
                            + "from CurseForge or Modrinth and place the .jar in the folder above. "
                            + "Do NOT put it in mods/. Until then hamsters are invisible and silent.")));
        });
    }
}
