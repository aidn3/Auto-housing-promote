package hypixel.aidn5.housing.commands;

import hypixel.aidn5.housing.config.common;
import hypixel.aidn5.housing.config.consts;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class Promote extends CommandBase implements ICommand {
	private String primary = EnumChatFormatting.AQUA + "";
	private String neutral = EnumChatFormatting.GRAY + "";
	private String secondary = EnumChatFormatting.YELLOW + "";

	// Manage... the process...
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		try {
			// Warn the user about saving data
			if (!common.settings.DIR_CHECKED) showMessage(consts.warning_settings, sender);

			// length will get requested many time in the code; better performence
			int length = args.length;

			if (length == 0) {// No arguments; show usage
				getCommandUsage(sender);
				return;
			}

			for (int i = 0; i < length; i++) {
				args[i] = args[i].toLowerCase(); // make it "unvirsal"
			}

			if (args[0].equals("on")) {
				showMessage(primary + "/" + getCommandName() + " toggled on", sender);
				common.settings.set("toggled", "ON");
				return;
			} else if (args[0].equals("off")) {
				showMessage(primary + "/" + getCommandName() + " toggled off", sender);
				common.settings.set("toggled", "OFF");
				return;

			} else if (args[0].equals("settings")) {
				settingsChange(args, sender);
				return;
			} else if (args[0].equals("debug")) {
				if (args[1].equals("on")) {
					common.settings.set("debug_mode", "True");
					consts.debug_mode = true;
				} else {
					common.settings.set("debug_mode", "False");
					consts.debug_mode = false;
				}
				return;

			} else if (args[0].equals("status")) {
				viewStatus(sender);
				return;

			} else if (args[0].equals("p") || args[0].equals("parkour")) {
				promoteChange("ap-pk", args[1], sender);
				return;
			} else if (args[0].equals("j") || args[0].equals("all")) {
				promoteChange("ap-jn", args[1], sender);
				return;
			} else if (args[0].equals("f") || args[0].equals("friends")) {
				promoteChange("ap-fr", args[1], sender);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		showSyntaxError(sender);
	}

	private void promoteChange(String name, String Key, ICommandSender sender) {
		String settings_value;

		if (Key.equals("co") || Key.equals("co-owner")) settings_value = "Co-Owner";
		else if (Key.equals("res") || Key.equals("resident")) settings_value = "Resident";
		else if (Key.equals("guest")) settings_value = "Guest";
		else if (Key.equals("off")) settings_value = "off";
		else {
			// the user mis-typed
			showSyntaxError(sender);
			return;
		}

		if (common.settings.set(name, settings_value)) {
			return;
		}

		showError(consts.ERERR_SET, sender);
		return;
	}

	private void settingsChange(String[] args, ICommandSender sender) {
		try {
			int length = args.length;

			if (args[1].equals("ranksaver")) {
				if (length == 2) {
					showMessage("Rank Saver: " + common.settings.get("ap-rank_saver", "true"), sender);
					return;
				} else if (args[2].equals("yes") || args[2].equals("y") || args[2].equals("on")) {
					common.settings.get("ap-rank_saver", "true");
					return;
				} else if (args[2].equals("no") || args[2].equals("n") || args[2].equals("off")) {
					common.settings.get("ap-rank_saver", "false");
					return;
				}
			}
		} catch (Exception ignore) {}
		showSyntaxError(sender);
	}

	private void viewStatus(ICommandSender sender) {
		showMessage(primary + "AutoPromote: ", sender);
		showMessage(primary + "Toggled: " + secondary + common.settings.set("toggled", "OFF"), sender);
		showMessage(primary + "All: " + secondary + common.settings.get("ap-jn", "OFF"), sender);
		showMessage(primary + "Friends: " + secondary + common.settings.get("ap-fr", "OFF"), sender);
		showMessage(primary + "Parkour: " + secondary + common.settings.get("ap-pk", "OFF"), sender);
		if (consts.debug_mode) {
			showMessage(primary + "onForce: " + secondary + String.valueOf(common.onForce), sender);
			showMessage(primary + "onHypixel: " + secondary + String.valueOf(common.onHypixel), sender);
			showMessage(primary + "onHousing: " + secondary + String.valueOf(common.onHousing), sender);
		}
	}

	// Manage commands context
	@Override
	public String getCommandName() {
		return "/" + this.getClass().getSimpleName().toLowerCase();
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
		showMessage(secondary + consts.MOD_NAME, sender);
		showMessage(primary + CMD_NAME + "<off,on>", sender);
		showMessage(primary + CMD_NAME + "status", sender);
		showMessage(primary + CMD_NAME + "All(A) <off,res,co>", sender);
		showMessage(primary + CMD_NAME + "Friends(F) <off,res,co>", sender);
		showMessage(primary + CMD_NAME + "Parkour(J) <off,res,co>", sender);

		return "";
	}

	// Manage messages
	public void showMessage(String message, ICommandSender sender) {
		sender.addChatMessage(new ChatComponentText(message));
	}

	public boolean showSyntaxError(ICommandSender sender) {
		showMessage(neutral + "--------------------", sender);
		showMessage(EnumChatFormatting.RED + "Invalid usage: ", sender);
		getCommandUsage(sender);
		return true;
	}

	public void showError(String error, ICommandSender sender) {
		showMessage(neutral + "--------------------", sender);
		showMessage(EnumChatFormatting.RED + "Error: " + error, sender);
	}

}
