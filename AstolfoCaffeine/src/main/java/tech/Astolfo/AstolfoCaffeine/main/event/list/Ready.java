package tech.Astolfo.AstolfoCaffeine.main.event.list;

import java.io.IOException;

import net.dv8tion.jda.api.events.ReadyEvent;
import tech.Astolfo.AstolfoCaffeine.App;
import tech.Astolfo.AstolfoCaffeine.main.web.keepOnline;

public class Ready {
  public void onReady(ReadyEvent e) throws IOException {
    keepOnline.innit();
    App.avatarURL = e.getJDA().getSelfUser().getAvatarUrl();
    System.out.println(e.getJDA().getSelfUser().getName()+" is online!");
  }
};