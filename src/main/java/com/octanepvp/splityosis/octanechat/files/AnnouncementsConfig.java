package com.octanepvp.splityosis.octanechat.files;

import dev.splityosis.sysengine.actions.Actions;
import dev.splityosis.sysengine.actions.ActionsBuilder;
import dev.splityosis.sysengine.configlib.configuration.Configuration;

import java.util.Map;

public class AnnouncementsConfig implements Configuration {

    @Field
    public int secondsBetweenAnnouncements = 60;

    @Field
    public Map<String, Actions> actionsMap = Map.of("discord", new ActionsBuilder().sendMessage("Join our discord").build());
}
