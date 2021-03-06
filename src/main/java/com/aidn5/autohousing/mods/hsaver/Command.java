package com.aidn5.autohousing.mods.hsaver;

import java.util.ArrayList;
import java.util.List;

import com.aidn5.autohousing.Common;
import com.aidn5.autohousing.Config;
import com.aidn5.autohousing.utiles.Utiles;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class Command extends CommandBase {
	private String primary = EnumChatFormatting.AQUA + "";
	private String neutral = EnumChatFormatting.GRAY + "";
	private String secondary = EnumChatFormatting.YELLOW + "";
	private String[] commands_name;

	public Command(String[] commands_name_) {
		commands_name = commands_name_;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		try {
			// Warn the user about saving data
			if (!Main.settings.DIR_CHECKED) showMessage(Common.language.get("warning_settings", ""), sender);

			// length will get requested many time in the code; better performence
			int length = args.length;
			String[] orginArgs = args.clone();

			if (length == 0) {// No arguments; show usage
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(Config.refresh_Speed);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						MainGui main = new MainGui(Minecraft.getMinecraft());
						Minecraft.getMinecraft().displayGuiScreen(main);

					}
				}).start();
				return;
			}

			for (int i = 0; i < length; i++) {
				args[i] = args[i].toLowerCase(); // make it "unvirsal"
			}

			if (args[0].equals("reminder")) {
				if (args[1].equals("every")) {
					int timer = Integer.valueOf(args[2]);
					if (timer < 1) throw new Exception("");
					if (!Common.main_settings.set("hsaver-reminder-timer", String.valueOf(timer))) {
						showError(Common.language.get("SET_SAVE_ERR", ""), sender);
						return;
					}
					showMessage(getCommandName() + "-Reminder will send message every " + timer + " minute(s)", sender);
					return;
				}
			} else if (args[0].equals("load")) {
				String username = orginArgs[1];// On Excpetion, showSyntaxError() will get trigered
				Utiles.debug("Hsaver: load info for '" + username + "'...");
				EntityPlayer player = Common.mc.theWorld.getPlayerEntityByName(username);
				if (player == null) {
					showError("Are you sure, the player is around you?", sender);
					return;
				}
				String UUID = EntityPlayer.getUUID(player.getGameProfile()) + "";
				String data = Main.settings.get(UUID, "");

				if (data == null) {
					showError("Failed loading save for user " + username + " :(", sender);
					return;
				} else if (data == "") {
					showError("Unable to find player " + username + " :(", sender);
					return;
				}
				try {
					String[] coord = data.split("!");
					showMessage(username + "'s Save: " + coord[0] + " / " + coord[1] + " / " + coord[2], sender);
				} catch (Exception e) {
					showError("Unable to fetch the data", sender);
				}
				return;
			} else if (args[0].equals("save")) {
				EntityPlayer player = Common.mc.theWorld.getPlayerEntityByName(args[1]);
				if (player == null) {
					showError("Are you sure, the player is around you?", sender);
					return;
				}
				String UUID = EntityPlayer.getUUID(player.getGameProfile()) + "";

				boolean writeStatus = Main.settings.set(UUID,
						(Math.round(player.posX * 1000.0) / 1000.0) + "!" + (Math.round(player.posY * 1000.0) / 1000.0)
								+ "!" + (Math.round(player.posZ * 1000.0) / 1000.0));
				if (writeStatus) {
					showMessage(player.getName() + "'s location saved!", sender);
				} else {
					showError("There was an error saving " + player.getName() + "' location!", sender);
				}
				return;
			}
		} catch (Exception e) {
			Utiles.debug(e);
		}

		showSyntaxError(sender);
	}

	// Manage commands context
	@Override
	public String getCommandName() {
		return "/" + commands_name[0];
	}

	@Override
	public List<String> getCommandAliases() {
		List<String> aliases = new ArrayList();
		for (String command : commands_name) {
			aliases.add("/" + command);
		}
		return aliases;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		int length = args.length;
		List<String> list = new ArrayList();

		if (length == 1) {
			list.add("load");
			list.add("save");
			list.add("reminder");
		} else if (length == 2) {
			if (args[0].equals("load") || args[0].equals("save")) {
				List<EntityPlayer> players = Common.mc.theWorld.playerEntities;

				for (EntityPlayer player : players) {
					list.add(player.getName());
				}
			} else if (args[0].equals("reminder")) {
				list.add("every");
			}
		}

		return list;
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	public boolean canSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		String CMD_NAME = "/" + getCommandName() + " ";
		showMessage(neutral + "--------------------", sender);
		showMessage(secondary + Config.MOD_NAME
				+ (Boolean.valueOf(Common.main_settings.get("hsaver-toggled", "ON").equals("ON")) ? " (Toggled)" : ""),
				sender);
		showMessage(primary + CMD_NAME + "load <username>", sender);
		showMessage(primary + CMD_NAME + "save <username>", sender);
		showMessage(primary + CMD_NAME + "reminder every <time(in minutes)>", sender);

		return "";
	}

	// Manage messages
	public void showMessage(String message, ICommandSender sender) {
		sender.addChatMessage(new ChatComponentText(secondary + message));
	}

	public boolean showSyntaxError(ICommandSender sender) {
		showMessage(EnumChatFormatting.RED + "Invalid usage: ", sender);
		getCommandUsage(sender);
		return true;
	}

	public void showError(String error, ICommandSender sender) {
		showMessage(neutral + "--------------------", sender);
		showMessage(EnumChatFormatting.RED + "Error: " + error, sender);
	}

}
