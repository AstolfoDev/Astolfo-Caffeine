package tech.Astolfo.AstolfoCaffeine.main.cmd.economy;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.BasicDBObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.bson.Document;
import org.bson.conversions.Bson;
import tech.Astolfo.AstolfoCaffeine.App;
import tech.Astolfo.AstolfoCaffeine.main.db.CloudData;
import tech.Astolfo.AstolfoCaffeine.main.db.Database;
import tech.Astolfo.AstolfoCaffeine.main.util.minecraft.Block;
import tech.Astolfo.AstolfoCaffeine.main.util.minecraft.MCgame;
import tech.Astolfo.AstolfoCaffeine.main.util.minecraft.Toolbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class Work extends Command {


    private final EventWaiter waiter;


    public Work(EventWaiter waiter) {
        super.name = "work";
        super.aliases = new String[]{"w","job"};
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
            add(new Block(Block.Material.STONE, Block.BlockStyle.IRON_ORE, 2, 5, 15, 100));
            add(new Block(Block.Material.STONE, Block.BlockStyle.GOLD_ORE, 2, 10, 15, 50));
            add(new Block(Block.Material.STONE, Block.BlockStyle.EMERALD_ORE, 2, 20, 15, 20));
            add(new Block(Block.Material.STONE, Block.BlockStyle.DIAMOND_ORE, 4, 100, 15, 5));
            add(new Block(Block.Material.STONE, Block.BlockStyle.ASTOLFO, 1, 1000, 5, 1));
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

        int cooldown = 3; // TODO: Set to 300
        if (App.cooldown.containsKey(msg.getAuthor().getIdLong())) {
            long time = (System.currentTimeMillis() - App.cooldown.get(msg.getAuthor().getIdLong())) / 1000;
            if (time < cooldown) {
                channel.sendMessage("oi! u gotta wait 4 da cooldown 2 expire before workin' again ;P\n*(" + (cooldown - time) + " seconds leftzZzz...)*").queue();
                return;
            } else {
                App.cooldown.remove(msg.getAuthor().getIdLong());
            }
        }
        App.cooldown.put(msg.getAuthor().getIdLong(), System.currentTimeMillis());

        //TODO: Read from mongodb the tools and add to user e.g. 2
        int toolBits = Objects.requireNonNull(new CloudData().get_data(msg.getAuthor().getIdLong(), "tools").getInteger("tools"));
        Toolbox toolbox = Toolbox.fromBits(toolBits);
        channel.sendMessage("**MINEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE**").queue(
                (react_msg) -> channel.sendMessage("Loading...").queue(
                        (block_msg) -> new MCgame(selectedBlock, toolbox, block_msg, react_msg, waiter).runThen(
                                () -> {
                                    react_msg.delete().queue();
                                    if (selectedBlock.state == Block.State.BROKEN) {
                                        react_msg.getChannel().sendMessage("Nice you broke it").queue();
                                        success(msg);
                                    } else if (selectedBlock.state == Block.State.EXPIRED) {
                                        react_msg.getChannel().sendMessage("You are too slow").queue();
                                    }
                                }
                        )
                )
        );
    }


    // TODO: Fix the spaghetti code in here
    private void success(Message msg) {

        // TODO: This might have a bug, where it always returns null, even when you're in a company
        BasicDBObject filter1 = new BasicDBObject().append("members", new BasicDBObject("$in", Collections.singletonList(msg.getAuthor().getIdLong())));
        Document comp = App.company.find(filter1).first();

        float extra = 0;
        float cut = 1;


        if (comp != null) {
            extra = Float.parseFloat(String.valueOf(0.01 * comp.getInteger("xp")));
            cut = Float.parseFloat(String.valueOf(0.01 * comp.getInteger("cut")));

            Bson up = set("xp", comp.getInteger("xp") + 1);
            App.company.updateOne(filter1, up);

            return;
        }


        Document doc = new Database().get_account(msg.getAuthor().getIdLong());

        int rand = (int) Math.round(Math.random() * 5) + 1;
        int tax = rand/4;
        int pay = rand-tax;

        if (extra != 0) {
          pay = Math.round(rand + extra);
        }

        int user_cut = (int) (pay * cut);
        int comp_cut = pay - user_cut;

        if (comp != null) {
            App.company.updateOne(filter1, set("bank", comp.getInteger("bank") + comp_cut));
        }


        Bson filter2 = eq("userID", msg.getAuthor().getIdLong());
        Bson update = set("credits", doc.getDouble("credits") + user_cut);

        App.col.updateOne(filter2, update);


        EmbedBuilder eb = App.embed()
            .setAuthor("Work Complete!", "https://astolfo.tech", msg.getAuthor().getAvatarUrl())
            .setDescription("You earned "+user_cut+" <:credit:738537190652510299> from working!\nYou now have **"+(doc.getDouble("credits")+user_cut)+"** <:credit:738537190652510299>");


        if (tax > 0) {
            eb.setDescription("You earned "+user_cut+" <:credit:738537190652510299> *(taxed at 25%)* from working!\nYou now have **"+(doc.getDouble("credits")+user_cut)+"** <:credit:738537190652510299>");
        }

        if (comp != null) {
          eb.appendDescription("\nCompany Bonus: `"+extra+"`\nCompany Cut: `"+cut+"`");
        }


        msg.getChannel().sendMessage(eb.build()).queue();
    }
}
