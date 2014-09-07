package helldragger.RolePlaySpeciality;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class Events implements Listener{

	private RPSPlugin rps;
	
	public Events(RPSPlugin plugin){

		this.rps = plugin;

	}





	 boolean zoneRP(Player player)
	{
		if (Config.WORLD_GUARD_RP_FLAG){
			if(rps.getWorldGuard().getRegionManager(player.getWorld()) != null)
			{
				RegionManager rgm = rps.getWorldGuard().getRegionManager(player.getWorld());
				if(rgm.getApplicableRegions(player.getLocation()) != null)
				{
					ApplicableRegionSet rg = rgm.getApplicableRegions(player.getLocation());
					
					if (rg.getFlag(rps.RP) != null)
					{
						boolean regionRPFlag = rg.getFlag(rps.RP).booleanValue();
						if (!regionRPFlag)
						{

						}
					}else{

						player.sendMessage(ChatColor.RED+"Le roleplay n'est pas permis dans ce monde.");
						return false;
					}
				}else RPSPlugin.log.warning("region world guard introuvée");
			}else RPSPlugin.log.warning("Region manager introuvé");
		}

		return true;

	}



	//Lors de la connexion d'un joueur: L'ajouter a la liste des joueurs présents.
	@EventHandler
	public void onConnect(PlayerJoinEvent joiningPlayer){

		Player player = joiningPlayer.getPlayer();
		Commands.playerSessions.put(joiningPlayer.getPlayer().getName(),Config.DEFAULT_COLOR);
		Commands.playerRPenabled.put(joiningPlayer.getPlayer().getName(), false);

		if(Permissions.hasPermission(player, "bypass"))
		{
			Commands.allowedToBypassVoiceDistance.put(player, true);
			Commands.BypassVoiceDistanceMode.put(player, false);
		}
		else
			Commands.allowedToBypassVoiceDistance.put(player, false);




		return;
	}



	//Lors de la deconnexion d'un joueur: Le retirer de la liste des joueurs présents.
	@EventHandler
	public void onDisconnect(PlayerQuitEvent exitingPlayer){

		Commands.playerSessions.remove(exitingPlayer.getPlayer().getName());
		Commands.playerRPenabled.remove(exitingPlayer.getPlayer().getName());
		Commands.allowedToBypassVoiceDistance.remove(exitingPlayer.getPlayer());
		Commands.BypassVoiceDistanceMode.remove(exitingPlayer.getPlayer());


		return;
	}

	//Changement des couleurs de messages dans le chat:
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent chat){
		String sender = chat.getPlayer().getName();
		String message = chat.getMessage();
		if(Commands.playerRPenabled.get(sender) == true){ //si le mode RP est activé

			if(!zoneRP(chat.getPlayer()))
			{
				chat.setCancelled(true);
				return;
			}


			if (Config.RP_RANGE_ENABLED)
			{


				//mise en forme du message
				message = (ChatColor.valueOf(Commands.playerSessions.get(sender))+message);

				//recuperation de la liste de joueurs proches
				ArrayList<Player> listeJoueursProches = Commands.messageRP(sender);

				for(Player player : Commands.BypassVoiceDistanceMode.keySet())
				{
					if (Commands.BypassVoiceDistanceMode.get(player))
						if (!listeJoueursProches.contains(player))
							listeJoueursProches.add(player);
				}

				//envoi du message a chaque joueur proche. 
				for (Player players : listeJoueursProches){
					String messageFormat 	= ChatColor.valueOf(Commands.playerSessions.get(sender))+"*"+ChatColor.WHITE+sender;
					String messageText 		= ChatColor.valueOf(Commands.playerSessions.get(sender))+" "+ message + "*";
					String SentMessage 		= messageFormat + messageText;
					players.sendMessage(SentMessage);
				}


			}else{

				message =ChatColor.valueOf(Commands.playerSessions.get(sender))+" "+ message + "*";
				chat.setMessage(message);

				String format =ChatColor.valueOf(Commands.playerSessions.get(sender))+"*"+ChatColor.WHITE+sender;
				chat.setFormat(format);

			}


			chat.setCancelled(true);


		}else{ //si le mode RP n'est pas activé
		}


		return;
	}

}
