package net.dirtydeeds.discordsoundboard.listeners;

import net.dirtydeeds.discordsoundboard.beans.SoundFile;
import net.dirtydeeds.discordsoundboard.service.SoundPlayerImpl;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.Set;

/**
 * author: Dave Furrer
 *
 * This class listens for users to leave a channel and plays a sound if there is one for the user.
 */
public class LeaveSoundBoardListener extends ListenerAdapter {

    private static final Log LOG = LogFactory.getLog("LeaveListener");

    private SoundPlayerImpl bot;
    private String suffix = "_leave";

    public LeaveSoundBoardListener(SoundPlayerImpl bot, String suffix) {
        this.bot = bot;
        if (suffix != null && !suffix.isEmpty()) {
            this.suffix = suffix;
        }
    }

    @SuppressWarnings("rawtypes, unused")
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if(!event.getMember().getUser().isBot()) {
            String userDisconnected = event.getMember().getUser().getName().toLowerCase();

            //Respond
            Set<Map.Entry<String, SoundFile>> entrySet = bot.getAvailableSoundFiles().entrySet();
            if (entrySet.size() > 0) {
                String fileToPlay = "";
                for (Map.Entry entry : entrySet) {
                    String fileEntry = (String) entry.getKey();
                    if (fileEntry.toLowerCase().startsWith(userDisconnected.toLowerCase()) &&
                            fileEntry.toLowerCase().endsWith(suffix.toLowerCase())
                            && fileEntry.length() > fileToPlay.length())
                        fileToPlay = fileEntry;
                }
                if (!fileToPlay.equals("")) {
                    try {
                        bot.playFileForDisconnect(fileToPlay, event);
                    } catch (Exception e) {
                        LOG.fatal("Could not play file for disconnection of " + userDisconnected);
                    }
                } else {
                    LOG.info("Could not disconnection sound for " + userDisconnected + ", so ignoring disconnection event.");
                }
            }
        }
        super.onGuildVoiceLeave(event);
    }
}
