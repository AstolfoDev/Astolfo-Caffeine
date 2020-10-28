package tech.Astolfo.AstolfoCaffeine.main.event.list;

import net.dv8tion.jda.api.events.self.SelfUpdateAvatarEvent;
import tech.Astolfo.AstolfoCaffeine.App;
import tech.Astolfo.AstolfoCaffeine.main.util.caching.Cache;

public class SelfAvatar
{
    public void onChange(SelfUpdateAvatarEvent e)
    {
        App.avatarURL = e.getNewAvatarUrl();
        Cache.avatarURL = e.getNewAvatarUrl();
    }
}