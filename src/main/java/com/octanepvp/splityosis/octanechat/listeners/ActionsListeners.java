package com.octanepvp.splityosis.octanechat.listeners;

import com.octanepvp.splityosis.octanechat.OctaneChat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ActionsListeners implements Listener {

    private OctaneChat octaneChat;

    public ActionsListeners(OctaneChat octaneChat) {
        this.octaneChat = octaneChat;
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e){

        if (octaneChat.getActionsConfig().disableLoginMessage)
            e.setJoinMessage(null);

        if (octaneChat.getActionsConfig().onFirstLoginEnable && !octaneChat.getDataFile().getConfig().contains("login-times."+e.getPlayer().getUniqueId())){
            octaneChat.getDataFile().getConfig().set("login-times."+e.getPlayer().getUniqueId(), System.currentTimeMillis());
            octaneChat.getActionsConfig().onFirstLoginActions.perform(e.getPlayer(), null);
            return;
        }

        if (octaneChat.getActionsConfig().onLoginEnable)
            octaneChat.getActionsConfig().onLoginActions.perform(e.getPlayer(), null);
    }

    @EventHandler
    public void quit(PlayerQuitEvent e){
        if (octaneChat.getActionsConfig().disableLogoutMessage)
            e.setQuitMessage(null);

        if (octaneChat.getActionsConfig().onLogoutEnable)
            octaneChat.getActionsConfig().onLogoutActions.perform(e.getPlayer(), null);
    }
}
