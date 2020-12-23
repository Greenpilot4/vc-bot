package me.greenpilot.vc;

import com.mongodb.*;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
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
    public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
        if(event.getMember().getUser().isBot())
            return;

        if (!config.Get("TARGET").isEmpty())
            if(!event.getMember().getUser().getId().equals(config.Get("TARGET")))
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

        if (!config.Get("TARGET").isEmpty())
            if(!event.getMember().getUser().getId().equals(config.Get("TARGET")))
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

        if (!MongoDB.getAdmins(event.getGuild().getId()).contains(event.getAuthor().getId()))
            return;

        String msg = event.getMessage().getContentRaw();

        if (msg.startsWith(".set admin ")) {
            String admin = msg.substring(11);
            MongoDB.setAdmin(event.getGuild().getId(), admin);
        }

        if (msg.startsWith(".set target ")) {
            String target = msg.substring(12);
            MongoDB.setTarget(event.getGuild().getId(), target);
        }
    }
}
