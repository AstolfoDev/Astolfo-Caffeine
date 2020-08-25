package tech.Astolfo.AstolfoCaffeine.main.event;

import java.io.IOException;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import tech.Astolfo.AstolfoCaffeine.main.event.list.Ready;

public class Listener implements EventListener {

    @Override
    public void onEvent(GenericEvent e) {
        try {
            switch(e.getClass().getName()) {
              case "net.dv8tion.jda.api.events.ReadyEvent":
                  new Ready().onReady((ReadyEvent) e);
                  break;
            }
        } catch (IOException err) {
          err.printStackTrace();
        }
    }
}