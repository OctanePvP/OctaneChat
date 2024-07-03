package com.octanepvp.splityosis.octanechat.objects;

import com.octanepvp.splityosis.configsystem.configsystem.actionsystem.Actions;
import com.octanepvp.splityosis.octanechat.OctaneChat;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class AnnouncementScheduler {
    public static AnnouncementScheduler currentSchedule;

    private boolean on;
    public AnnouncementScheduler() {
        on = true;
        currentSchedule = this;
        startSchedule();
    }

    public void startSchedule(){
        new BukkitRunnable() {
            @Override
            public void run() {
                if (on){
                    Actions actions = getRandomEntry(OctaneChat.announcementsConfig.actionsMap.values());
                    actions.performOnAll();
                    return;
                }
                this.cancel();
            }
        }.runTaskTimer(OctaneChat.plugin, 0, OctaneChat.announcementsConfig.secondsBetweenAnnouncements* 20L);
    }

    public static <T> T getRandomEntry(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return null; // or throw an exception if you prefer
        }

        int size = collection.size();
        int itemIndex = new Random().nextInt(size);

        if (collection instanceof List) {
            return ((List<T>) collection).get(itemIndex);
        } else {
            int currentIndex = 0;
            for (T item : collection) {
                if (currentIndex == itemIndex) {
                    return item;
                }
                currentIndex++;
            }
        }
        return null; // should never reach here if collection is not empty
    }

    public static void reloadSchedule(){
        if (currentSchedule == null){
            new AnnouncementScheduler();
            return;
        }
        currentSchedule.on = false;
        new AnnouncementScheduler();
    }
}
