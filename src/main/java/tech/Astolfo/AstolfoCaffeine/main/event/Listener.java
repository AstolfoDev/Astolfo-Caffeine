package tech.Astolfo.AstolfoCaffeine.main.event;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateAvatarEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import tech.Astolfo.AstolfoCaffeine.main.event.list.Message;
import tech.Astolfo.AstolfoCaffeine.main.event.list.Ready;
import tech.Astolfo.AstolfoCaffeine.main.event.list.SelfAvatar;

import java.io.IOException;

public class Listener implements EventListener {

    @Override
    public void onEvent(GenericEvent e) {
        try {
            switch (e.getClass().getName()) {
                case "net.dv8tion.jda.api.events.ReadyEvent" -> new Ready().onReady((ReadyEvent) e);
                case "net.dv8tion.jda.api.events.self.SelfUpdateAvatarEvent" -> new SelfAvatar().onChange((SelfUpdateAvatarEvent) e);
                case "net.dv8tion.jda.api.events.message.MessageReceivedEvent" -> new Message().run((MessageReceivedEvent) e);
            }
        } catch (IOException err) {
          err.printStackTrace();
        }
    }
}