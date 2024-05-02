package com.octanepvp.splityosis.octanechat.files;

import com.octanepvp.splityosis.configsystem.configsystem.AnnotatedConfig;
import com.octanepvp.splityosis.configsystem.configsystem.ConfigField;
import com.octanepvp.splityosis.configsystem.configsystem.actionsystem.ActionData;
import com.octanepvp.splityosis.configsystem.configsystem.actionsystem.Actions;

import java.io.File;
import java.util.Arrays;

public class ActionsConfig extends AnnotatedConfig {
    public ActionsConfig(File parentDirectory, String name) {
        super(parentDirectory, name);
    }


    @ConfigField(path = "on-login.enable")
    public boolean onLoginEnable = true;

    @ConfigField(path = "on-login.actions")
    public Actions onLoginActions = new Actions(Arrays.asList(new ActionData("MESSAGE", Arrays.asList("&6&lWelcome !!!!!!"))));

    @ConfigField(path = "on-login.disable-login-message")
    public boolean disableLoginMessage = true;

    @ConfigField(path = "on-first-login.enable")
    public boolean onFirstLoginEnable = true;

    @ConfigField(path = "on-first-login.actions")
    public Actions onFirstLoginActions = new Actions(Arrays.asList(new ActionData("MESSAGE", Arrays.asList("&6&lWelcome for the first time!!!!!!"))));

    @ConfigField(path = "on-logout.disable-login-message")
    public boolean disableLogoutMessage = true;

    @ConfigField(path = "on-logout.enable")
    public boolean onLogoutEnable = true;

    @ConfigField(path = "on-logout.actions")
    public Actions onLogoutActions = new Actions(Arrays.asList(new ActionData("TITLEALL", Arrays.asList("&e%player_name% &dhas logged off!", " ", "20", "20", "20"))));

}
