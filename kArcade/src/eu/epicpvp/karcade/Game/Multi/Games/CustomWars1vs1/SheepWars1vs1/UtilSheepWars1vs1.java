package eu.epicpvp.karcade.Game.Multi.Games.CustomWars1vs1.SheepWars1vs1;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import eu.epicpvp.karcade.Game.Game;
import eu.epicpvp.kcore.Kit.Kit;
import eu.epicpvp.kcore.Kit.KitType;
import eu.epicpvp.kcore.Kit.Perk;
import eu.epicpvp.kcore.Kit.Perks.PerkArrowFire;
import eu.epicpvp.kcore.Kit.Perks.PerkArrowInfinity;
import eu.epicpvp.kcore.Kit.Perks.PerkDeathDropOnly;
import eu.epicpvp.kcore.Kit.Perks.PerkEquipment;
import eu.epicpvp.kcore.Kit.Perks.PerkHeal;
import eu.epicpvp.kcore.Kit.Perks.PerkHealByHit;
import eu.epicpvp.kcore.Kit.Perks.PerkHolzfaeller;
import eu.epicpvp.kcore.Kit.Perks.PerkMoreHearth;
import eu.epicpvp.kcore.Kit.Perks.PerkNoDropsByDeath;
import eu.epicpvp.kcore.Kit.Perks.PerkNoExplosionDamage;
import eu.epicpvp.kcore.Kit.Perks.PerkNoFalldamage;
import eu.epicpvp.kcore.Kit.Perks.PerkNoFiredamage;
import eu.epicpvp.kcore.Kit.Perks.PerkNoHunger;
import eu.epicpvp.kcore.Kit.Perks.PerkNoKnockback;
import eu.epicpvp.kcore.Kit.Perks.PerkPoisen;
import eu.epicpvp.kcore.Kit.Perks.PerkPotionByDeath;
import eu.epicpvp.kcore.Kit.Perks.PerkRespawnBuff;
import eu.epicpvp.kcore.Kit.Perks.PerkSneakDamage;
import eu.epicpvp.kcore.Kit.Perks.PerkSpawnByDeath;
import eu.epicpvp.kcore.Kit.Perks.PerkStopPerk;
import eu.epicpvp.kcore.Kit.Perks.PerkTNT;
import eu.epicpvp.kcore.Kit.Perks.PerkWalkEffect;
import eu.epicpvp.kcore.Permission.PermissionType;

public class UtilSheepWars1vs1 {

