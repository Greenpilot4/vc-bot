package me.greenpilot.vc;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class Bot {

    public Bot() throws LoginException {
       JDABuilder.createDefault(
               config.Get("TOKEN"),
               GatewayIntent.GUILD_MEMBERS,
               GatewayIntent.GUILD_MESSAGES,
               GatewayIntent.GUILD_VOICE_STATES,
               GatewayIntent.GUILD_EMOJIS
       )
               .addEventListeners(new Listener())
               .setActivity(Activity.listening("you"))
               .build();
    }

    public static void main(String[] args) throws LoginException {
        //noinspection InstantiationOfUtilityClass
        new Bot();
    }
}
