package net.dawson.adorablehamsterpets.mixin.client;

import net.dawson.adorablehamsterpets.assetbridge.AssetBridgeRepositorySource;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Arrays;

/**
 * Haengt unsere RepositorySource nur an den Client-Resource-PackRepository
 * (erkennbar an der ClientPackSource), nicht an den Datapack-Repository des Servers.
 * Pruefen: Konstruktor-Signatur in 26.2 (aktuell: PackRepository(RepositorySource... sources)).
 */
@Mixin(PackRepository.class)
public abstract class PackRepositoryMixin {
    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private static RepositorySource[] ahp$addAssetBridge(RepositorySource[] sources) {
        boolean isClientResourceRepo = Arrays.stream(sources).anyMatch(s -> s instanceof ClientPackSource);
        if (!isClientResourceRepo) return sources;
        RepositorySource[] copy = Arrays.copyOf(sources, sources.length + 1);
        copy[sources.length] = new AssetBridgeRepositorySource();
        return copy;
    }
}
