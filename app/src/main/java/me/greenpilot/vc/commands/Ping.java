package me.greenpilot.vc.commands;

import me.greenpilot.vc.Command;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class Ping implements Command {

    @Override
    public void run(List<String> args, GuildMessageReceivedEvent event) {
        if(args.size() == 0) {
            event.getChannel().sendMessage("Pong!").queue(msg -> {
                msg.editMessage("Pong! Latency is " + event.getJDA().getGatewayPing()+"ms.").queue();
            });
        } else {
            event.getChannel().sendMessage("Something went wrong!").queue(msg -> {
            });
        }
    }

    @Override
    public String getCommand() {
        return "ping";
    }

    @Override
    public String getHelp() {
        return "Gives you the gateway ping of the bot!\n" +
                "Usage: `" + getCommand() + "`";
    }

}
