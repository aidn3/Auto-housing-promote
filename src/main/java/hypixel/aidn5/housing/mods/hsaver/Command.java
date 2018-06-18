package hypixel.aidn5.housing.mods.hsaver;

import java.util.ArrayList;
import java.util.List;

import hypixel.aidn5.housing.Common;
import hypixel.aidn5.housing.Config;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class Command extends CommandBase implements ICommand {
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

			if (length == 0) {// No arguments; show usage
				getCommandUsage(sender);
				return;
			}

			for (int i = 0; i < length; i++) {
				args[i] = args[i].toLowerCase(); // make it "unvirsal"
			}

			if (args[0].equals("on")) {
				Main.settings.set("hsaver-toggled", "True");
				showMessage(getCommandName() + " Toggled one", sender);
				return;
			} else if (args[0].equals("off")) {
				Main.settings.set("hsaver-toggled", "False");
				showMessage(getCommandName() + " Toggled off", sender);
				return;

			} else if (args[0].equals("resetall")) {
				if (Main.settings.set("hsaver-toggled", "False") && Main.settings.clear()) {
					showMessage("Clear all AND reset settings for " + getCommandName(), sender);
					return;
				}
				showError("Cannot clear settings and reset all data! :(", sender);
				return;
			} else if (args[0].equals("load")) {
				String username = args[1];// On Excpetion showSyntaxError() will get trigered
				String data = Main.settings.get(username, "");
				if (data == null) {
					showError("Failed loading save for user " + username + " :(", sender);
					return;
				} else if (data == "") {
					showError("Unable to find player " + username + " :(", sender);
					return;
				}
				try {
					String[] coord = data.split("|");
					showMessage(username + "'s Save: " + coord[0] + " / " + coord[1] + " / " + coord[2], sender);
				} catch (Exception e) {
					showError("Unable to fetch the data", sender);
				}
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
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
			aliases.add(command);
		}
		return aliases;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		List<EntityPlayer> players = Common.mc.theWorld.playerEntities;
		List<String> playerNames = new ArrayList();

		for (EntityPlayer player : players) {
			playerNames.add(player.getName());
		}

		return playerNames;
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
				+ (Boolean.valueOf(Main.settings.get("hsaver-toggled", "False")) ? " (Toggled)" : ""), sender);
		showMessage(primary + CMD_NAME + "<off,on>", sender);
		showMessage(primary + CMD_NAME + "resetAll", sender);
		showMessage(primary + CMD_NAME + "load <username>", sender);

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