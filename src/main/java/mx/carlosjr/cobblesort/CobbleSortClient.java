package mx.carlosjr.cobblesort;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class CobbleSortClient implements ClientModInitializer {
    private static final SortController SORT_CONTROLLER = new SortController();
    private static KeyBinding sortKey;

    @Override
    public void onInitializeClient() {
        sortKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cobblesort.sort",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.cobblesort.controls"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            SORT_CONTROLLER.tick(client);

            // Las teclas configurables normales solo se sondean cuando no hay una pantalla abierta.
            while (client.currentScreen == null && sortKey.wasPressed()) {
                trigger(client);
            }
        });

        // Minecraft entrega el teclado a la pantalla abierta antes que a KeyBinding.
        // Se escucha cada inventario directamente para que la misma tecla funcione en cofres y shulkers.
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof HandledScreen<?>)) {
                return;
            }
            ScreenKeyboardEvents.allowKeyPress(screen).register((currentScreen, key, scanCode, modifiers) -> {
                if (!sortKey.matchesKey(key, scanCode)) {
                    return true;
                }
                trigger(client);
                return false;
            });
        });
    }

    private static void trigger(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null) {
            return;
        }
        if (SORT_CONTROLLER.isBusy()) {
            client.player.sendMessage(Text.translatable("message.cobblesort.busy"), true);
        } else if (!SORT_CONTROLLER.start(client)) {
            client.player.sendMessage(Text.translatable("message.cobblesort.unsupported"), true);
        }
    }
}
