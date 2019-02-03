package net.dirtydeeds.discordsoundboard.listeners;

import net.dirtydeeds.discordsoundboard.beans.SoundFile;
import net.dirtydeeds.discordsoundboard.service.SoundPlayerImpl;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.Set;

/**
 * @author asafatli.
 *
 * This class handles waiting for people to enter a discord voice channel and responding to their entrance.
 */
public class EntranceSoundBoardListener extends ListenerAdapter {

    private static final Log LOG = LogFactory.getLog("EntranceListener");

    private SoundPlayerImpl bot;

    public EntranceSoundBoardListener(SoundPlayerImpl bot) {
        this.bot = bot;
    }

    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        voiceEntrance(event, event.getChannelJoined());
        super.onGuildVoiceMove(event);
    }

    @SuppressWarnings("rawtypes, unused")
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        voiceEntrance(event, event.getChannelJoined());
        super.onGuildVoiceJoin(event);
    }

    private void voiceEntrance(GenericGuildVoiceEvent event, VoiceChannel channel) {
        if(!event.getMember().getUser().isBot()) {
            String joined = event.getMember().getUser().getName().toLowerCase();

            //Respond
            Set<Map.Entry<String, SoundFile>> entrySet = bot.getAvailableSoundFiles().entrySet();
            if (entrySet.size() > 0) {
                String fileToPlay = "";
                for (Map.Entry entry : entrySet) {
                    String fileEntry = (String) entry.getKey();
                    if (joined.toLowerCase().startsWith(fileEntry.toLowerCase())
                            && fileEntry.length() > fileToPlay.length())
                        fileToPlay = fileEntry;
                }
                if (!fileToPlay.equals("")) {
                    try {
                        bot.playFileForEntrance(fileToPlay, event, channel);
                    } catch (Exception e) {
                        LOG.fatal("Could not play file for entrance of " + joined);
                    }
                } else {
                    LOG.info("Could not find any sound that starts with " + joined + ", so ignoring entrance.");
                }
            }
        }
    }
}