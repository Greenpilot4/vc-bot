package me.greenpilot.vc.commands.admin;

import me.greenpilot.vc.Command;
import me.greenpilot.vc.helpers.MongoDB;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SetRoles implements Command {
    @Override
    public void run(List<String> args, GuildMessageReceivedEvent event) {
        String guild_id = event.getGuild().getId();
        String user = event.getAuthor().getId();
        String message = event.getMessage().getContentRaw();

        String member = message.substring(11);
        String[] raw_id = null;

        if(message.contains("<")) {
            raw_id = message.split("[<@!>]+");
            member = raw_id[1];
        }

        List<String> roles = new ArrayList<String>();
        for(String arg: args) {
            // Convert each String arg to char array
            char[] caMainArg = arg.toCharArray();

            // Convert each char array to String
            String strMainArg = new String(caMainArg);

            String role = strMainArg.substring(11);
            String[] raw_role= null;

            if(strMainArg.contains("<")) {
                raw_role = strMainArg.split("[<@&!>]+");
                role = raw_role[1];
            }
            roles.add(role);
        }

        MongoDB.setRoles(guild_id, member, roles);
    }

    @Override
    public String getCommand() {
        return "setRoles";
    }

    @Override
    public String getHelp() {
        return "Gives you the gateway ping of the bot!\n" +
                "Usage: `" + getCommand() + "`";
    }
}
