package me.greenpilot.vc;

import me.greenpilot.vc.commands.*;
import me.greenpilot.vc.commands.admin.*;
import me.greenpilot.vc.helpers.Config;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.*;
import java.util.regex.Pattern;

public class Manager {
    private final Map<String, Command> commands = new HashMap<>();

    Manager() {
        addCommand(new Ping());
        addCommand(new SetTarget());
        addCommand(new AddAdmin());
        addCommand(new RemoveAdmin());
        addCommand(new SetRoles());
    }

    private void addCommand(Command c) {
        if(!commands.containsKey(c.getCommand())) {
            commands.put(c.getCommand(), c);
        }
    }

    public Collection<Command> getCommands() {
        return commands.values();
    }

    public Command getCommand(String commandName) {
        if(commandName == null) {
            return null;
        }
        return commands.get(commandName);
    }

    void run(GuildMessageReceivedEvent event) {
        final String msg = event.getMessage().getContentRaw();
        if(!msg.startsWith(Config.get("PREFIX"))) {
            return;
        }
        final String[] split = msg.replaceFirst("(?i)" + Pattern.quote(Config.get("PREFIX")), "").split("\\s+");
        final String command = split[0];
        if(commands.containsKey(command)) {
            final List<String> args = Arrays.asList(split).subList(1, split.length);
            commands.get(command).run(args, event);
        }
    }
}
