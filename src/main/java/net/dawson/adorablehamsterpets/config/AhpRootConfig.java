package net.dawson.adorablehamsterpets.config;

import dev.architectury.networking.NetworkManager;
import me.fzzyhmstrs.fzzy_config.annotations.NonSync;
import me.fzzyhmstrs.fzzy_config.annotations.RootConfig;
import me.fzzyhmstrs.fzzy_config.api.SaveType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigAction;
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup;
import me.fzzyhmstrs.fzzy_config.screen.widget.TextureIds;
import me.fzzyhmstrs.fzzy_config.util.Translatable;
import net.dawson.adorablehamsterpets.AdorableHamsterPets;
import net.dawson.adorablehamsterpets.networking.payload.RequestGuidebookPayload;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Root-level configuration used purely as the entry point for this mod's
 * settings.  The @RootConfig annotation causes this config’s name and
 * description to be shown on the landing page, and the other configs in
 * the same namespace will be listed below it.
 */
@Translatable.Name("Main Menu")
@Translatable.Desc("Here's where your hamster experimentation begins. Don't forget to touch grass.")
@RootConfig
public class AhpRootConfig extends Config {
    public AhpRootConfig() {
        super(Identifier.fromNamespaceAndPath(AdorableHamsterPets.MOD_ID, "root"));
    }

    @Override
    @NotNull
    public SaveType saveType() {
        return SaveType.SEPARATE;
    }

    // --- Help & Other Distractions ---
    @Translatable.Name("Help & Other Distractions")
    @Translatable.Desc("Buttons for when you’re lost, bored, or met a bug that’s not just existential hamster angst.")
    public ConfigGroup helpAndResources = new ConfigGroup("helpAndResources", false);

    @NonSync
    @Translatable.Name("I Lost My Book!")
    public ConfigAction giveGuideBook = new ConfigAction.Builder()
            .title(Component.translatable("config.adorablehamsterpets.main.helpAndResources.giveGuideBook"))
            .desc(Component.translatable("config.adorablehamsterpets.main.helpAndResources.giveGuideBook.desc"))
            .decoration(TextureIds.INSTANCE.getDECO_BOOK())
            .build(() -> {
                NetworkManager.sendToServer(new RequestGuidebookPayload());
            });

    @NonSync
    @Translatable.Name("Report a Bug")
    public ConfigAction reportBug = new ConfigAction.Builder()
            .title(Component.translatable("config.adorablehamsterpets.main.helpAndResources.reportBug"))
            .desc(Component.translatable("config.adorablehamsterpets.main.helpAndResources.reportBug.desc"))
            .decoration(TextureIds.INSTANCE.getDECO_LINK())
            .build(new ClickEvent.OpenUrl(java.net.URI.create("https://github.com/IamGatsu/Unofficial-Adorable-Hamster-Pets-Port")));

    @NonSync
    @Translatable.Name("Join Discord")
    public ConfigAction joinDiscord = new ConfigAction.Builder()
            .title(Component.translatable("config.adorablehamsterpets.main.helpAndResources.joinDiscord"))
            .desc(Component.translatable("config.adorablehamsterpets.main.helpAndResources.joinDiscord.desc"))
            .decoration(TextureIds.INSTANCE.getDECO_BUTTON_CLICK())
            .build(new ClickEvent.OpenUrl(java.net.URI.create("https://discord.gg/gydHQSHsf")));

    @NonSync
    @ConfigGroup.Pop
    @Translatable.Name("Support Original Creator")
    @Translatable.Desc("Support the creator of the original Adorable Hamster Pets mod. This is an unofficial port.")
    public ConfigAction visitWebsite = new ConfigAction.Builder()
            .title(Component.literal("Buy him a Coffee"))
            .desc(Component.literal("Opens the original creator's Ko-fi page."))
            .decoration(TextureIds.INSTANCE.getDECO_LINK())
            .build(new ClickEvent.OpenUrl(java.net.URI.create("https://ko-fi.com/dawsonbodenhamer")));
}
