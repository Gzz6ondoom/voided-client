package me.gzz6ondoom.voided.features.gui;

import me.gzz6ondoom.voided.Voided;
import me.gzz6ondoom.voided.features.Feature;
import me.gzz6ondoom.voided.features.gui.items.Item;
import me.gzz6ondoom.voided.features.gui.items.buttons.ModuleButton;
import me.gzz6ondoom.voided.features.modules.Module;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class VoidedGui extends Screen {

    private static VoidedGui INSTANCE;
    private static Color colorClipboard = null;

    static {
        INSTANCE = new VoidedGui();
    }

    private final ArrayList<Widget> widgets = new ArrayList<>();

    public VoidedGui() {
        super(Component.literal("VOIDED"));
        setInstance();
        load();
    }

    public static VoidedGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new VoidedGui();
        }
        return INSTANCE;
    }

    public static VoidedGui getClickGui() {
        return VoidedGui.getInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    private void load() {
        int x = -84;
        for (Module.Category category : Voided.moduleManager.getCategories()) {
            if (category == Module.Category.HUD) continue;
            Widget panel = new Widget(category.getName(), x += 90, 4, true);
            Voided.moduleManager.stream()
                    .filter(m -> m.getCategory() == category && !m.hidden)
                    .map(ModuleButton::new)
                    .forEach(panel::addButton);
            this.widgets.add(panel);
        }
        this.widgets.forEach(components -> components.getItems().sort(Comparator.comparing(Feature::getName)));
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        Item.context = context;
        context.fill(0, 0, context.guiWidth(), context.guiHeight(), new Color(0, 0, 0, 120).hashCode());
        this.widgets.forEach(components -> components.drawScreen(context, mouseX, mouseY, delta));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        this.widgets.forEach(components -> components.mouseClicked((int) click.x(), (int) click.y(), click.button()));
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        this.widgets.forEach(components -> components.mouseReleased((int) click.x(), (int) click.y(), click.button()));
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount < 0) {
            this.widgets.forEach(component -> component.setY(component.getY() - 10));
        } else if (verticalAmount > 0) {
            this.widgets.forEach(component -> component.setY(component.getY() + 10));
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        this.widgets.forEach(component -> component.onKeyPressed(input.input()));
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        this.widgets.forEach(component -> component.onKeyTyped(input.codepointAsString(), input.modifiers()));
        return super.charTyped(input);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
        // ignore 1.21.8 blur thing
    }

    public final ArrayList<Widget> getComponents() {
        return this.widgets;
    }

    public int getTextOffset() {
        return -6;
    }

    public static Color getColorClipboard() {
        return colorClipboard;
    }

    public static void setColorClipboard(Color color) {
        colorClipboard = color;
    }
}
