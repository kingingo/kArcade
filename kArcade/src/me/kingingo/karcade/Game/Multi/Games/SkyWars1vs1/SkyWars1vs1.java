package me.kingingo.karcade.Game.Multi.Games.SkyWars1vs1;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import me.kingingo.karcade.Game.Multi.MultiGames;
import me.kingingo.karcade.Game.Multi.Addons.MultiGameArenaRestore;
import me.kingingo.karcade.Game.Multi.Events.MultiGameStartEvent;
import me.kingingo.karcade.Game.Multi.Events.MultiGameStateChangeEvent;
import me.kingingo.karcade.Game.Multi.Games.MultiGame;
import me.kingingo.kcore.Enum.GameState;
import me.kingingo.kcore.Enum.Team;
import me.kingingo.kcore.Kit.Kit;
import me.kingingo.kcore.Kit.KitType;
import me.kingingo.kcore.Kit.Perk;
import me.kingingo.kcore.Kit.Perks.PerkArrowFire;
import me.kingingo.kcore.Kit.Perks.PerkArrowInfinity;
import me.kingingo.kcore.Kit.Perks.PerkDoubleJump;
import me.kingingo.kcore.Kit.Perks.PerkEquipment;
import me.kingingo.kcore.Kit.Perks.PerkHeal;
import me.kingingo.kcore.Kit.Perks.PerkHealByHit;
import me.kingingo.kcore.Kit.Perks.PerkHolzfäller;
import me.kingingo.kcore.Kit.Perks.PerkMoreHearth;
import me.kingingo.kcore.Kit.Perks.PerkNoExplosionDamage;
import me.kingingo.kcore.Kit.Perks.PerkNoFalldamage;
import me.kingingo.kcore.Kit.Perks.PerkNoFiredamage;
import me.kingingo.kcore.Kit.Perks.PerkNoHunger;
import me.kingingo.kcore.Kit.Perks.PerkNoKnockback;
import me.kingingo.kcore.Kit.Perks.PerkPoisen;
import me.kingingo.kcore.Kit.Perks.PerkSneakDamage;
import me.kingingo.kcore.Kit.Perks.PerkTNT;
import me.kingingo.kcore.Kit.Perks.PerkWalkEffect;
import me.kingingo.kcore.Kit.Shop.MultiKitShop;
import me.kingingo.kcore.PacketAPI.Packets.kPacketPlayOutWorldBorder;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.Util.InventorySize;
import me.kingingo.kcore.Util.UtilBG;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilLocation;
import me.kingingo.kcore.Util.UtilMap;
import me.kingingo.kcore.Util.UtilMath;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilWorld;

import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.Potion.Tier;
import org.bukkit.potion.PotionType;

public class SkyWars1vs1 extends MultiGame{

	private MultiGameArenaRestore area;
	private kPacketPlayOutWorldBorder packet;
	private HashMap<Chest,ArrayList<String>> template;
	private HashMap<String,Integer> template_type;
	
