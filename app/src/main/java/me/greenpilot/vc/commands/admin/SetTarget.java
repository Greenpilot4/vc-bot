package me.greenpilot.vc.commands.admin;

import me.greenpilot.vc.Command;
import me.greenpilot.vc.helpers.MongoDB;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.Objects;

public class SetTarget implements Command {
    @Override
    public void run(List<String> args, GuildMessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        String guild_id = event.getGuild().getId();
        String user = event.getAuthor().getId();
        TextChannel channel = event.getChannel();

        if(args.isEmpty()) {
            channel.sendMessage("Something went wrong!").queue(msg -> {
            });
        }
        else {
            if (!MongoDB.checkAdmin(guild_id, user) && !user.equals(Objects.requireNonNull(event.getGuild().getOwner()).getId())) {
                channel.sendMessage("You're not an admin!")
                        .queue();
                return;
            }
            String uid = message.substring(11);
            String[] raw_id = null;

            if (message.contains("<")) {
                raw_id = message.split("[<@!>]+");
                uid = raw_id[1];
            }

            MongoDB.setTarget(guild_id, uid);
            channel.sendMessage("Target set!")
                    .queue();
        }
    }

    @Override
    public String getCommand() {
        return "setTarget";
    }

    @Override
    public String getHelp() {
        return "Gives you the gateway ping of the bot!\n" +
                "Usage: `" + getCommand() + "`";
    }
}
