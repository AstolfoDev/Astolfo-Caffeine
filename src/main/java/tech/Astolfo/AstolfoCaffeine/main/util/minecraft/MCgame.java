package tech.Astolfo.AstolfoCaffeine.main.util.minecraft;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.util.concurrent.TimeUnit;

// ISSUES:
// When spamming reactions the client can sorta follow, but the animations can't, which results in them being queued and therefore seemingly reacting themselves after you stop spamming. ?????????
// The server cannot follow though, and a lot of the reactions will be denied with 429. The bot therefore only gets some of the reactions. ?????????
public class MCgame {

    private Block block;
    private Toolbox inventory;
    private Message react_host;
    private Message block_host;
    private EventWaiter waiter;
    private Runnable callback;

    public MCgame(Block block, Toolbox inventory, Message block_host, Message react_host, EventWaiter waiter) {
        this.block = block;
        this.inventory = inventory;
        this.block_host = block_host;
        this.react_host = react_host;
        this.waiter = waiter;
    }

    public void runThen(Runnable callback) {
        this.callback = callback;
        block.startTimer();
        inventory.addToMessage(react_host);
        block_host.editMessage(block.render()).queue((msg) -> {
            mainLoop();
        });
    }

    private void mainLoop() {
        waiter.waitForEvent(
                GuildMessageReactionAddEvent.class,
                ev -> react_host.getId().equals(ev.getMessageId()) && !ev.getUser().isBot() && inventory.getTool(ev.getReaction()) != null,
                ev -> {
                    Tool tool = inventory.getTool(ev.getReaction());
                    String block_render = block.hitWith(tool);
                    if (block.state != Block.State.ACTIVE) {
                        block_host.editMessage(block.render()).queue();
                        callback.run();
                        return;
                    }
                    ev.getReaction().removeReaction(ev.getUser()).queue();
                    if (block_render != null) {
                        block_host.editMessage(block_render).queue((m) -> mainLoop());
                        return;
                    }
                    mainLoop();
                },
                block.leftTime(), TimeUnit.SECONDS,
                () -> {
                    block.state = Block.State.EXPIRED;
                    callback.run();
                }
        );
    }
}