	public SkyWars1vs1(MultiGames games,String Map,Location location,File file) {
		super(games,Map, location);
		this.packet=UtilWorld.createWorldBorder(getPasteLocation(), 125*2, 25, 10);
		Location ecke1 = getPasteLocation().clone();
		ecke1.setY(255);
		ecke1.add(125, 0, 125);
		Location ecke2 = getPasteLocation().clone();
		ecke2.setY(0);
		ecke2.add(-125, 0, -125);
		this.area=new MultiGameArenaRestore(this, ecke1,ecke2);
		UtilBG.setHub("versus");
		setUpdateTo("versus");
		getWorldData().loadSchematic(this, location, file);
		
		for(Team team : getWorldData().getTeams(this).keySet()){
			for(Location loc : getWorldData().getTeams(this).get(team)){
				loc.getBlock().setType(Material.EMERALD_BLOCK);
				Log(UtilLocation.getLocString(loc));
			}
		}
		
		UtilMap.makeQuadrat(null,getWorldData().getLocs(this, Team.RED).get(0).clone().add(0,10, 0), 2, 5, new ItemStack(Material.STAINED_GLASS,1,(byte)14),null);
		UtilMap.makeQuadrat(null,getWorldData().getLocs(this, Team.BLUE).get(0).clone().add(0,10, 0), 2, 5, new ItemStack(Material.STAINED_GLASS,1,(byte)11),null);

		setBlockBreak(true);
		setBlockPlace(true);
		setDropItem(true);
		setPickItem(true);
		setDropItembydeath(true);
		setFoodlevelchange(true);
		setDamagePvP(false);
		setDamage(false);
		getEntityDamage().add(DamageCause.FALL);
		
		UtilSkyWars1vs1.loadWorld(this, template, template_type);
		
		if(games.getKitshop()==null){
			games.setKitshop(new MultiKitShop(games, "Kit-Shop", InventorySize._9, new Kit[]{
				new Kit("§aStarter-Kit",new String[]{"§8x1§7 Leder Rüstung","§8x1§7 Holzschwert mit Schärfe 1"},new ItemStack(Material.LEATHER_HELMET),kPermission.SKYWARS_KIT_STARTERKIT,KitType.STARTER,0,0,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.WOOD_SWORD), Enchantment.DAMAGE_ALL, 1),new ItemStack(Material.LEATHER_BOOTS),new ItemStack(Material.LEATHER_LEGGINGS),new ItemStack(Material.LEATHER_CHESTPLATE),new ItemStack(Material.LEATHER_HELMET)})
				}),
				new Kit("§ePanzer",new String[]{"§8x1§7 Diamanthelm mit Dornen 1,Unbreaking 1","§8x1§7 Eisenbrustpanzer mit Unbreaking 1","§8x1§7 Eisenhose mit Unbreaking 1","§8x1§7 Eisenschuhe mit Unbreaking 1"},new ItemStack(Material.SLIME_BALL),kPermission.SKYWARS_KIT_PANZER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(UtilItem.EnchantItem(new ItemStack(Material.DIAMOND_CHESTPLATE), Enchantment.DURABILITY, 1), Enchantment.THORNS, 1),
							UtilItem.EnchantItem(new ItemStack(Material.IRON_HELMET), Enchantment.DURABILITY, 1),
							UtilItem.EnchantItem(new ItemStack(Material.IRON_LEGGINGS), Enchantment.DURABILITY, 1),
							UtilItem.EnchantItem(new ItemStack(Material.IRON_BOOTS), Enchantment.DURABILITY, 1)})
				}),
				new Kit("§eGlueckshase",new String[]{"§8x1§7 Eisenspitzhacke mit Glueck 2"},new ItemStack(Material.RABBIT_FOOT),kPermission.SKYWARS_KIT_HASE,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.IRON_PICKAXE), Enchantment.LUCK, 2)})
				}),
				new Kit("§eHulk",new String[]{"§8x1§7 Leder Rüstung mit Schutz 1","§8x1§7 Stärke 2 Trank"},UtilItem.LSetColor(new ItemStack(Material.LEATHER_HELMET), DyeColor.GREEN),kPermission.SKYWARS_KIT_HULK,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.LSetColor(new ItemStack(Material.LEATHER_BOOTS), DyeColor.GREEN),
							UtilItem.LSetColor(new ItemStack(Material.LEATHER_LEGGINGS), DyeColor.GREEN),
							UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), DyeColor.GREEN),
							UtilItem.LSetColor(new ItemStack(Material.LEATHER_HELMET), DyeColor.GREEN),
							new ItemStack(Material.POTION,1,(byte)8233)})
				}),
				new Kit("§eSuperMario",new String[]{"§8x1§7 Roter Lederbrustpanzer","§8x1§7 Blaue Lederhose","§8x1§7 Sprungkraft 1 Trank"},UtilItem.LSetColor(new ItemStack(Material.LEATHER_HELMET), DyeColor.RED),kPermission.SKYWARS_KIT_MARIO,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.LSetColor(new ItemStack(Material.LEATHER_LEGGINGS), DyeColor.RED),
							UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), DyeColor.BLUE),
							new ItemStack(Material.POTION,1,(byte)16459)})
				}),
				new Kit("§eStuntman",new String[]{"§8x1§7 Diamantschuhe mit Federfall IV"},new ItemStack(Material.FEATHER),kPermission.SKYWARS_KIT_STUNTMAN,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.DIAMOND_BOOTS), Enchantment.PROTECTION_FALL, 5)})
				}),
				new Kit("§eSlime",new String[]{"§8x16§7 Slime Bleocke","§8x5§7 Jump Wurftrank"},new ItemStack(Material.SLIME_BALL),kPermission.SKYWARS_KIT_SLIME,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.SLIME_BLOCK,16),new ItemStack(Material.POTION,5,(byte)16427)})
				}),
				new Kit( "§eKoch",new String[]{"§8x1§7 Holzschwert Schärfe 2","§8x8§7 Steaks","§8x1§7 Kuchen","§8x3§7 Goldenäpfel"}, new ItemStack(Material.CAKE),kPermission.SKYWARS_KIT_KOCH,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.RenameItem(UtilItem.EnchantItem(new ItemStack(Material.WOOD_SWORD),Enchantment.DAMAGE_ALL,2), "§7Gabel"),new ItemStack(Material.COOKED_BEEF,8),new ItemStack(Material.CAKE,1),new ItemStack(Material.GOLDEN_APPLE,3)})
				}),
				new Kit( "§eSprengmeister",new String[]{"§8x10 TnT","§8x1 Feuerzeug"}, new ItemStack(Material.TNT),kPermission.SKYWARS_KIT_SPRENGMEISTER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.TNT,10),new ItemStack(Material.FLINT_AND_STEEL)})
				}),
				new Kit( "§eJäger",new String[]{"§8x1§7 Bogen","§8x10 §7Pfeile"}, new ItemStack(Material.ARROW),kPermission.SKYWARS_KIT_JÄGER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.BOW),new ItemStack(Material.ARROW,10)})
				}),
				new Kit( "§eEnchanter",new String[]{"§8x1§7 Enchantment Table","§8x64 §7Enchantment Bottles"}, new ItemStack(Material.ENCHANTMENT_TABLE),kPermission.SKYWARS_KIT_ENCHANTER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.ENCHANTMENT_TABLE),new ItemStack(Material.EXP_BOTTLE,64)})
				}),
				new Kit( "§eHeiler",new String[]{"§8x3§7 Tränke der Heilung","§8x3§7 Regenerations Tränke"}, new ItemStack(Material.POTION,1,(short)16389),kPermission.SKYWARS_KIT_HEILER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.POTION,3,(short)16389),new ItemStack(Material.POTION,3,(short)16385)})
				}),
				new Kit( "§eSpäher",new String[]{"§8x1§7 Steinschwert","§8x3 §7Schnelligkeits Tränke"}, new ItemStack(Material.STONE_SWORD),kPermission.SKYWARS_KIT_SPÄHER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.STONE_SWORD),new ItemStack(Material.POTION,3,(short)16386)})
				}),
				new Kit( "§eKampfmeister",new String[]{"§8x1§7 Anvil","§864x§7 Enchantment Bottles","§8x1§7 Diamant Helm","§8x1§7 Enchantment Buch (Protection III)"}, new ItemStack(Material.ANVIL),kPermission.SKYWARS_KIT_KAMPFMEISTER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.getEnchantmentBook(Enchantment.PROTECTION_ENVIRONMENTAL, 3),new ItemStack(Material.DIAMOND_HELMET),new ItemStack(Material.EXP_BOTTLE,64),new ItemStack(Material.ANVIL)})
				}),
				new Kit( "§eRitter",new String[]{"§8x1§7 Eisenschwert (Schärfe II)"}, new ItemStack(Material.IRON_SWORD),kPermission.SHEEPWARS_KIT_ARROWMAN,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.IRON_SWORD), Enchantment.DAMAGE_ALL, 2)})
				}),
				new Kit( "§eFeuermeister",new String[]{"§8x1§7Feuerzeug","§82x§7 Lava-Eimer","§82x§7 Feuer Tränke"}, new ItemStack(Material.LAVA_BUCKET),kPermission.SKYWARS_KIT_FEUERMEISTER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.POTION,2,(short)16387),new ItemStack(Material.FLINT_AND_STEEL,1),new ItemStack(Material.LAVA_BUCKET,2)})
				}),
				new Kit( "§eDroide",new String[]{"§8x1§7 Regenerations Trank","§82x§7 Tränke der Heilung","§82x§7 Vergiftungs Tränke","§82x§7 Schadens Tränke"}, new ItemStack(Material.POTION),kPermission.SKYWARS_KIT_DROIDE,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.POTION,1,(short)16385),new ItemStack(Material.POTION,2,(short)16388),new ItemStack(Material.POTION,2,(short)16389),new ItemStack(Material.POTION,2,(short)16396)})
				}),
				new Kit( "§eStoßer",new String[]{"§8x1§7 Holzschwert mit Rückstoß 2"}, new ItemStack(Material.WOOD_SWORD),kPermission.SKYWARS_KIT_STOßER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.WOOD_SWORD), Enchantment.KNOCKBACK,1)})
				}),
				new Kit( "§eHase",new String[]{"§8x2§7 Schnelligkeits Treanke","§8x2§7 Sprungkraft Treanke","§8x8§7 Kartotten"}, new ItemStack(Material.CARROT),kPermission.SKYWARS_KIT_HASE,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.POTION,2,(byte)0),new ItemStack(Material.POTION,2,(byte)0),new ItemStack(Material.CARROT,8)})
				}),
				new Kit( "§eRusher",new String[]{"§8x25§7 Granit Bloecke","§8x1§7 Steinschwert Schaerfe 1","§8x1§7 Goldbrustpanzer"}, new ItemStack(Material.GOLD_CHESTPLATE),kPermission.SKYWARS_KIT_RUSHER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.COBBLESTONE,25),new ItemStack(Material.GOLD_CHESTPLATE),UtilItem.EnchantItem(new ItemStack(Material.STONE_SWORD), Enchantment.DAMAGE_ALL, 1)})
				}),
				new Kit( "§ePolizist",new String[]{"§8x1§7 Lederbrustpanzer","§8x1§7 Lederhose","§8x1§7 Stock mit Schaerfe 2"}, UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), DyeColor.BLUE),kPermission.SKYWARS_KIT_POLIZIST,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), DyeColor.BLUE),UtilItem.LSetColor(new ItemStack(Material.LEATHER_LEGGINGS), DyeColor.BLUE),UtilItem.RenameItem(UtilItem.EnchantItem(new ItemStack(Material.STICK), Enchantment.DAMAGE_ALL,2), "§7Knueppel")})
				}),
				new Kit( "§eFarmer",new String[]{"§8x1§7 Eisenspitzhacke mit Schaerfe 2 u. Efficiens 2","§8x16§7 Eier"}, new ItemStack(Material.IRON_PICKAXE),kPermission.SKYWARS_KIT_FARMER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.EGG,16),UtilItem.EnchantItem(UtilItem.EnchantItem(new ItemStack(Material.IRON_PICKAXE), Enchantment.DIG_SPEED, 2), Enchantment.DAMAGE_ALL,2)})
				}),
				new Kit( "§eFischer",new String[]{"§8x1§7 Angel mit unbreaking 2","§8x1§7 Kettenrüstung mit Schutz 2"}, new ItemStack(Material.CHAINMAIL_CHESTPLATE),kPermission.SKYWARS_KIT_FISCHER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.FISHING_ROD), Enchantment.DURABILITY,2),UtilItem.EnchantItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE), Enchantment.PROTECTION_ENVIRONMENTAL,2)})
				}),
				new Kit( "§eVip",new String[]{"§8x1§7 Steinschwert","§8x1§7 Bogen","§8x8§7 Pfeile","§8x2§7 Heal Potions"}, new ItemStack(Material.GOLD_NUGGET),kPermission.SKYWARS_KIT_VIP,KitType.VIP,0,0,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.STONE_SWORD),new ItemStack(Material.BOW),new ItemStack(Material.ARROW,8),new Potion(PotionType.INSTANT_HEAL, Tier.TWO, true).toItemStack(2)})
				}),
				new Kit( "§eUltra",new String[]{"§8x1§7 Steinschwert mit Schaerfe 1","§8x1§7 Eisenpickaxe","§8x16§7 Steine","§8x4§7 Heal Potions"}, new ItemStack(Material.GOLD_INGOT),kPermission.SKYWARS_KIT_ULTRA,KitType.ULTRA,0,0,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.STONE_SWORD), Enchantment.DAMAGE_ALL, 1),new ItemStack(Material.IRON_PICKAXE),new ItemStack(Material.STONE,16),new Potion(PotionType.INSTANT_HEAL, Tier.TWO, true).toItemStack(4)})
				}),
				new Kit( "§eLegend",new String[]{"§8x1§7 Eisenpanzer","§8x1§7 Eisenschuhe","§8x1§7 Steinschwert","§8x8§7 Steaks","§8x4§7 Heal Potions"}, new ItemStack(Material.IRON_INGOT),kPermission.SKYWARS_KIT_LEGEND,KitType.LEGEND,0,0,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.STONE_SWORD),new ItemStack(Material.IRON_CHESTPLATE),new ItemStack(Material.IRON_BOOTS),new ItemStack(Material.COOKED_BEEF,8),new Potion(PotionType.INSTANT_HEAL, Tier.TWO, true).toItemStack(4)})
				}),
				new Kit( "§eMVP",new String[]{"§8x1§7 Steinschwert mit Rueckstoß","§8x1§7 Diamanthelm","§8x1§7 Diamnantschuhe","§8x4§7 Heal Potion"}, new ItemStack(Material.EMERALD),kPermission.SKYWARS_KIT_MVP,KitType.MVP,0,0,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.STONE_SWORD), Enchantment.KNOCKBACK, 1),new ItemStack(Material.DIAMOND_HELMET),new ItemStack(Material.DIAMOND_BOOTS),new Potion(PotionType.INSTANT_HEAL, Tier.TWO, true).toItemStack(4)})
				}),
				new Kit( "§eMVP+",new String[]{"§8x1§7 Diamanthelm","§8x1§7 Eisenbrustpanzer","§8x16§7 Steine","§8x8§7 Eisenschwert","§8x4§7 Heal Potion"}, new ItemStack(Material.DIAMOND),kPermission.SKYWARS_KIT_MVPPLUS,KitType.MVP_PLUS,0,0,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.IRON_SWORD),new ItemStack(Material.DIAMOND_HELMET),new ItemStack(Material.STONE,16),new Potion(PotionType.INSTANT_HEAL, Tier.TWO, true).toItemStack(4)})
				}),
				new Kit( "§aSuperman",new String[]{"Der Superman ist das Beste kit in SkyWars!"}, new ItemStack(Material.DIAMOND_SWORD),kPermission.SKYWARS_KIT_SUPERMAN,KitType.ADMIN,2000,new Perk[]{
					new PerkNoHunger(),
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.IRON_SWORD,1), Enchantment.DAMAGE_ALL,4),new ItemStack(Material.BOW), new ItemStack(Material.LEATHER_HELMET,1), UtilItem.EnchantItem(new ItemStack(Material.IRON_CHESTPLATE,1), Enchantment.PROTECTION_ENVIRONMENTAL,3), UtilItem.EnchantItem(new ItemStack(Material.LEATHER_LEGGINGS,1), Enchantment.PROTECTION_ENVIRONMENTAL,4), new ItemStack(Material.LEATHER_BOOTS)}),
					new PerkSneakDamage(1),
					new PerkPoisen(10,50),
					new PerkHolzfäller(),
					new PerkNoFiredamage(),
					new PerkNoFalldamage(),
					new PerkArrowFire(80),
					new PerkDoubleJump(),
					new PerkNoKnockback(games.getManager().getInstance()),
					new PerkNoExplosionDamage(),
					new PerkTNT(),
					new PerkHealByHit(60, 6),
					new PerkHeal(6),
					new PerkMoreHearth(6, 60),
					new PerkArrowInfinity(),
					new PerkWalkEffect(Effect.CRIT,10)
				})
			}));
		}
	}

	@EventHandler
	public void lobby(MultiGameStateChangeEvent ev){
		if(ev.getGame()!=this)return;
		if(ev.getTo()==GameState.LobbyPhase){
			if(area!=null)area.restore();
			UtilMap.makeQuadrat(null,getWorldData().getLocs(this, Team.RED).get(0).clone().add(0,10, 0), 2, 5, new ItemStack(Material.STAINED_GLASS,1,(byte)14),null);
			UtilMap.makeQuadrat(null,getWorldData().getLocs(this, Team.BLUE).get(0).clone().add(0,10, 0), 2, 5, new ItemStack(Material.STAINED_GLASS,1,(byte)11),null);
			
			setDamagePvP(false);
			setDamage(false);
		}
	}
	
	@EventHandler
	public void start(MultiGameStartEvent ev){
		if(ev.getGame() == this){
			UtilMap.makeQuadrat(null,getWorldData().getLocs(this, Team.RED).get(0).clone().add(0,10, 0), 2, 5, new ItemStack(Material.AIR,1),null);
			UtilMap.makeQuadrat(null,getWorldData().getLocs(this, Team.BLUE).get(0).clone().add(0,10, 0), 2, 5, new ItemStack(Material.AIR,1),null);
			UtilSkyWars1vs1.loadWorld(this, template, template_type);
			
			for(Player player : getGameList().getPlayers().keySet()){
				UtilPlayer.sendPacket(player, this.packet);
			}
		}
	}
}
