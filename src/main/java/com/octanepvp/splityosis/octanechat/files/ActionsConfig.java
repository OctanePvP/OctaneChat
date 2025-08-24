package com.octanepvp.splityosis.octanechat.files;

import dev.splityosis.sysengine.actions.Actions;
import dev.splityosis.sysengine.actions.ActionsBuilder;
import dev.splityosis.sysengine.configlib.configuration.Configuration;

import java.io.File;
import java.util.Arrays;

public class ActionsConfig implements Configuration {

    @Field
    public boolean onLoginEnable = true;

    @Field
    public Actions onLoginActions = new ActionsBuilder().sendMessage("Welcome").build();

    @Field
    public boolean disableLoginMessage = true;

    @Field
    public boolean onFirstLoginEnable = true;

    @Field
    public Actions onFirstLoginActions =  new ActionsBuilder().sendMessage("Welcome for the first time").build();

    @Field
    public boolean disableLogoutMessage = true;

    @Field
    public boolean onLogoutEnable = true;

    @Field
    public Actions onLogoutActions = new ActionsBuilder().sendTitleAll("%player_name% has logged off!", " ", 20, 20 ,20).build();

}
