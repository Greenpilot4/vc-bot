package me.greenpilot.vc;

import com.mongodb.*;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.util.Objects;

public class Listener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        LOGGER.info("{} is ready", event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        String guild = event.getGuild().getId();
        String guild_name = event.getGuild().getName();

        MongoDB.makeGuildDoc(guild, guild_name);
    }

        @Override
    public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
        if(event.getMember().getUser().isBot())
            return;

        if(!MongoDB.checkTarget(event.getGuild().getId(), event.getMember().getId()))
            return;

        VoiceChannel connectedChannel = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();
        AudioManager audioManager = event.getGuild().getAudioManager();

        audioManager.openAudioConnection(connectedChannel);

        LOGGER.info("Joined channel with {}", event.getMember().getUser().getAsTag());
    }

    @Override
    public void onGuildVoiceMove(@Nonnull GuildVoiceMoveEvent event) {
        if(event.getMember().getUser().isBot())
            return;

        if(!MongoDB.checkTarget(event.getGuild().getId(), event.getMember().getId()))
            return;

        VoiceChannel connectedChannel = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();
        AudioManager audioManager = event.getGuild().getAudioManager();

        audioManager.openAudioConnection(connectedChannel);

        LOGGER.info("Joined channel with {}", event.getMember().getUser().getAsTag());
    }

    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
        if(event.getMember().getUser().isBot())
            return;

        AudioManager audioManager = event.getGuild().getAudioManager();

        audioManager.closeAudioConnection();
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        String guild_id = event.getGuild().getId();
        String user = event.getAuthor().getId();
        TextChannel channel = event.getChannel();

        if(message.startsWith(".add admin ")) {
            if(!MongoDB.checkAdmins(guild_id, user) && !user.equals(Objects.requireNonNull(event.getGuild().getOwner()).getId())) {
                channel.sendMessage("You're not an admin!")
                        .queue();
                return;
            }
            String uid = message.substring(11);
            String[] raw_id = null;

            if(message.contains("<")) {
                raw_id = message.split("[<@!>]+");
                uid = raw_id[1];
            }

            MongoDB.addAdmin(guild_id, uid);
            channel.sendMessage("Added user to admins!")
                    .queue();
        }
        if(message.startsWith(".remove admin ")) {
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

            MongoDB.removeAdmin(guild_id, uid);
            channel.sendMessage("Removed user from admins!")
                    .queue();
        }
        if(message.startsWith(".set target ")) {
            if(!MongoDB.checkAdmins(guild_id, user) && !user.equals(Objects.requireNonNull(event.getGuild().getOwner()).getId())) {
                channel.sendMessage("You're not an admin!")
                        .queue();
                return;
            }
            String uid = message.substring(11);
            String[] raw_id = null;

            if(message.contains("<")) {
                raw_id = message.split("[<@!>]+");
                uid = raw_id[1];
            }

            MongoDB.setTarget(guild_id, uid);
            channel.sendMessage("Target set!")
                    .queue();
        }
    }
}
