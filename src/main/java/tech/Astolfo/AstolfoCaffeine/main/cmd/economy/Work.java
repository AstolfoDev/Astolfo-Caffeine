package tech.Astolfo.AstolfoCaffeine.main.cmd.economy;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.bson.Document;
import org.bson.conversions.Bson;
import tech.Astolfo.AstolfoCaffeine.main.db.CloudData;
import tech.Astolfo.AstolfoCaffeine.main.msg.Logging;
import tech.Astolfo.AstolfoCaffeine.main.util.minecraft.Block;
import tech.Astolfo.AstolfoCaffeine.main.util.minecraft.MCgame;
import tech.Astolfo.AstolfoCaffeine.main.util.minecraft.Toolbox;

import java.util.ArrayList;
import java.util.Random;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class Work extends Command {


    private final EventWaiter waiter;


    public Work(EventWaiter waiter) {
        super.name = "work";
        super.aliases = new String[]{"w", "job"};
        super.cooldown = 0;
        super.help = "earn a salary at your company";
        super.category = new Category("economy");
        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent e) {

        Message msg = e.getMessage();
        MessageChannel channel = msg.getChannel();

        //TODO: Move this somewhere else, sould be cahced
        ArrayList<Block> blocks = new ArrayList<Block>() {{
            add(new Block(Block.Material.STONE, Block.BlockStyle.IRON_ORE, 2, 5, 20, 100));
            add(new Block(Block.Material.STONE, Block.BlockStyle.GOLD_ORE, 2, 10, 20, 50));
            add(new Block(Block.Material.STONE, Block.BlockStyle.EMERALD_ORE, 2, 20, 20, 20));
            add(new Block(Block.Material.STONE, Block.BlockStyle.DIAMOND_ORE, 4, 100, 20, 5));
            add(new Block(Block.Material.STONE, Block.BlockStyle.ASTOLFO, 1, 1000, 10, 1));
            //TODO: ADD MORE
        }};

        Random rndGen = new Random();
        int sum = 0;
        for (Block block : blocks) sum += block.rarity;
        int rndVal = rndGen.nextInt(sum);
        int cmlPrb = 0;
        Block selectedBlockTemp = null;
        for (Block block : blocks) {
            cmlPrb += block.rarity;
            if (rndVal <= cmlPrb) {
                selectedBlockTemp = block;
                break;
            }
        }
        final Block selectedBlock = selectedBlockTemp;

        //TODO: Read from mongodb the tools and add to user e.g. 2
        Toolbox toolbox = Toolbox.fromUserID(e.getAuthor().getIdLong());
        channel.sendMessage("**MINEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE**").queue(
                (react_msg) -> channel.sendMessage("Loading...").queue(
                        (block_msg) -> new MCgame(selectedBlock, toolbox, block_msg, react_msg, waiter).runThen(
                                () -> {
                                    if (selectedBlock.state == Block.State.BROKEN) {
                                        react_msg.getChannel().sendMessage("Nice you broke it").queue();
                                        success(msg, selectedBlock);
                                    } else if (selectedBlock.state == Block.State.EXPIRED) {
                                        react_msg.getChannel().sendMessage("You are too slow").queue();
                                    }
                                    react_msg.delete().queue();
                                }
                        )
                )
        );
    }


    // TODO: Fix the spaghetti code in here
    private void success(Message msg, Block mined_block) {

        float cut = 1;

        Document doc = new CloudData().get_data(msg.getAuthor().getIdLong(), CloudData.Database.Economy, CloudData.Collection.wallets);

        int pay = mined_block.value;

        int user_cut = (int) (pay * cut);

        Bson filter2 = eq("userID", msg.getAuthor().getIdLong());
        Bson update = set("credits", doc.getDouble("credits") + user_cut);

        new CloudData().update_data(filter2, update, CloudData.Database.Economy, CloudData.Collection.wallets);


        EmbedBuilder eb = new Logging().embed()
                .setAuthor("Work Complete!", "https://astolfo.tech", msg.getAuthor().getAvatarUrl())
                .setDescription("You earned " + user_cut + " <:credit:738537190652510299> from working!\nYou now have **" + (doc.getDouble("credits") + user_cut) + "** <:credit:738537190652510299>");

        msg.getChannel().sendMessage(eb.build()).queue();
    }
}
