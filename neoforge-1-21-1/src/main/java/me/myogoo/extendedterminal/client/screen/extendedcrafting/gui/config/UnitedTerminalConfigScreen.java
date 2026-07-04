package me.myogoo.extendedterminal.client.screen.extendedcrafting.gui.config;

import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.widgets.AECheckbox;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import me.myogoo.myotus.api.config.MyoConfigTabScreen;
import net.minecraft.network.chat.Component;

public class UnitedTerminalConfigScreen implements MyoConfigTabScreen {
    private UnitedTerminalMenu menu;
    private AECheckbox rememberRecipeType;

    @Override
    public void buildTab(WidgetContainer widget, AEBaseScreen<?> screen) {
        if (!(screen.getMenu() instanceof UnitedTerminalMenu menu)) {
            return;
        }

        this.menu = menu;
        this.rememberRecipeType = widget.addCheckbox(
                "rememberRecipeType",
                Component.translatable(ETTranslationKey.GUI.GUI_CONFIG_REMEMBER_UNITED_RECIPE_TYPE.key()),
                this::save
        );
        updateState();
    }

    private void updateState() {
        if (rememberRecipeType != null && menu != null) {
            rememberRecipeType.setSelected(menu.rememberRecipeType());
        }
    }

    private void save() {
        if (menu == null || rememberRecipeType == null) {
            return;
        }

        menu.setRememberRecipeType(rememberRecipeType.isSelected());
    }
}
