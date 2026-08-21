package mx.carlosjr.cobblesort;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

final class SortController {
    private final Deque<Click> clicks = new ArrayDeque<>();
    private int expectedSyncId = -1;

    boolean isBusy() {
        return !clicks.isEmpty();
    }

    boolean start(MinecraftClient client) {
        ScreenHandler handler = resolveHandler(client);
        if (handler == null || client.player == null) {
            return false;
        }

        List<Slot> slots = targetSlots(handler, client.player.getInventory());
        if (slots.size() < 2 || !slots.stream().allMatch(slot -> slot.canTakeItems(client.player))) {
            return false;
        }

        List<CobblemonItemOrder.SortKey> current = slots.stream()
                .map(slot -> CobblemonItemOrder.key(slot.getStack()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        List<CobblemonItemOrder.SortKey> wanted = new ArrayList<>(current);
        wanted.sort(Comparator.naturalOrder());

        for (int destination = 0; destination < wanted.size(); destination++) {
            CobblemonItemOrder.SortKey wantedKey = wanted.get(destination);
            if (current.get(destination).equals(wantedKey)) {
                continue;
            }

            int source = find(current, wantedKey, destination + 1);
            if (source < 0) {
                clicks.clear();
                return false;
            }

            Slot sourceSlot = slots.get(source);
            Slot destinationSlot = slots.get(destination);
            ItemStack sourceStack = sourceSlot.getStack();
            ItemStack destinationStack = destinationSlot.getStack();
            if ((!destinationStack.isEmpty() && !sourceSlot.canInsert(destinationStack))
                    || (!sourceStack.isEmpty() && !destinationSlot.canInsert(sourceStack))) {
                clicks.clear();
                return false;
            }

            clicks.addLast(new Click(sourceSlot.id));
            clicks.addLast(new Click(destinationSlot.id));
            clicks.addLast(new Click(sourceSlot.id));

            CobblemonItemOrder.SortKey old = current.get(destination);
            current.set(destination, current.get(source));
            current.set(source, old);
        }

        expectedSyncId = handler.syncId;
        if (clicks.isEmpty()) {
            client.player.sendMessage(Text.translatable("message.cobblesort.sorted"), true);
        }
        return true;
    }

    void tick(MinecraftClient client) {
        if (clicks.isEmpty()) {
            return;
        }
        if (client.player == null || client.interactionManager == null
                || client.player.currentScreenHandler.syncId != expectedSyncId) {
            clicks.clear();
            return;
        }

        // Un intercambio completo por tick mantiene el cursor vacío y evita una ráfaga excesiva de paquetes.
        for (int i = 0; i < 3 && !clicks.isEmpty(); i++) {
            Click click = clicks.removeFirst();
            client.interactionManager.clickSlot(
                    expectedSyncId, click.slotId(), 0, SlotActionType.PICKUP, client.player
            );
        }

        if (clicks.isEmpty()) {
            client.player.sendMessage(Text.translatable("message.cobblesort.sorted"), true);
        }
    }

    private static ScreenHandler resolveHandler(MinecraftClient client) {
        if (client.player == null) {
            return null;
        }
        if (client.currentScreen == null) {
            return client.player.playerScreenHandler;
        }
        if (!(client.currentScreen instanceof HandledScreen<?> handledScreen)) {
            return null;
        }
        ScreenHandler handler = handledScreen.getScreenHandler();
        if (handler instanceof PlayerScreenHandler
                || handler instanceof GenericContainerScreenHandler
                || handler instanceof ShulkerBoxScreenHandler) {
            return handler;
        }
        return null;
    }

    private static List<Slot> targetSlots(ScreenHandler handler, PlayerInventory playerInventory) {
        if (handler instanceof PlayerScreenHandler) {
            List<Slot> main = new ArrayList<>();
            for (Slot slot : handler.slots) {
                if (slot.inventory != playerInventory) continue;
                if (slot.getIndex() >= 9 && slot.getIndex() <= 35) main.add(slot);
            }
            main.sort(Comparator.comparingInt(Slot::getIndex));
            return main;
        }

        List<Slot> container = new ArrayList<>();
        for (Slot slot : handler.slots) {
            if (slot.inventory != playerInventory) {
                container.add(slot);
            }
        }
        return container;
    }

    private static int find(List<CobblemonItemOrder.SortKey> stacks,
                            CobblemonItemOrder.SortKey wanted, int start) {
        for (int i = start; i < stacks.size(); i++) {
            if (stacks.get(i).equals(wanted)) {
                return i;
            }
        }
        return -1;
    }

    private record Click(int slotId) {
    }
}