	public static Kit[] getKits(Game game){
		return new Kit[]{
				new Kit( "§aStarter",new String[]{"Der Starter bekommt kein Hunger."}, new ItemStack(Material.WOOD_SWORD),PermissionType.SHEEPWARS_KIT_STARTER,KitType.STARTER,2000,new Perk[]{
						new PerkNoHunger()
					}),
					new Kit( "§eArrowMan",new String[]{"Der ArrowMan besitzt die 30% Chance","das seine Pfeile brennen."}, new ItemStack(Material.ARROW),PermissionType.SHEEPWARS_KIT_ARROWMAN,KitType.KAUFEN,2000,500,new Perk[]{
						new PerkArrowFire(30)
					}),
					new Kit( "§eItemStealer",new String[]{"Der ItemStealer hat nach seinem"," Tod 10 sekunden um seine","Sachen aufzuheben solange","kann er sie nur aufheben."}, new ItemStack(Material.SHEARS),PermissionType.SHEEPWARS_KIT_ITEMSTEALER,KitType.KAUFEN,2000,500,new Perk[]{
						new PerkDeathDropOnly(10)
					}),
					new Kit( "§eHealer",new String[]{"Der Healer heilt schneller."}, new ItemStack(Material.APPLE),PermissionType.SHEEPWARS_KIT_HEALER,KitType.KAUFEN,2000,500,new Perk[]{
						new PerkHeal(5)
					}),
					new Kit( "§eDropper",new String[]{"Der Dropper l§sst seine Sachen","beim Tod nicht fallen."}, new ItemStack(Material.DROPPER),PermissionType.SHEEPWARS_KIT_DROPPER,KitType.KAUFEN,2000,500,new Perk[]{
						new PerkNoDropsByDeath()
					}),
					new Kit( "§eAnker",new String[]{"Der Anker bekommt kein R§cksto§."}, new ItemStack(Material.ANVIL),PermissionType.SHEEPWARS_KIT_ANKER,KitType.KAUFEN,2000,500,new Perk[]{
						new PerkNoKnockback(game.getManager().getInstance())
					}),
					new Kit( "§ePerker",new String[]{"Der Perker stoppt beim angreiffen","vom Gegner die Perk's"}, new ItemStack(Material.TORCH),PermissionType.SHEEPWARS_KIT_PERKER,KitType.KAUFEN,2000,500,new Perk[]{
						new PerkStopPerk(10)
					}),
					new Kit( "§eTNTer",new String[]{"Der TNT hat die 10% Chance","das an seiner Todes stelle","ein TNT spawnt."}, new ItemStack(Material.TNT),PermissionType.SHEEPWARS_KIT_TNTER,KitType.KAUFEN,2000,500,new Perk[]{
						new PerkSpawnByDeath(EntityType.PRIMED_TNT,10)
					}),
					new Kit( "§eBuffer",new String[]{"Der Buffer bekommt wenn er Respawn","Feuerresistance und Schadenresistance."}, new ItemStack(Material.POTION),PermissionType.SHEEPWARS_KIT_BUFFER,KitType.KAUFEN,2000,500,new Perk[]{
						new PerkRespawnBuff(new PotionEffect[]{new PotionEffect(PotionEffectType.FIRE_RESISTANCE,20*20,2),new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,20*20,2)})
					}),
					new Kit( "§eKnight",new String[]{"Der Knight bekommt beim Sneaken","h§chstens 1 Herz schaden","wenn er angegriffen wird."}, new ItemStack(Material.DIAMOND_CHESTPLATE),PermissionType.SHEEPWARS_KIT_KNIGHT,KitType.KAUFEN,2000,500,new Perk[]{
						new PerkSneakDamage(3.0)
					}),
					new Kit( "§eTheDeath",new String[]{"Der TheDeath drop beim Tod","ein Blindheits Trank."}, new ItemStack(Material.IRON_SWORD),PermissionType.SHEEPWARS_KIT_THEDEATH,KitType.KAUFEN,2000,500,new Perk[]{
						new PerkPotionByDeath(new PotionEffect(PotionEffectType.BLINDNESS,20*5,1))
					}),
					new Kit( "§eSpringer",new String[]{"Der Springer bekommt kein Fallschaden."}, new ItemStack(Material.FEATHER),PermissionType.SHEEPWARS_KIT_SPRINGER,KitType.KAUFEN,2000,500,new Perk[]{
						new PerkNoFalldamage()
					}),
					new Kit("§6PigZombie",new String[]{"Der PigZombie bekommt beim Respawnen","Speed und Regeneration"}, new ItemStack(Material.RAW_BEEF), PermissionType.SHEEPWARS_KIT_VIP,KitType.VIP,0,new Perk[]{
						new PerkRespawnBuff(new PotionEffect[]{new PotionEffect(PotionEffectType.SPEED,15*20,1), new PotionEffect(PotionEffectType.REGENERATION,10*20,1)})
					}),
					new Kit("§6Creeper",new String[]{"Der Creeper hat die 10% Chance","das an seiner Todes stelle","ein TNT spawnt."}, new ItemStack(Material.SKULL_ITEM,1,(byte)4), PermissionType.SHEEPWARS_KIT_ULTRA,KitType.ULTRA,0,new Perk[]{
						new PerkSpawnByDeath(EntityType.PRIMED_TNT,30)
					}),
					new Kit("§6Zombie",new String[]{"Der Zombie vergiftet seinen Gegner","bei einer Chance von 30%","f§r 3 sekunden."}, new ItemStack(Material.SKULL_ITEM,1,(byte)2), PermissionType.SHEEPWARS_KIT_LEGEND, KitType.LEGEND,0,new Perk[]{
						new PerkPoisen(3,30)
					}),
					new Kit( "§cOldRush",new String[]{"Der OldRush kriegt kein Fallschaden","15% Chance das seine Pfeile brennen","10% Chance das beim Tod ein TNT Spawn."}, new ItemStack(Material.BED), PermissionType.SHEEPWARS_KIT_OLD_RUSH, KitType.SPEZIAL_KIT, 0,new Perk[]{
						new PerkNoFiredamage(),
						new PerkArrowFire(15),
						new PerkNoFalldamage(),
						new PerkSpawnByDeath(EntityType.PRIMED_TNT,10)
					}),
					new Kit( "§aSuperman",new String[]{"Der Superman ist das Beste kit in SheepWars!"}, new ItemStack(Material.DIAMOND_SWORD),PermissionType.SHEEPWARS_KIT_SUPERMAN,KitType.ADMIN,2000,new Perk[]{
						new PerkNoHunger(),
						new PerkEquipment(new ItemStack[]{new ItemStack(Material.IRON_CHESTPLATE,1)}),
						new PerkSneakDamage(1),
						new PerkPoisen(10,50),
						new PerkHolzfaeller(),
						new PerkNoFiredamage(),
						new PerkNoFalldamage(),
						new PerkArrowFire(80),
						new PerkNoExplosionDamage(),
						new PerkTNT(),
						new PerkHealByHit(60, 6),
						new PerkHeal(6),
						new PerkMoreHearth(6, 60),
						new PerkArrowInfinity(),
						new PerkWalkEffect(Effect.HEART,10),
						new PerkSpawnByDeath(EntityType.PRIMED_TNT,80),
						new PerkNoKnockback(game.getManager().getInstance())
					})
				};
	}
	
}
