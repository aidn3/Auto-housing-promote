package com.aidn5.autohousing.mods.hsaver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.aidn5.autohousing.Common;
import com.aidn5.autohousing.services.GuiHandler;
import com.aidn5.autohousing.utiles.Message;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class MainGui extends GuiHandler {
	private List<List<String>> hoverText;

	public MainGui(Minecraft mc) {
		super(mc);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		for (int i = 0; i < buttonList.size(); i++) {
			if (buttonList.get(i) instanceof GuiButton) {
				GuiButton btn = buttonList.get(i);
				if (btn.isMouseOver()) {
					drawHoveringText(hoverText.get(i), mouseX, mouseY);
				}
			}
		}
	}

	private List<String> toolTipText(String[] string) {
		List<String> list = new ArrayList<String>();
		for (String tip : string) {
			list.add(tip);
		}
		return list;
	}

	@Override
	public void initGui() {
		initGui_();
		super.initGui();
	}

	public void initGui_() {
		buttonList = new ArrayList();
		hoverText = new ArrayList();

		buttonList.add(new GuiButton(1, width / 2 - 70, height / 2 - 50, 140, 20,
				"feature: toggled" + checkStatus(Common.main_settings.get("hsaver-toggled", "ON").equals("ON"))));
		hoverText.add(toolTipText(new String[] { "Self. The mod." }));

		buttonList.add(new GuiButton(2, width / 2 - 70, height / 2 - 28, 140, 20, "Reminder: toggled"
				+ checkStatus(Common.main_settings.get("hsaver-reminder-toggled", "ON").equals("ON"))));
		hoverText.add(
				toolTipText(new String[] { "Remind people to save their locations", "every (default 10 minutes)." }));

		buttonList.add(new GuiButton(3, width / 2 - 70, height / 2 - 6, 140, 20, "Reset All"));
		hoverText.add(toolTipText(new String[] { "Remove all player data", "(saved locations)." }));
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if (button.enabled) {
			if (button.id == 1) {
				Common.main_settings.set("hsaver-toggled",
						(Common.main_settings.get("hsaver-toggled", "ON").equals("ON") ? "OFF" : "ON"));
			} else if (button.id == 2) {
				Common.main_settings.set("hsaver-reminder-toggled",
						(Common.main_settings.get("hsaver-reminder-toggled", "ON").equals("ON") ? "OFF" : "ON"));
			} else if (button.id == 3) {
				Main.settings.clear();
				Message.showMessage(Common.language.get("RESET_SET", ""));
				if (!Main.settings.SaveUserSettings()) {
					Message.showMessage(Common.language.get("SET_SAVE_ERR", ""));
					return;
				}
			}
		}
		initGui();
	}

}
