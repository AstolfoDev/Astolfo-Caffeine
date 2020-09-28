package tech.Astolfo.AstolfoCaffeine.main.cmd.info;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;

public class Stats extends Command {
  public Stats() {
        super.name = "stats";
        super.aliases = new String[]{"stat","statistics","bot"};
        super.help = "displays some of the bot's information";
        super.category = new Category("info");
  }

  @Override
  protected void execute(CommandEvent e) {
      Message msg = e.getMessage();
      JDA jda = msg.getJDA();
      SelfUser client = jda.getSelfUser();
      //float ram_usage = Float.parseFloat(String.valueOf(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()))/1048576;
      MessageEmbed embed = new EmbedBuilder()
          .setAuthor(client.getName()+" Statistics", "https://astolfo.tech", client.getAvatarUrl())
          .setFooter(System.getenv("VERSION_ID"), client.getAvatarUrl())
          .addField("Shard Latency", "`"+jda.getGatewayPing()+"ms`", true)
          //.addField("Ram Usage (mb)", "`"+ram_usage+"`", true)
          .addField("Website", "https://astolfo.tech", true)
          .addField("Support", "https://discord.gg/RSEpxVJ", true)
          .setColor(0xde1073)
          .build();
      msg.getChannel().sendMessage(embed).queue();
  }
}