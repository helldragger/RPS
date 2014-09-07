package helldragger.RolePlaySpeciality;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.mewin.WGCustomFlags.WGCustomFlagsPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.BooleanFlag;




public class RPSPlugin extends JavaPlugin {
	
	
	public PluginManager pm;

	public PluginDescriptionFile pdf;
	public static Logger log;
	

	Commands cmdListener;
	Events events;
	
	WorldGuardPlugin wgp;
	WGCustomFlagsPlugin cf;
	public BooleanFlag RP = new BooleanFlag("RP");

	@Override
	public void onEnable() // Déclenchée lors du démarrage du serveur
	{
		
		pm = getServer().getPluginManager();
		pdf = getDescription();
		log = Logger.getLogger("Minecraft");
		cmdListener = new Commands(this);
		events = new Events(this);
		pm.registerEvents(events, this);
		
		
			
		Permissions.BPERMISSIONS = pm.getPlugin("bPermissions") != null;
			
		getCommand("rp").setExecutor(cmdListener);
		getCommand("do").setExecutor(cmdListener);
		getCommand("think").setExecutor(cmdListener);
		getCommand("shout").setExecutor(cmdListener);
		getCommand("rpreload").setExecutor(cmdListener);
		getCommand("destin").setExecutor(cmdListener);
		getCommand("loot").setExecutor(cmdListener);
		getCommand("spy").setExecutor(cmdListener);
			
		log.info("Chargement de la configuration en cours...");
		Config.loadConfiguration( getDataFolder().getPath(),this );
		
		if(Config.WORLD_GUARD_RP_FLAG)
		{
			wgp = getWorldGuard();

			if (wgp instanceof WorldGuardPlugin && wgp != null)
			{
				cf = getWGCustomFlags();

				if (cf instanceof WGCustomFlagsPlugin && cf != null)
				{
					
					cf.addCustomFlag(RP);

				}
				else
				{
					log.warning("WorldGuard customFlags non présent, régions anti rp inactives!");

					Config.WORLD_GUARD_RP_FLAG = false;
				}
			}
			else
			{
				log.warning("WorldGuard non présent, régions anti rp inactives!");
				Config.WORLD_GUARD_RP_FLAG = false;
			}
		}
		
		log.info("Chargement des joueurs connectés");
		Player[] playersOnline =Bukkit.getOnlinePlayers();
		if (playersOnline.length != 0){
			for (Player player : playersOnline){
				Commands.playerRPenabled.put(player.getName(), false);
				Commands.playerSessions.put(player.getName(), Config.DEFAULT_COLOR);
			}
		}
		
		log.info(Bukkit.getOnlinePlayers().length+" players online and RPS list are now up to date!");
		
		log.info("Le RolePlay est pret à l'action!");
	}
	

	

	
	@Override
	public void onDisable() // Déclenchée lors de l'arrêt du serveur
	{
		log.info("Le serveur se ferme, aussitot apres le RolePlay s'endors...");
		Commands.playerSessions.clear();
		Commands.playerRPenabled.clear();
		

		
	
	}
	
	public void onReload()
	{
	    this.reloadConfig();
	    
		log.info(ChatColor.GOLD+"RolePlaySpeciality reloaded!");
		Bukkit.broadcastMessage(ChatColor.GOLD+"["+ChatColor.WHITE+"RolePlaySpeciality"+ChatColor.GOLD+"] reloaded, restoring everybody to default RP color and to the general chat.");
	}
	
	
	void launchRPFlag()
	{
		
	}


	 WGCustomFlagsPlugin getWGCustomFlags()
	{
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WGCustomFlags");

		if (plugin == null || !(plugin instanceof WGCustomFlagsPlugin))
		{
			return null;
		}

		return (WGCustomFlagsPlugin) plugin;
	}



	 WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null; // Maybe you want throw an exception instead
		}

		return (WorldGuardPlugin) plugin;
	}



	
/*
 * on entre une commande:
 * command (player, message)
 * on regarde sa commande:
 * /rp seul = activation desactivation (couleurs par defaut)
 * /rp + autre chose = activation ou update de la couleur du joueur.
 * /rp list donne une liste des couleurs différentes.
 * /d qui lance les dés du destin.
 *  Limite de distance. 
 * TODO Securité anti spam (/d) a faire
 *
 * 
 * 
 * sessions joueurs:
 * hashmap <Joueur, etat (Boolean)>
 * 
 * session active =
	 * joueur - couleur + position
	 * les deux hashmap:
	 * couleur: <Joueur, couleur>
	 * position: <Joueur, position>
	 * position recalculée à chaque message envoyé en RP
 * 
 * session desactive =
 * 
 * 
 * 
 * 
 * 
 * 
 */
	
	
	
	

}


//module Weaponlevels : classes
