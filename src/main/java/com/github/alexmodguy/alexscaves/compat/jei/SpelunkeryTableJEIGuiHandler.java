package com.github.alexmodguy.alexscaves.compat.jei;

import com.github.alexmodguy.alexscaves.client.gui.SpelunkeryTableScreen;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rect2i;

import java.util.Collections;
import java.util.List;

public class SpelunkeryTableJEIGuiHandler implements IGuiContainerHandler<SpelunkeryTableScreen> {

    @Override
    public List<Rect2i> getGuiExtraAreas(SpelunkeryTableScreen tableScreen) {
        if (tableScreen.hasPaper()) {
            int i = tableScreen.getGuiLeft();
            int j = tableScreen.getGuiTop();
            return Collections.singletonList(new Rect2i(i - 80, j + 5, 80, 145));
        } else {
            return Collections.emptyList();
        }
    }

}
