package com.david;

import com.david.commands.CommandManager;
import com.david.listeners.EventListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import javax.security.auth.login.LoginException;

public class DiscordBot {

    private final Dotenv config;

    private final ShardManager shardManager;

    public DiscordBot() throws LoginException {
        // Load enviroment variables
        config = Dotenv.configure().load();
        String token = config.get("TOKEN");

        // Build shard manager
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("Discord -> /help"));
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.MESSAGE_CONTENT);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        builder.enableCache(CacheFlag.ONLINE_STATUS);
        builder.enableCache(CacheFlag.VOICE_STATE);
        shardManager = builder.build();

        // Register listener,commandManager etc.
        shardManager.addEventListener(new EventListener());
        shardManager.addEventListener(new CommandManager());
    }

    public Dotenv getConfig(){
        return config;
    }

    public ShardManager getShardManager(){
        return shardManager;
    }

    public static void main(String[] args) {
        try{
            DiscordBot bot = new DiscordBot();
        } catch (LoginException e){
            System.out.println("ERROR: Provided bot token is invalid!");
        }
    }
}
