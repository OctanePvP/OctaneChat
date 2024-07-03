package com.octanepvp.splityosis.octanechat.files;

import com.octanepvp.splityosis.configsystem.configsystem.AnnotatedConfig;
import com.octanepvp.splityosis.configsystem.configsystem.ConfigField;
import com.octanepvp.splityosis.octaneengine.files.logics.ActionsMap;

import java.io.File;

public class AnnouncementsConfig extends AnnotatedConfig {
    public AnnouncementsConfig(File parentDirectory, String name) {
        super(parentDirectory, name);
    }

    @ConfigField(path = "settings.seconds-between-announcements")
    public int secondsBetweenAnnouncements = 60;

    @ConfigField(path = "announcements")
    public ActionsMap actionsMap = new ActionsMap();
}
