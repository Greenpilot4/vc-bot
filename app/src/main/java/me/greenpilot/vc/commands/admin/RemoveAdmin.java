package me.greenpilot.vc.commands.admin;

import me.greenpilot.vc.Command;
import me.greenpilot.vc.helpers.MongoDB;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.Objects;

public class RemoveAdmin implements Command {
    @Override
    public void run(List<String> args, GuildMessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        String guild_id = event.getGuild().getId();
        String user = event.getAuthor().getId();
        TextChannel channel = event.getChannel();

        if(!args.isEmpty()) {
            if(!user.equals(Objects.requireNonNull(event.getGuild().getOwner()).getId())) {
                channel.sendMessage("You're not the owner!")
                        .queue();
                return;
            }
            String uid = message.substring(11);
            String[] raw_id = null;

            if(message.contains("<")) {
                raw_id = message.split("[<@!>]+");
                uid = raw_id[1];
            }

            if(!MongoDB.checkAdmin(guild_id, uid)) {
                channel.sendMessage("User not an admin!")
                        .queue();
                return;
            }

            MongoDB.removeAdmin(guild_id, uid);
            channel.sendMessage("Removed user from admins!")
                    .queue();
        } else {
            event.getChannel().sendMessage("Something went wrong!").queue(msg -> {
            });
        }
    }

    @Override
    public String getCommand() {
        return "removeAdmin";
    }

    @Override
    public String getHelp() {
        return "Gives you the gateway ping of the bot!\n" +
                "Usage: `" + getCommand() + "`";
    }
}
