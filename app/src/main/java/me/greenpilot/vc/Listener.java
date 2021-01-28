package me.greenpilot.vc;

import me.greenpilot.vc.helpers.MongoDB;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class Listener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);

    public final Manager m = new Manager();

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
        if(!MongoDB.checkTarget(event.getGuild().getId(), event.getMember().getId()))
            return;

        AudioManager audioManager = event.getGuild().getAudioManager();

        audioManager.closeAudioConnection();
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        m.run(event);
    }

    @Override
    public void onGuildBan(@NotNull GuildBanEvent event) {
        User user = event.getUser();
        Guild guild = event.getGuild();
        TextChannel channel = guild.getDefaultChannel();
        assert channel != null;
        String invite = channel.createInvite().setMaxUses(1).complete().getUrl();

        event.getGuild().unban(user).queue();

        user.openPrivateChannel().queue((private_channel) ->
        {
            private_channel.sendMessage(invite).queue();
        });
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Member member = event.getMember();
        User user = event.getUser();
        Guild guild = event.getGuild();

        Role big_iq = guild.getRoleById("695349368986927105");
        Role pokemon = guild.getRoleById("784661486806761483");
        Role sexc = guild.getRoleById("784937027900407838");

        if(member.getId().equals("788543463477215303")) {
            guild.modifyMemberRoles(member, big_iq, pokemon, sexc).queue();
        }

        List<String> roles = MongoDB.checkRoles(guild.getId(), member.getId());

        for(String role : roles) {
            System.out.println(role);
        }
    }
}
