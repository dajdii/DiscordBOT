package com.david.commands;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CommandManager extends ListenerAdapter {
    //code here

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        //code here
        String command = event.getName();

        if (command.equals("commands")){
            // Run the '/commands' command
            String userTag = event.getUser().getAsTag();
            event.reply("/help \n /commands \n /say \n /emote").setEphemeral(true).queue();
        }
        // Run the 'help' command
        else if (command.equals("help")) {
            event.reply("Tento BOT byl vytvoreno pro:\n-Prehrani hudby(Zatim nefunguje)\n-Zjisteni Faceit stats(Zatim nefunguje)\n-Ruzne prikazy\n\nPokud nevis nejaky prikaz zadej: /commands").setEphemeral(true).queue();
        }
        // Run the 'roles' command
        else if (command.equals("roles")) {
            event.deferReply().queue();
            String response = "";
            for (Role role : event.getGuild().getRoles()){
                response += role.getAsMention() + "\n";
            }
            event.getHook().sendMessage(response).queue();
        } else if (command.equals("say")) {
            // get message option
            OptionMapping messageOption = event.getOption("message");
            String message = messageOption.getAsString();

            // get channel option
            MessageChannel channel;
            OptionMapping channelOption = event.getOption("channel");
            if (channelOption != null){
                channel = channelOption.getAsChannel().asGuildMessageChannel();
            } else {
                channel = event.getChannel();
            }

            // send message in chat
            channel.sendMessage(message).queue();
            event.reply("Your message was sent!").setEphemeral(true).queue();
        } else if (command.equals("emote")) {
            OptionMapping option = event.getOption("type");
            String type = option.getAsString();

            String replyMessage = "";
            switch (type.toLowerCase()){
                case "hug" -> {
                    replyMessage = "You hug the closest person  to you.";
                }
                case "laugh" -> {
                    replyMessage = "Your laugh hysterically at everyone around you.";
                }
                case "cry" -> {
                    replyMessage = "You can not stop crying.";
                }
            }
            event.reply(replyMessage).queue();
        }
    }

    //Guild command

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        //code here
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("commands", "list of commands"));
        commandData.add(Commands.slash("help", "help info"));
        commandData.add(Commands.slash("roles", "Display all roles on the server"));

        // Command: /say <message> [channel]
        OptionData option1 = new OptionData(OptionType.STRING, "message", "The message you want the bot say", true);
        OptionData option2 = new OptionData(OptionType.CHANNEL, "channel", "The channel you want to send this message in", false).setChannelTypes(ChannelType.TEXT, ChannelType.NEWS, ChannelType.GUILD_PUBLIC_THREAD);
        commandData.add(Commands.slash("say", "Make the BOT say a message").addOptions(option1, option2));

        // Command: /emote [type]
        OptionData option3 = new OptionData(OptionType.STRING, "type", "The type of emotion to express", true).addChoice("Hug", "hug").addChoice("Laugh", "laugh").addChoice("Cry", "cry");
        commandData.add(Commands.slash("emote", "Express your emotions trough text").addOptions(option3));

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}
