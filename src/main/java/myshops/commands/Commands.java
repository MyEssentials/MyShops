package myshops.commands;

import myshops.proxies.LocalizationProxy;
import mytown.core.Localization;
import mytown.core.utils.command.CommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.util.List;

/**
 * @author Joe Goett
 */
public abstract class Commands {
    public static Localization getLocal() {
        return LocalizationProxy.getLocalization();
    }

    /**
     * Calls the method to which the set of arguments corresponds to.
     */
    public static boolean callSubFunctions(ICommandSender sender, List<String> args, String callersPermNode) {
        List<String> subCommands = CommandManager.getSubCommandsList(callersPermNode);
        if (args.size() > 0) {
            for (String s : subCommands) {
                String name = CommandManager.commandNames.get(s);
                // Checking if name corresponds and if parent's
                if (name.equals(args.get(0)) && CommandManager.getParentPermNode(s).equals(callersPermNode)) {
                    CommandManager.commandCall(s, sender, args.subList(1, args.size()));
                    return true;
                }
            }
        }

        sendHelpMessage(sender, callersPermNode, null);
        return false;
    }

    /**
     * Sends the help message for the permission node with the arguments.
     */
    public static void sendHelpMessage(ICommandSender sender, String permBase, List<String> args) {
        String node;
        if (args == null || args.size() == 0) {
            //If no arguments are provided then we check for the base permission
            node = permBase;
        } else {
            node = CommandManager.getPermissionNodeFromArgs(args, permBase);
        }


        String command = "/" + CommandManager.commandNames.get(permBase);

        if(args != null) {
            String prevNode = permBase;
            for (String s : args) {
                String t = CommandManager.getSubCommandNode(s, prevNode);
                if (t != null) {
                    command += " " + s;
                    prevNode = t;
                } else
                    break;
            }
        }

        sendMessageBackToSender(sender, command);
        List<String> scList = CommandManager.getSubCommandsList(node);
        if (scList == null || scList.size() == 0) {
            sendMessageBackToSender(sender, "   " + getLocal().getLocalization(node + ".help"));
        } else {
            for (String s : scList) {
                sendMessageBackToSender(sender, "   " + CommandManager.commandNames.get(s) + ": " + getLocal().getLocalization(s + ".help"));
            }
        }
    }

    /**
     * Populates the tab completion map.
     */
    public static void populateCompletionMap() {
    }

    public static void sendMessageBackToSender(ICommandSender sender, String message) {
        sender.addChatMessage(new ChatComponentText(message));
    }
}
