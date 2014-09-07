package helldragger.RolePlaySpeciality;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class Config {


	public static boolean USE_PERMS;

	public static boolean WORLD_GUARD_RP_FLAG;

	public static boolean RP_ENABLED;
	public static boolean RP_RANGE_ENABLED;

	public static boolean SHOUT_ENABLED;
	public static boolean SHOUT_RANGE_ENABLED;

	public static boolean THINK_ENABLED; 
	public static boolean THINK_RANGE_ENABLED;

	public static boolean DO_ENABLED;
	public static boolean DO_RANGE_ENABLED;

	public static boolean DESTIN_ENABLED;
	public static boolean DESTIN_RANGE_ENABLED;
	public static int WIN_CHANCE;

	public static boolean LOOT_ENABLED;
	public static boolean LOOT_RANGE_ENABLED;

	public static boolean RP_RELOAD_ENABLED;

	public static boolean SPY_ENABLED;


	public static boolean VOICE_RANGE_ENABLED;
	public static int VOICE_RANGE;


	public static ArrayList<String> AUTORISED_COLORS = new ArrayList<String>();
	public static ArrayList<String> FORBIDDEN_COLORS = new ArrayList<String>();
	public static String DEFAULT_COLOR;


	private static final String CONFIG_DEFAULT_COLOR = "DARK_GREEN";


	public static final ArrayList<String> COLORS = new ArrayList<String>(Arrays.asList(

			"DARK_BLUE",
			"DARK_AQUA",
			"DARK_RED",
			"DARK_PURPLE",
			"DARK_GRAY",
			"DARK_GREEN",
			"LIGHT_PURPLE",

			"GOLD",
			"GRAY",
			"BLACK",
			"BLUE",
			"GREEN",
			"AQUA",
			"RED",
			"YELLOW",
			"WHITE",

			"MAGIC",
			"BOLD",
			"STRIKETHROUGH",
			"UNDERLINE",
			"ITALIC",
			"RESET"

			));

	private static String ENABLED_COMMANDS_PATH = "Enabled commands configuration.";
	private static String VOICE_RANGE_PATH = "Voice range configuration.";
	private static String DESTINY_ROLL_DICE_PATH = "Destiny roll dice configuration.";
	private static String RP_CHAT_COLORS_PATH = "RP chat colors configuration.";


	@SuppressWarnings("unchecked")
	public static void loadConfiguration(String data_PATH,RPSPlugin plugin){
		File Config = new File(data_PATH, "config.yml");
		FileConfiguration Configfile = YamlConfiguration.loadConfiguration(Config);

		if(!Config.exists()){
			try
			{
				createDefaultConfigFile(plugin);
			}
			catch(Exception e)
			{
				RPSPlugin.log.severe("Error when trying to create updated config file!");
			}
		}
		else if (Configfile.get("module WeaponsLevel actif") != null
				|| Configfile.get("enabled worlds") != null
				|| Configfile.get("Display player name") != null)
		{
			deleteOldData(Config);
			try
			{
				createDefaultConfigFile(plugin);
			}
			catch(Exception e)
			{
				RPSPlugin.log.severe("Error when trying to create updated config file!");
			}

		}
		Configfile = YamlConfiguration.loadConfiguration(Config);


		/*
		 * 
		 * Chargement du fichier
		 */
		try{



			//chargement couleurs 

			FORBIDDEN_COLORS = (ArrayList<String>) Configfile.get(RP_CHAT_COLORS_PATH+ "Forbidden Colors");
			AUTORISED_COLORS.clear();
			if (FORBIDDEN_COLORS != null)
			{

				for (int index=0; index != COLORS.size();index++){
					if ( !FORBIDDEN_COLORS.contains( COLORS.get( index ) ) ){//si une couleur de COLORS[] n'apparait pas dans la liste noire, celle ci est ajoutée a la liste autorisée si elle ne l'as pas déjà
						AUTORISED_COLORS.add(COLORS.get(index));
					}

				}
				for (int index =0;index != FORBIDDEN_COLORS.size();index++){
					if (!COLORS.contains(FORBIDDEN_COLORS.get(index))){//si une couleur interdite n'appartient pas a aucune des valeurs des couleurs 
						FORBIDDEN_COLORS.remove(index);
					}
				}

			}
			else
			{
				AUTORISED_COLORS = COLORS;
			}
			if (COLORS.contains( (String) Configfile.get("DefaultRPcolor") ) ){
				DEFAULT_COLOR = (String) Configfile.get("DefaultRPcolor");
			}
			else{
				DEFAULT_COLOR = CONFIG_DEFAULT_COLOR;
			}

			WORLD_GUARD_RP_FLAG = (boolean)Configfile.get( ENABLED_COMMANDS_PATH +"Use WG RP region flag");
			USE_PERMS			= (boolean)Configfile.get( ENABLED_COMMANDS_PATH +"Permissions");
			RP_ENABLED 			= (boolean)Configfile.get( ENABLED_COMMANDS_PATH +"RP");
			LOOT_ENABLED		= (boolean)Configfile.get( ENABLED_COMMANDS_PATH +"Loot");
			SHOUT_ENABLED		= (boolean)Configfile.get( ENABLED_COMMANDS_PATH +"Shout");
			THINK_ENABLED		= (boolean)Configfile.get( ENABLED_COMMANDS_PATH +"Think"); 
			DO_ENABLED 			= (boolean)Configfile.get( ENABLED_COMMANDS_PATH +"Do");
			DESTIN_ENABLED 		= (boolean)Configfile.get( ENABLED_COMMANDS_PATH +"Destin");
			RP_RELOAD_ENABLED 	= (boolean)Configfile.get( ENABLED_COMMANDS_PATH +"RPReload");


			try{
				SPY_ENABLED 	= (boolean)Configfile.get( ENABLED_COMMANDS_PATH +"Spy");
			}
			catch (Exception e)
			{
				SPY_ENABLED		= true;
				Configfile.set( ENABLED_COMMANDS_PATH +"Spy",true);
			}


			//chargement distance de voix
			VOICE_RANGE_ENABLED = (boolean) Configfile.get( ENABLED_COMMANDS_PATH +"Voice Range");
			if (VOICE_RANGE_ENABLED){

				LOOT_RANGE_ENABLED	= (boolean) Configfile.get(VOICE_RANGE_PATH +"Loot voice range enabled");
				RP_RANGE_ENABLED 	= (boolean) Configfile.get(VOICE_RANGE_PATH +"RP voice range enabled");
				DESTIN_RANGE_ENABLED= (boolean) Configfile.get(VOICE_RANGE_PATH +"Destin voice range enabled");
				DO_RANGE_ENABLED 	= (boolean) Configfile.get(VOICE_RANGE_PATH +"Do voice range enabled");
				THINK_RANGE_ENABLED = (boolean) Configfile.get(VOICE_RANGE_PATH +"Think voice range enabled");
				SHOUT_RANGE_ENABLED = (boolean) Configfile.get(VOICE_RANGE_PATH +"Shout voice range enabled");
				VOICE_RANGE = (int) Configfile.get(VOICE_RANGE_PATH +"Voice range");
			}
			else{
				LOOT_RANGE_ENABLED	= false;
				RP_RANGE_ENABLED 	= false;
				DESTIN_RANGE_ENABLED= false;
				DO_RANGE_ENABLED 	= false;
				THINK_RANGE_ENABLED = false;
				SHOUT_RANGE_ENABLED = false;
				VOICE_RANGE = 0;
			}







			//chargement chances des dés du destin

			WIN_CHANCE = (int) Configfile.get(DESTINY_ROLL_DICE_PATH +"Win chance percentage (100 and more to always win)");



			//chargement modules activés


			//mise a jour des renseignements complémentaires

			Configfile.save(Config);
		}catch(Exception e){
			e.printStackTrace();
		}


		return;
	}

	private static void createDefaultConfigFile(RPSPlugin plugin) throws IOException {

		File folder = plugin.getDataFolder();

		String dataPath = folder.getPath() + File.separator + "configuration" + File.separator;

		File configFile = new File(dataPath + "config.yml");

		if (!configFile.exists())
		{
			InputStream is = null;
			OutputStream os = null;
			try {
				is = plugin.getResource("config.yml");
				os = new FileOutputStream(configFile);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = is.read(buffer)) > 0) {
					os.write(buffer, 0, length);
				}
			} finally {
				is.close();
				os.close();
			}


		}
	}

	private static void deleteOldData(File Configfile){
		Configfile.delete();
	}

	public static void switchRPUse(){
		RP_ENABLED = !RP_ENABLED;

	}
	public static void switchShoutUse(){
		SHOUT_ENABLED = !SHOUT_ENABLED;

	}
	public static void switchThinkUse(){
		THINK_ENABLED = !THINK_ENABLED;

	}
	public static void switchDoUse(){
		DO_ENABLED = !DO_ENABLED;

	}
	public static void switchDestinUse(){
		DESTIN_ENABLED = !DESTIN_ENABLED;

	}
	public static void switchLootUse(){
		LOOT_ENABLED = !LOOT_ENABLED;
	}
	public static void switchRPReloadUse(){
		RP_RELOAD_ENABLED = !RP_RELOAD_ENABLED;
	}
	public static void switchRPRangeUse(){
		RP_RANGE_ENABLED = !RP_RANGE_ENABLED;

	}
	public static void switchShoutRangeUse(){
		SHOUT_RANGE_ENABLED = !SHOUT_RANGE_ENABLED;

	}
	public static void switchThinkRangeUse(){
		THINK_RANGE_ENABLED = !THINK_RANGE_ENABLED;

	}
	public static void switchDoRangeUse(){
		DO_RANGE_ENABLED = !DO_RANGE_ENABLED;

	}
	public static void switchDestinRangeUse(){
		DESTIN_RANGE_ENABLED = !DESTIN_RANGE_ENABLED;

	}
	public static void switchLootRangeUse(){
		LOOT_RANGE_ENABLED = !LOOT_RANGE_ENABLED;
	}
	public static void switchWGFlagUse(){
		WORLD_GUARD_RP_FLAG = !WORLD_GUARD_RP_FLAG;
	}

	static void copyFileUsingStream(File source, File dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			is.close();
			os.close();
		}
	}

}
