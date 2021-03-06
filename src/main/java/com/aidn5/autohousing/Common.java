package com.aidn5.autohousing;

import com.aidn5.autohousing.main.AutoUpdater;
import com.aidn5.autohousing.services.CommandHandler;
import com.aidn5.autohousing.services.InternetHandler;
import com.aidn5.autohousing.services.SettingsHandler;
import com.aidn5.autohousing.utiles.Utiles;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class Common {
	// Mod status
	static public boolean started = false; // determire if the mod finished init();
	static public boolean onForce = false;
	static public boolean autoReconnect = false;
	static public boolean onHypixel = false;
	public static boolean onHousing = false;

	public static String[] test_msgs = new String[] { "aidn3 entered the world" };
	public static String master = "username";

	// shared methods
	static public Minecraft mc; // Save minecraft object for later use; better performence

	// Services
	static public AutoUpdater autoUpdater; // AutoUpdater
	static public CommandHandler commandHandler; // third party to send command instead of directly
	static public InternetHandler internetHandler; // to requesting data from url with callback

	// Settings/SavesData
	static public SettingsHandler language; // Where the language saved
	static public SettingsHandler main_settings; // Main settings ;for the app

	// Send command to the server
	public static void sendCommand(String command) {
		if (command == null || command.isEmpty()) return;
		Utiles.debug("EXECUTING: " + command);
		Common.mc.thePlayer.sendChatMessage(command);
	}

	public static String Owner() {
		return master;
	}

	public static boolean checkHousing() {
		if (!Common.onForce) {
			if (!Common.onHypixel || !Common.onHousing) {
				return false;
			}
		}
		return true;
	}

	public static boolean masterIsOwner() {
		if (Common.onForce) return true;
		EntityPlayer player = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(master);
		if (player == null) {
			Utiles.debug("Master is not here... :/");
			return false;
		}
		Utiles.debug("Player Tag IS: " + player.getCustomNameTag());
		if (player.getCustomNameTag().toLowerCase().contains("[owner]")) {
			Utiles.debug("Player is the OWNER!");
			return true;
		}
		return false;
	}
}
