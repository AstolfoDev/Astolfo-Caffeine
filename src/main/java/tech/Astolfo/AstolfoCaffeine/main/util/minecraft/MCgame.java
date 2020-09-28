package tech.Astolfo.AstolfoCaffeine.main.util.minecraft;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.util.concurrent.TimeUnit;


public class MCgame {

    private Block block;
    private Toolbox inventory;
    private Message host;
    private EventWaiter waiter;
    private Runnable callback;

    public MCgame(Block block, Toolbox inventory, Message host, EventWaiter waiter) {
        this.block = block;
        this.inventory = inventory;
        this.host = host;
        this.waiter = waiter;
    }

    public void runThen(Runnable callback) {
        this.callback = callback;
        block.startTimer();
        inventory.addToMessage(host);
        Message new_content = new MessageBuilder(block.render(host.getJDA())).build();
        host.editMessage(new_content).queue((msg) -> mainLoop());
    }

    private void mainLoop() {
        waiter.waitForEvent(
                GuildMessageReactionAddEvent.class,
                ev -> host.getId().equals(ev.getMessageId()) && !ev.getUser().isBot() && inventory.getTool(ev.getReaction()) != null,
                ev -> {
                    Tool tool = inventory.getTool(ev.getReaction());
                    String block_render = block.hitWith(tool, host.getJDA());
                    if (block_render != null) {
                        host.editMessage(block_render).queue();
                    } if (block.state != Block.State.ACTIVE) {
                        callback.run();
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
