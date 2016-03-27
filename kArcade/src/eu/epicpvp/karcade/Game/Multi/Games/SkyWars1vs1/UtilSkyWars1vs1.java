package eu.epicpvp.karcade.Game.Multi.Games.SkyWars1vs1;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.potion.Potion.Tier;

import eu.epicpvp.karcade.Game.Game;
import eu.epicpvp.karcade.Game.Multi.Games.MultiGame;
import eu.epicpvp.karcade.Game.Single.Games.SkyWars.Item.CreeperSpawner;
import eu.epicpvp.kcore.Enum.Team;
import eu.epicpvp.kcore.Kit.Kit;
import eu.epicpvp.kcore.Kit.KitType;
import eu.epicpvp.kcore.Kit.Perk;
import eu.epicpvp.kcore.Kit.Perks.PerkArrowFire;
import eu.epicpvp.kcore.Kit.Perks.PerkArrowInfinity;
import eu.epicpvp.kcore.Kit.Perks.PerkDoubleJump;
import eu.epicpvp.kcore.Kit.Perks.PerkEquipment;
import eu.epicpvp.kcore.Kit.Perks.PerkHeal;
import eu.epicpvp.kcore.Kit.Perks.PerkHealByHit;
import eu.epicpvp.kcore.Kit.Perks.PerkHolzfaeller;
import eu.epicpvp.kcore.Kit.Perks.PerkLessDamage;
import eu.epicpvp.kcore.Kit.Perks.PerkLessDamageCause;
import eu.epicpvp.kcore.Kit.Perks.PerkMoreHearth;
import eu.epicpvp.kcore.Kit.Perks.PerkNoExplosionDamage;
import eu.epicpvp.kcore.Kit.Perks.PerkNoFalldamage;
import eu.epicpvp.kcore.Kit.Perks.PerkNoFiredamage;
import eu.epicpvp.kcore.Kit.Perks.PerkNoHunger;
import eu.epicpvp.kcore.Kit.Perks.PerkNoKnockback;
import eu.epicpvp.kcore.Kit.Perks.PerkPoisen;
import eu.epicpvp.kcore.Kit.Perks.PerkSneakDamage;
import eu.epicpvp.kcore.Kit.Perks.PerkTNT;
import eu.epicpvp.kcore.Kit.Perks.PerkWalkEffect;
import eu.epicpvp.kcore.Kit.Shop.KitShop;
import eu.epicpvp.kcore.Kit.Shop.SingleKitShop;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilMath;

public class UtilSkyWars1vs1 {
	
	public static ItemStack Sonstiges(){
		try{
			switch(UtilMath.r(39)){
			case 0: return new ItemStack(Material.ENDER_PEARL,UtilMath.RandomInt(4, 2));
			case 1: return new ItemStack(Material.GOLDEN_APPLE,1);
			case 2: return new ItemStack(Material.ARROW,UtilMath.RandomInt(64,32));
			case 3: return new ItemStack(Material.ARROW,UtilMath.RandomInt(64,32));
			case 4: return new ItemStack(Material.ARROW,UtilMath.RandomInt(64,32));
			case 5: return new ItemStack(Material.ARROW,UtilMath.RandomInt(64,32));
			case 6: return new ItemStack(Material.FISHING_ROD);
			case 7: return new ItemStack(Material.FISHING_ROD);
			case 8: return new ItemStack(Material.FISHING_ROD);
			case 9: return new ItemStack(Material.TNT);
			case 10: return new ItemStack(Material.TNT);
			case 11: return new ItemStack(Material.TNT);
			case 12:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16421);
			case 13:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16417);
			case 14:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16389);
			case 15:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16385);
			case 16:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16387);
			case 17:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16421);
			case 18:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16417);
			case 19:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16389);
			case 20:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16385);
			case 21:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16387);
			case 22:return new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(32,10));
			case 23:return new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(32,10));
			case 24:return new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(32,10));
			case 25:return new ItemStack(Material.COOKED_BEEF,UtilMath.RandomInt(12,8));
			case 26:return new ItemStack(Material.COOKED_CHICKEN,UtilMath.RandomInt(12,8));
			case 27:return new ItemStack(Material.COOKED_FISH,UtilMath.RandomInt(12,8));
			case 28:return new ItemStack(Material.COOKED_RABBIT,UtilMath.RandomInt(12,8));
			case 29:return new ItemStack(Material.BREAD,UtilMath.RandomInt(12,8));
			case 30:return new ItemStack(Material.COOKED_BEEF,UtilMath.RandomInt(12,8));
			case 31:return new ItemStack(Material.COOKED_CHICKEN,UtilMath.RandomInt(12,8));
			case 32:return new ItemStack(Material.COOKED_FISH,UtilMath.RandomInt(12,8));
			case 33:return new ItemStack(Material.COOKED_RABBIT,UtilMath.RandomInt(12,8));
			case 34:return new ItemStack(Material.BREAD,UtilMath.RandomInt(12,8));
			case 35: return new ItemStack(Material.ENDER_PEARL,UtilMath.RandomInt(4, 2));
			case 36: return new ItemStack(Material.ENDER_PEARL,UtilMath.RandomInt(4, 2));
			default: return new ItemStack(Material.STICK);
			}
		}catch(NullPointerException e){
			System.err.println("Error: ");
			e.printStackTrace();
			return new ItemStack(Material.BREAD,UtilMath.RandomInt(12,8));
		}
	}
	
	public static Kit[] getKits(Game game, CreeperSpawner creeper){
		return new Kit[]{
				
				new Kit("§aStarter-Kit",new String[]{"§8x1§7 Leder R§stung","§8x1§7 Holzschwert mit Sch§rfe 1"},new ItemStack(Material.LEATHER_HELMET),PermissionType.SKYWARS_KIT_STARTERKIT,KitType.STARTER,0,0,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.WOOD_SWORD), Enchantment.DAMAGE_ALL, 1),new ItemStack(Material.LEATHER_BOOTS),new ItemStack(Material.LEATHER_LEGGINGS),new ItemStack(Material.LEATHER_CHESTPLATE),new ItemStack(Material.LEATHER_HELMET)})
				}),
				new Kit( "§eBuchhalter",new String[]{"§8x1§7 Leder Ruestung","§8x1§7 Feder mit Sch§rfe II","§8x1§7 Buch und Feder","§8x64§7 Buecherregale"}, new ItemStack(Material.BOOK_AND_QUILL),PermissionType.SKYWARS_KIT_BUCHHALTER,KitType.KAUFEN,2000,500,new Perk[]{
						new PerkEquipment(new ItemStack[]{new ItemStack(Material.BOOK_AND_QUILL),new ItemStack(Material.BOOKSHELF,64),UtilItem.EnchantItem(new ItemStack(Material.FEATHER), Enchantment.DAMAGE_ALL, 2),UtilItem.LSetColor(new ItemStack(Material.LEATHER_HELMET), DyeColor.GRAY),UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), DyeColor.GRAY),UtilItem.LSetColor(new ItemStack(Material.LEATHER_LEGGINGS), DyeColor.GRAY),UtilItem.LSetColor(new ItemStack(Material.LEATHER_BOOTS), DyeColor.GRAY)}),
					}),
				new Kit( "§eVodo",new String[]{"§8x1§7 Holzschwert mit Sch§rfe II","§8x1§7 Lederbrustpanzer mit Sch§rfe III","§8x2§7 Wurftr§nke der Langsamkeit"}, new ItemStack(Material.POTION),PermissionType.SKYWARS_KIT_WUDU,KitType.KAUFEN,2000,500,new Perk[]{
						new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.WOOD_SWORD),Enchantment.DAMAGE_ALL,2),UtilItem.EnchantItem(new ItemStack(Material.LEATHER_CHESTPLATE),Enchantment.PROTECTION_ENVIRONMENTAL,3),new ItemStack(Material.POTION,2,(byte)16394)}),
					}),
				new Kit( "§eLohe",new String[]{"§8x1§7 Leder Ruestung","§8x1§7 Lohenrute mit Verbrennung I","§8x10§7 Feuerkugeln"}, new ItemStack(Material.BLAZE_ROD),PermissionType.SKYWARS_KIT_LOHE,KitType.KAUFEN,2000,500,new Perk[]{
						new PerkEquipment(new ItemStack[]{new ItemStack(Material.FIREBALL,10),UtilItem.EnchantItem(new ItemStack(Material.BLAZE_ROD), Enchantment.FIRE_ASPECT, 1),UtilItem.LSetColor(new ItemStack(Material.LEATHER_HELMET), DyeColor.YELLOW),UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), DyeColor.YELLOW),UtilItem.LSetColor(new ItemStack(Material.LEATHER_LEGGINGS), DyeColor.YELLOW),UtilItem.LSetColor(new ItemStack(Material.LEATHER_BOOTS), DyeColor.YELLOW)}),
					}),
				new Kit( "§eSensenmann",new String[]{"§8x1§7 Goldene Sense mit Sch§rfe II","§8x64§7 Kohlebl§cke","§8x1§7 Lederschuhe mit Protection I","§8x1§7 Lederhose mit Protection I","§8x1§7 Lederbrustpanzer mit Dron I","§8x1§7 Lederhelm mit Protection I"}, new ItemStack(Material.COAL_BLOCK),PermissionType.SKYWARS_KIT_SENSENMAN,KitType.KAUFEN,2000,500,new Perk[]{
						new PerkEquipment(new ItemStack[]{new ItemStack(Material.COAL_BLOCK,64),UtilItem.EnchantItem(new ItemStack(Material.GOLD_HOE), Enchantment.DAMAGE_ALL, 2),UtilItem.EnchantItem(new ItemStack(Material.LEATHER_BOOTS), Enchantment.PROTECTION_ENVIRONMENTAL, 1),UtilItem.EnchantItem(new ItemStack(Material.LEATHER_CHESTPLATE), Enchantment.THORNS, 1),UtilItem.EnchantItem(new ItemStack(Material.LEATHER_LEGGINGS), Enchantment.PROTECTION_ENVIRONMENTAL, 1),UtilItem.EnchantItem(new ItemStack(Material.LEATHER_HELMET), Enchantment.PROTECTION_ENVIRONMENTAL, 1)}),
					}),
				new Kit( "§eMLG",new String[]{"§8x64§7 Holz","§8x12§7 TNT","§8x1§7 Redstone Fackel","§8x2§7 Wasser Eimer","§8x1§7 Boot","§8x8§7 Spinnenweben"}, new ItemStack(Material.WATER_BUCKET),PermissionType.SKYWARS_KIT_MLG,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.WOOD,64),new ItemStack(Material.TNT,12),new ItemStack(Material.REDSTONE_TORCH_ON,1),new ItemStack(Material.WATER_BUCKET,2),new ItemStack(Material.BOAT,1),new ItemStack(Material.WEB,8)}),
					new PerkLessDamage(5,EntityType.PRIMED_TNT),
					new PerkLessDamageCause(40, DamageCause.FALL)
				}),
				new Kit( "§eSpinne",new String[]{"§8x12§7 Spinnweben","§8x1§7 Sprungkraft II Trank","§8x1§7 Angel","§8x2§7 Wasser Eimer"}, new ItemStack(Material.WEB),PermissionType.SKYWARS_KIT_SPINNE,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.FISHING_ROD),new ItemStack(Material.POTION,1,(byte)8235),new ItemStack(Material.WEB,12)})
				}),
				new Kit( "§eDoktor",new String[]{"§8x1§7 Wei§e Leder Ruestung","§8x2§7 Heil Tr§nke II","§8x2§7 Schere","§8x8§7 Spinnenweben"},UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), DyeColor.WHITE),PermissionType.SKYWARS_KIT_DOKTOR,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.LSetColor(new ItemStack(Material.LEATHER_HELMET), DyeColor.WHITE),UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), DyeColor.WHITE),UtilItem.LSetColor(new ItemStack(Material.LEATHER_LEGGINGS), DyeColor.WHITE),UtilItem.LSetColor(new ItemStack(Material.LEATHER_BOOTS), DyeColor.WHITE),new ItemStack(Material.SHEARS),new ItemStack(Material.POTION,1,(byte)8229)})
				}),
				new Kit( "§eDagobert Duck",new String[]{"§8x1§7 Goldschwert","§8x2§7 Gold§pfel","§8x1§7 Goldbrustpanzer","§8x8§7 Gold"}, new ItemStack(Material.GOLD_CHESTPLATE),PermissionType.SKYWARS_KIT_DAGOBERT_DUCK,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.GOLD_SWORD),new ItemStack(Material.GOLDEN_APPLE,2),new ItemStack(Material.GOLD_CHESTPLATE),new ItemStack(Material.GOLD_INGOT,8)})
				}),
				new Kit( "§eForster",new String[]{"§8x1§7 Steinaxt Sch§rfe 1","§8x1§7 Schere","§8x16§7 Laub","§8x5§7 §pfel"}, new ItemStack(Material.LEAVES),PermissionType.SKYWARS_KIT_FORSTER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.STONE_AXE), Enchantment.DAMAGE_ALL, 1),new ItemStack(Material.APPLE,2),new ItemStack(Material.LEAVES,16),new ItemStack(Material.SHEARS,1)})
				}),
				new Kit("§ePanzer",new String[]{"§8x1§7 Diamanthelm mit Dornen 1,Unbreaking 1","§8x1§7 Eisenbrustpanzer mit Unbreaking 1","§8x1§7 Eisenhose mit Unbreaking 1","§8x1§7 Eisenschuhe mit Unbreaking 1"},new ItemStack(Material.SLIME_BALL),PermissionType.SKYWARS_KIT_PANZER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(UtilItem.EnchantItem(new ItemStack(Material.IRON_CHESTPLATE), Enchantment.DURABILITY, 1), Enchantment.THORNS, 1),
							UtilItem.EnchantItem(new ItemStack(Material.DIAMOND_HELMET), Enchantment.DURABILITY, 1),
							UtilItem.EnchantItem(new ItemStack(Material.IRON_LEGGINGS), Enchantment.DURABILITY, 1),
							UtilItem.EnchantItem(new ItemStack(Material.IRON_BOOTS), Enchantment.DURABILITY, 1)})
				}),
				new Kit("§eGlueckshase",new String[]{"§8x1§7 Eisenspitzhacke mit Glueck 2"},new ItemStack(Material.RABBIT_FOOT),PermissionType.SKYWARS_KIT_HASE,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.IRON_PICKAXE), Enchantment.LOOT_BONUS_BLOCKS, 2)})
				}),
				new Kit("§eHulk",new String[]{"§8x1§7 Leder R§stung mit Schutz 1","§8x1§7 St§rke 2 Trank"},UtilItem.LSetColor(new ItemStack(Material.LEATHER_HELMET), DyeColor.GREEN),PermissionType.SKYWARS_KIT_HULK,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(UtilItem.LSetColor(new ItemStack(Material.LEATHER_BOOTS), DyeColor.GREEN), Enchantment.PROTECTION_ENVIRONMENTAL, 1),
							UtilItem.EnchantItem(UtilItem.LSetColor(new ItemStack(Material.LEATHER_LEGGINGS), DyeColor.GREEN), Enchantment.PROTECTION_ENVIRONMENTAL, 1),
							UtilItem.EnchantItem(UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), DyeColor.GREEN), Enchantment.PROTECTION_ENVIRONMENTAL, 1),
							UtilItem.EnchantItem(UtilItem.LSetColor(new ItemStack(Material.LEATHER_HELMET), DyeColor.GREEN), Enchantment.PROTECTION_ENVIRONMENTAL, 1),
							new ItemStack(Material.POTION,1,(byte)16393)})
				}),
				new Kit("§eSuperMario",new String[]{"§8x1§7 Roter Lederbrustpanzer","§8x1§7 Blaue Lederhose","§8x1§7 Sprungkraft 1 Trank"},UtilItem.LSetColor(new ItemStack(Material.LEATHER_HELMET), DyeColor.RED),PermissionType.SKYWARS_KIT_MARIO,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.LSetColor(new ItemStack(Material.LEATHER_LEGGINGS), DyeColor.RED),
							UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), DyeColor.BLUE),
							new ItemStack(Material.POTION,1,(byte)16459)})
				}),
				new Kit("§eStuntman",new String[]{"§8x1§7 Diamantschuhe mit Federfall IV"},new ItemStack(Material.FEATHER),PermissionType.SKYWARS_KIT_STUNTMAN,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.DIAMOND_BOOTS), Enchantment.PROTECTION_FALL, 5)})
				}),
				new Kit("§eSlime",new String[]{"§8x16§7 Slime Bleocke","§8x5§7 Jump Wurftrank"},new ItemStack(Material.SLIME_BALL),PermissionType.SKYWARS_KIT_SLIME,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.SLIME_BLOCK,16),new ItemStack(Material.POTION,5,(byte)16427)})
				}),
				new Kit( "§eKoch",new String[]{"§8x1§7 Holzschwert Sch§rfe 2","§8x8§7 Steaks","§8x1§7 Kuchen","§8x3§7 Golden§pfel"}, new ItemStack(Material.CAKE),PermissionType.SKYWARS_KIT_KOCH,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.RenameItem(UtilItem.EnchantItem(new ItemStack(Material.WOOD_SWORD),Enchantment.DAMAGE_ALL,2), "§7Gabel"),new ItemStack(Material.COOKED_BEEF,8),new ItemStack(Material.CAKE,1),new ItemStack(Material.GOLDEN_APPLE,3)})
				}),
				new Kit( "§eSprengmeister",new String[]{"§8x4§7 Creeper Spawner","§8x10§7 TnT","§8x1§7 Feuerzeug"}, new ItemStack(Material.TNT),PermissionType.SKYWARS_KIT_SPRENGMEISTER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment((creeper == null ? new ItemStack[]{new ItemStack(Material.TNT,10),new ItemStack(Material.FLINT_AND_STEEL)} : new ItemStack[]{new ItemStack(Material.TNT,10),new ItemStack(Material.FLINT_AND_STEEL),creeper.getItem(4)}))
				}),
				new Kit( "§eJ§ger",new String[]{"§8x1§7 Bogen","§8x10 §7Pfeile"}, new ItemStack(Material.ARROW),PermissionType.SKYWARS_KIT_JAEGER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.BOW),new ItemStack(Material.ARROW,10)})
				}),
				new Kit( "§eEnchanter",new String[]{"§8x1§7 Enchantment Table","§8x64 §7Enchantment Bottles"}, new ItemStack(Material.ENCHANTMENT_TABLE),PermissionType.SKYWARS_KIT_ENCHANTER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.ENCHANTMENT_TABLE),new ItemStack(Material.LAPIS_ORE,16),new ItemStack(Material.EXP_BOTTLE,64)})
				}),
				new Kit( "§eHeiler",new String[]{"§8x3§7 Tr§nke der Heilung","§8x3§7 Regenerations Tr§nke"}, new ItemStack(Material.POTION,1,(short)16389),PermissionType.SKYWARS_KIT_HEILER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.POTION,3,(short)16389),new ItemStack(Material.POTION,3,(short)16385)})
				}),
				new Kit( "§eSp§her",new String[]{"§8x1§7 Steinschwert","§8x3 §7Schnelligkeits Tr§nke"}, new ItemStack(Material.STONE_SWORD),PermissionType.SKYWARS_KIT_SPAEHER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.STONE_SWORD),new ItemStack(Material.POTION,3,(short)16386)})
				}),
				new Kit( "§eKampfmeister",new String[]{"§8x1§7 Anvil","§8x64§7 Enchantment Bottles","§8x1§7 Diamant Helm","§8x1§7 Enchantment Buch (Protection III)"}, new ItemStack(Material.ANVIL),PermissionType.SKYWARS_KIT_KAMPFMEISTER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.getEnchantmentBook(Enchantment.PROTECTION_ENVIRONMENTAL, 3),new ItemStack(Material.DIAMOND_HELMET),new ItemStack(Material.EXP_BOTTLE,64),new ItemStack(Material.ANVIL)})
				}),
				new Kit( "§eRitter",new String[]{"§8x1§7 Eisenschwert (Sch§rfe II)"}, new ItemStack(Material.IRON_SWORD),PermissionType.SHEEPWARS_KIT_ARROWMAN,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.IRON_SWORD), Enchantment.DAMAGE_ALL, 2)})
				}),
				new Kit( "§eFeuermeister",new String[]{"§8x1§7Feuerzeug","§8x2§7 Lava-Eimer","§8x2§7 Feuer Tr§nke"}, new ItemStack(Material.LAVA_BUCKET),PermissionType.SKYWARS_KIT_FEUERMEISTER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.POTION,2,(short)16387),new ItemStack(Material.FLINT_AND_STEEL,1),new ItemStack(Material.LAVA_BUCKET,2)})
				}),
				new Kit( "§eDroide",new String[]{"§8x1§7 Regenerations Trank","§8x2§7 Tr§nke der Heilung","§8x2§7 Vergiftungs Tr§nke","§8x2§7 Schadens Tr§nke"}, new ItemStack(Material.POTION),PermissionType.SKYWARS_KIT_DROIDE,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.POTION,1,(short)16385),new ItemStack(Material.POTION,2,(short)16388),new ItemStack(Material.POTION,2,(short)16389),new ItemStack(Material.POTION,2,(short)16396)})
				}),
				new Kit( "§eSto§er",new String[]{"§8x1§7 Holzschwert mit R§cksto§ 1"}, new ItemStack(Material.WOOD_SWORD),PermissionType.SKYWARS_KIT_STOSSER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.WOOD_SWORD), Enchantment.KNOCKBACK,1)})
				}),
				new Kit( "§eHase",new String[]{"§8x2§7 Schnelligkeits Treanke","§8x2§7 Sprungkraft Treanke","§8x8§7 Kartotten"}, new ItemStack(Material.CARROT),PermissionType.SKYWARS_KIT_HASE,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.POTION,2,(byte)0),new ItemStack(Material.POTION,2,(byte)0),new ItemStack(Material.CARROT,8)})
				}),
				new Kit( "§eRusher",new String[]{"§8x25§7 Granit Bloecke","§8x1§7 Steinschwert Schaerfe 1","§8x1§7 Goldbrustpanzer"}, new ItemStack(Material.GOLD_CHESTPLATE),PermissionType.SKYWARS_KIT_RUSHER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.COBBLESTONE,25),new ItemStack(Material.GOLD_CHESTPLATE),UtilItem.EnchantItem(new ItemStack(Material.STONE_SWORD), Enchantment.DAMAGE_ALL, 1)})
				}),
				new Kit( "§ePolizist",new String[]{"§8x1§7 Lederbrustpanzer","§8x1§7 Lederhose","§8x1§7 Stock mit Schaerfe 2"}, UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), DyeColor.BLUE),PermissionType.SKYWARS_KIT_POLIZIST,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.LSetColor(new ItemStack(Material.LEATHER_CHESTPLATE), DyeColor.BLUE),UtilItem.LSetColor(new ItemStack(Material.LEATHER_LEGGINGS), DyeColor.BLUE),UtilItem.RenameItem(UtilItem.EnchantItem(new ItemStack(Material.STICK), Enchantment.DAMAGE_ALL,2), "§7Knueppel")})
				}),
				new Kit( "§eFarmer",new String[]{"§8x1§7 Eisenspitzhacke mit Schaerfe 2 u. Efficiens 2","§8x16§7 Eier"}, new ItemStack(Material.IRON_PICKAXE),PermissionType.SKYWARS_KIT_FARMER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.EGG,16),UtilItem.EnchantItem(UtilItem.EnchantItem(new ItemStack(Material.IRON_PICKAXE), Enchantment.DIG_SPEED, 2), Enchantment.DAMAGE_ALL,2)})
				}),
				new Kit( "§eFischer",new String[]{"§8x1§7 Angel mit unbreaking 2","§8x1§7 Kettenr§stung mit Schutz 2"}, new ItemStack(Material.CHAINMAIL_CHESTPLATE),PermissionType.SKYWARS_KIT_FISCHER,KitType.KAUFEN,2000,500,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.FISHING_ROD), Enchantment.DURABILITY,2),UtilItem.EnchantItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE), Enchantment.PROTECTION_ENVIRONMENTAL,2)})
				}),
				new Kit( "§eVip",new String[]{"§8x1§7 Steinschwert","§8x1§7 Bogen","§8x8§7 Pfeile","§8x2§7 Heal Potions"}, new ItemStack(Material.GOLD_NUGGET),PermissionType.SKYWARS_KIT_VIP,KitType.VIP,0,0,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.STONE_SWORD),new ItemStack(Material.BOW),new ItemStack(Material.ARROW,8),new Potion(PotionType.INSTANT_HEAL, Tier.TWO, true).toItemStack(2)})
				}),
				new Kit( "§eUltra",new String[]{"§8x1§7 Steinschwert mit Schaerfe 1","§8x1§7 Eisenpickaxe","§8x16§7 Steine","§8x4§7 Heal Potions"}, new ItemStack(Material.GOLD_NUGGET),PermissionType.SKYWARS_KIT_ULTRA,KitType.ULTRA,0,0,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.STONE_SWORD), Enchantment.DAMAGE_ALL, 1),new ItemStack(Material.IRON_PICKAXE),new ItemStack(Material.STONE,16),new Potion(PotionType.INSTANT_HEAL, Tier.TWO, true).toItemStack(4)})
				}),
				new Kit( "§eLegend",new String[]{"§8x1§7 Eisenpanzer","§8x1§7 Eisenschuhe","§8x1§7 Steinschwert","§8x8§7 Steaks","§8x4§7 Heal Potions"}, new ItemStack(Material.IRON_INGOT),PermissionType.SKYWARS_KIT_LEGEND,KitType.LEGEND,0,0,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.STONE_SWORD),new ItemStack(Material.IRON_CHESTPLATE),new ItemStack(Material.IRON_BOOTS),new ItemStack(Material.COOKED_BEEF,8),new Potion(PotionType.INSTANT_HEAL, Tier.TWO, true).toItemStack(4)})
				}),
				new Kit( "§eMVP",new String[]{"§8x1§7 Steinschwert mit Ruecksto§","§8x1§7 Diamanthelm","§8x1§7 Diamantschuhe","§8x4§7 Heal Potion"}, new ItemStack(Material.GOLD_INGOT),PermissionType.SKYWARS_KIT_MVP,KitType.MVP,0,0,new Perk[]{
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.STONE_SWORD), Enchantment.KNOCKBACK, 1),new ItemStack(Material.DIAMOND_HELMET),new ItemStack(Material.DIAMOND_BOOTS),new Potion(PotionType.INSTANT_HEAL, Tier.TWO, true).toItemStack(4)})
				}),
				new Kit( "§eMVP+",new String[]{"§8x1§7 Diamanthelm","§8x1§7 Eisenbrustpanzer","§8x16§7 Steine","§8x1§7 Eisenschwert","§8x4§7 Heal Potion"}, new ItemStack(Material.DIAMOND),PermissionType.SKYWARS_KIT_MVPPLUS,KitType.MVP_PLUS,0,0,new Perk[]{
					new PerkEquipment(new ItemStack[]{new ItemStack(Material.IRON_SWORD),new ItemStack(Material.IRON_CHESTPLATE),new ItemStack(Material.DIAMOND_HELMET),new ItemStack(Material.STONE,16),new Potion(PotionType.INSTANT_HEAL, Tier.TWO, true).toItemStack(4)})
				}),
				new Kit( "§aSuperman",new String[]{"Der Superman ist das Beste kit in SkyWars!"}, new ItemStack(Material.DIAMOND_SWORD),PermissionType.SKYWARS_KIT_SUPERMAN,KitType.ADMIN,2000,new Perk[]{
					new PerkNoHunger(),
					new PerkEquipment(new ItemStack[]{UtilItem.EnchantItem(new ItemStack(Material.IRON_SWORD,1), Enchantment.DAMAGE_ALL,4),new ItemStack(Material.BOW), new ItemStack(Material.LEATHER_HELMET,1), UtilItem.EnchantItem(new ItemStack(Material.IRON_CHESTPLATE,1), Enchantment.PROTECTION_ENVIRONMENTAL,3), UtilItem.EnchantItem(new ItemStack(Material.LEATHER_LEGGINGS,1), Enchantment.PROTECTION_ENVIRONMENTAL,4), new ItemStack(Material.LEATHER_BOOTS)}),
					new PerkSneakDamage(1),
					new PerkPoisen(10,50),
					new PerkHolzfaeller(),
					new PerkNoFiredamage(),
					new PerkNoFalldamage(),
					new PerkArrowFire(80),
					new PerkDoubleJump(),
					new PerkNoKnockback(game.getManager().getInstance()),
					new PerkNoExplosionDamage(),
					new PerkTNT(),
					new PerkHealByHit(60, 6),
					new PerkHeal(6),
					new PerkMoreHearth(6, 60),
					new PerkArrowInfinity(),
					new PerkWalkEffect(Effect.CRIT,10)
				})
			};
	}
	
	public static ItemStack Tools(){
		switch(UtilMath.r(5)){
		case 0: return new ItemStack(Material.DIAMOND_SWORD);
		case 1: return new ItemStack(Material.IRON_SWORD);
		case 2: return new ItemStack(Material.DIAMOND_AXE);
		case 3: return new ItemStack(Material.DIAMOND_PICKAXE);
		case 4: return new ItemStack(Material.BOW);
		default: return new ItemStack(Material.WOOD_SWORD);
		}
	}
	
	public static ItemStack DiaRuestung(){
		switch(UtilMath.r(4)){
		case 0: return new ItemStack(Material.DIAMOND_HELMET);
		case 1: return new ItemStack(Material.DIAMOND_CHESTPLATE);
		case 2: return new ItemStack(Material.DIAMOND_LEGGINGS);
		case 3: return new ItemStack(Material.DIAMOND_BOOTS);
		default:
			return new ItemStack(Material.LEATHER_HELMET);
		}
	}
	
	public static ItemStack IronRuestung(){
		switch(UtilMath.r(4)){
		case 0: return new ItemStack(Material.IRON_HELMET);
		case 1: return new ItemStack(Material.IRON_CHESTPLATE);
		case 2: return new ItemStack(Material.IRON_LEGGINGS);
		case 3: return new ItemStack(Material.IRON_BOOTS);
		default:
			return new ItemStack(Material.LEATHER_HELMET);
		}
	}
	
	public static ItemStack rdmItem(){
		ItemStack item;
		int r = UtilMath.r(1000);
		if(r>=150&&r<=155){
			item=UtilItem.RenameItem(new ItemStack(Material.SLIME_BALL), "§bX");
			item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 5);
			return item;
		}
		
		switch(UtilMath.r(6)){
		case 0: item = Sonstiges();break;
		case 1: item = IronRuestung();break;
		case 2: item = DiaRuestung();break;
		case 3: item = Tools();break;
		case 4: item = Sonstiges();break;
		case 5: item = Sonstiges();break;
		default: item = new ItemStack(Material.APPLE);break;
		}
		
		if(item.getType()==Material.BOW){
			r=UtilMath.r(100);
			if(r>=0&&r<=40){
				item.addEnchantment(Enchantment.ARROW_DAMAGE, 3);
			}else if(r>=40&&r<=60){
				item.addEnchantment(Enchantment.ARROW_FIRE, 1);
			}
		}else if(item.getType()==Material.IRON_CHESTPLATE){
			r=UtilMath.r(100);
			if(r>=0&&r<=30){
				item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
			}else if(r>=30&&r<=50){
				item.addEnchantment(Enchantment.PROTECTION_FIRE, 4);
			}
		}else if(item.getType()==Material.DIAMOND_CHESTPLATE){
			r=UtilMath.r(100);
			if(r>=0&&r<=40){
				item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
			}else if(r>=30&&r<=50){
				item.addEnchantment(Enchantment.PROTECTION_FIRE, 4);
			}
		}else if(item.getType()==Material.IRON_SWORD){
			item.addEnchantment(Enchantment.DAMAGE_ALL, 3);
		}else if(item.getType()==Material.DIAMOND_SWORD){
			r=UtilMath.r(100);
			if(r>=0&&r<=40){
				item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
			}else if(r>=40&&r<=60){
				item.addEnchantment(Enchantment.DAMAGE_ALL, 1);
				item.addEnchantment(Enchantment.FIRE_ASPECT, 1);
				item.addEnchantment(Enchantment.KNOCKBACK, 1);
			}
		}else if(item.getType()==Material.DIAMOND_AXE){
			item.addEnchantment(Enchantment.DIG_SPEED, 3);
		}else if(item.getType()==Material.DIAMOND_PICKAXE){
			item.addEnchantment(Enchantment.DIG_SPEED, 3);
		}
		return item;
	}
	
	public static Team getChestSpawn(Team team){
		switch(team){
		case RED:return Team.SHEEP_RED;
		case BLUE:return Team.SHEEP_BLUE;
		case YELLOW:return Team.SHEEP_YELLOW;
		case GREEN:return Team.SHEEP_GREEN;
		case GRAY:return Team.SHEEP_GRAY;
		case PINK:return Team.SHEEP_PINK;
		case ORANGE:return Team.SHEEP_ORANGE;
		case PURPLE:return Team.SHEEP_PURPLE;
		case WHITE:return Team.SHEEP_WHITE;
		case BLACK:return Team.SHEEP_BLACK;
		case CYAN:return Team.SHEEP_CYAN;
		case AQUA:return Team.SHEEP_AQUA;
		default:
		return Team.SHEEP_RED;
		}
	}
	
	public static Material rdmBlock(){
		switch(UtilMath.r(3)){
		case 0:return Material.STONE;
		case 1:return Material.DIRT;
		case 2:return Material.WOOD;
		default: return Material.BEDROCK;
		}
	}
	
	public static ItemStack rdmFood(){
		switch(UtilMath.r(4)){
		case 0:return new ItemStack(Material.COOKED_BEEF,UtilMath.RandomInt(16, 8));
		case 1:return new ItemStack(Material.BREAD,UtilMath.RandomInt(16, 8));
		case 2:return new ItemStack(Material.CAKE);
		case 3:return new ItemStack(Material.CARROT,UtilMath.RandomInt(16, 8));
		default: return new ItemStack(Material.GOLDEN_CARROT,UtilMath.RandomInt(16, 8));
		}
	}
	
	public static Material rdmHelm(){
		switch(UtilMath.r(4)){
		case 0:return Material.DIAMOND_HELMET;
		case 1:return Material.GOLD_HELMET;
		case 2:return Material.IRON_HELMET;
		case 3:return Material.CHAINMAIL_HELMET;
		default: return Material.LEATHER_HELMET;
		}
	}
	
	public static Material rdmChestplate(){
		switch(UtilMath.r(4)){
		case 0:return Material.DIAMOND_CHESTPLATE;
		case 1:return Material.GOLD_CHESTPLATE;
		case 2:return Material.IRON_CHESTPLATE;
		case 3:return Material.CHAINMAIL_CHESTPLATE;
		default: return Material.LEATHER_CHESTPLATE;
		}
	}
	
	public static Material rdmLeggings(){
		switch(UtilMath.r(4)){
		case 0:return Material.DIAMOND_LEGGINGS;
		case 1:return Material.GOLD_LEGGINGS;
		case 2:return Material.IRON_LEGGINGS;
		case 3:return Material.CHAINMAIL_LEGGINGS;
		default: return Material.LEATHER_LEGGINGS;
		}
	}
	
	public static Material rdmTool(){
		switch(2){
		case 0: return Material.IRON_PICKAXE;
		case 1: return Material.IRON_AXE;
		default: return Material.DIAMOND_PICKAXE;
		}
	}
	
	public static Material rdmBoots(){
		switch(UtilMath.r(4)){
			case 0:return Material.DIAMOND_BOOTS;
			case 1:return Material.GOLD_BOOTS;
			case 2:return Material.IRON_BOOTS;
			case 3:return Material.CHAINMAIL_BOOTS;
			default: return Material.LEATHER_BOOTS;
		}
	}
	
	public static ItemStack rdmPotion(){
		switch(UtilMath.r(5)){
			case 0:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16421);
			case 1:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16417);
			case 2:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16389);
			case 3:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16385);
			case 4:return new ItemStack(Material.POTION,UtilMath.RandomInt(3, 1),(short)16387);
			default: return new ItemStack(Material.POTION,1);
		}
	}
	
	public static void loadWorld(MultiGame game,HashMap<Chest,ArrayList<String>> template,HashMap<String,Integer> template_type){
		int i=0;
		Chest[] chests;
		for(Team t : game.getWorldData().getTeams(game).keySet()){
			if(game.getWorldData().getTeams(game).get(t).isEmpty())continue;
			chests=new Chest[game.getWorldData().getLocs(game,UtilSkyWars1vs1.getChestSpawn(t)).size()];

			i=0;
			for(Location loc : game.getWorldData().getLocs(game,UtilSkyWars1vs1.getChestSpawn(t))){
				if(!(loc.getBlock().getState() instanceof Chest)){
					loc.getBlock().setType(Material.CHEST);
				}
				chests[i]=((Chest)loc.getBlock().getState());
				chests[i].getInventory().clear();
				i++;
			}
			
			if(i!=0)fillIslandChests(t,chests,template,template_type);
		}
		
		Chest chest;
		for(Location loc : game.getWorldData().getLocs(game,Team.VILLAGE_RED)){
			if(!(loc.getBlock().getState() instanceof Chest)){
				loc.getBlock().setType(Material.CHEST);
			}
			if(loc.getBlock().getState() instanceof Chest){
				chest=(Chest)loc.getBlock().getState();
				chest.getInventory().clear();
				for (int nur = 0; nur < UtilMath.RandomInt(6,3); nur++) {
					chest.getInventory().setItem(UtilSkyWars1vs1.emptySlot(chest.getInventory()), UtilSkyWars1vs1.rdmItem()); 
				}
			}
		}
	}
	
	public static void fillIslandChests(Team t,Chest[] chests,HashMap<Chest,ArrayList<String>> template,HashMap<String,Integer> template_type){
		//SWORD BLOCK HELM CHESTPLATE LEGGINGS BOOTS BOW ARROW POTION FOOD SNOWBALL EGG WEB LAVA-BUCKET WATER-BUCKET
		template.clear();
		template_type.clear();
		template_type.put("SWORD",3);
		template_type.put("BLOCK",3);
		template_type.put("HELM",2);
		template_type.put("CHESTPLATE",2);
		template_type.put("LEGGINGS",2);
		template_type.put("BOOTS",2);
		template_type.put("ARROW",1);
		template_type.put("POTION",4);
		template_type.put("FOOD",3);
		template_type.put("SNOWBALL",3);
		template_type.put("EGG",3);
		template_type.put("WEB",3);
		template_type.put("LAVA-BUCKET",1);
		template_type.put("WATER-BUCKET",1);
		template_type.put("TNT",1);
		template_type.put("FIRE",1);
		template_type.put("BOW",1);
		template_type.put("TOOL",2);
		
		for(Chest chest : chests){
			chest.getInventory().clear();
			template.put(chest, new ArrayList<String>());
		}
		
		if(UtilMath.r(100)>70){
			add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"BOW");
			add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"ARROW");
		}else{
			template_type.remove("BOW");
			template_type.remove("ARROW");
		}
		
		add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"SWORD");
		add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"BLOCK");
		add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"TOOL");
		add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"POTION");
		add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"CHESTPLATE");
		add(template,template_type, (Chest)template.keySet().toArray()[UtilMath.r(template.size())] ,"FOOD");
		
		int r;
		ArrayList<String> list;
		for(Chest chest : template.keySet()){
			list=template.get(chest);
			r=UtilMath.RandomInt(8, 5);
			if(r<=list.size())continue;
			for(int i = 0; i < (r-list.size()); i++){
				add(template,template_type,chest, (String)template_type.keySet().toArray()[UtilMath.r(template_type.size())]);
			}
		}
		
		list=null;
		
		for(Chest chest : template.keySet()){
			for(String i : template.get(chest)){
				switch(i){
				case "SWORD":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(UtilItem.rdmSchwert()) );
					break;
				case "HELM":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmHelm()));
					break;
				case "CHESTPLATE":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmChestplate()));
					break;
				case "LEGGINGS":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmLeggings()));
					break;
				case "BOOTS":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmBoots()));
					break;
				case "BOW":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.BOW));
					break;
				case "ARROW":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.ARROW,UtilMath.RandomInt(16, 8)));
					break;
				case "BLOCK":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmBlock(),UtilMath.RandomInt(32, 16)));
					break;
				case "POTION":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmPotion()));
					break;
				case "FOOD":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , rdmFood());
					break;
				case "SNOWBALL":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.SNOW_BALL,UtilMath.RandomInt(16, 8)));
					break;
				case "EGG":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.EGG,UtilMath.RandomInt(16, 8)));
					break;
				case "WEB":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.WEB,UtilMath.RandomInt(16, 8)));
					break;
				case "LAVA-BUCKET":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.LAVA_BUCKET));
					break;
				case "WATER-BUCKET":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.WATER_BUCKET));
					break;
				case "TNT":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.TNT,UtilMath.RandomInt(3, 1)));
					break;
				case "FIRE":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(Material.FLINT_AND_STEEL));
					break;
				case "TOOL":
					chest.getInventory().setItem( emptySlot(chest.getInventory()) , new ItemStack(rdmTool()));
					break;
				default:
					System.out.println("Island Chest FAIL: "+i);
					break;
				}
			}
			template.get(chest).clear();
		}
		
		for(Chest chest : template.keySet()){
			for(ItemStack item : chest.getInventory().getContents()){
				if(item!=null&&item.getType()!=Material.AIR){
					if(item.getType()==Material.BOW){
						switch(UtilMath.r(3)){
						case 0:item.addEnchantment(Enchantment.ARROW_DAMAGE, 1);break;
						case 1:item.addEnchantment(Enchantment.ARROW_DAMAGE, 3);break;
						case 2:item.addEnchantment(Enchantment.ARROW_DAMAGE, 1);item.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);break;
						default:break;
						}
					}else if(UtilItem.isSword(item)){
						if(item.getType()==Material.WOOD_SWORD){
							item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
						}else if(item.getType()==Material.DIAMOND_SWORD){
							r=UtilMath.r(100);
							if(r>=50&&r<=55){
								item.addEnchantment(Enchantment.FIRE_ASPECT, 1);
							}else if(r>=0&&r<=40){
								item.addEnchantment(Enchantment.DAMAGE_ALL, 1);
							}
						}else if(item.getType()==Material.GOLD_SWORD){
							r=UtilMath.r(100);
							item.addEnchantment(Enchantment.DURABILITY, 1);
							if(r>=0&&r<=60){
								item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
							}else if(r>=60&&r<=100){
								item.addEnchantment(Enchantment.KNOCKBACK, 1);
							}
						}else if(item.getType()==Material.STONE_SWORD){
							item.addEnchantment(Enchantment.DAMAGE_ALL, 1);
						}else if(item.getType()==Material.IRON_SWORD){
							r=UtilMath.r(100);
							if(r>=40&&r<=80){
								item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
							}
						}
					}else if(UtilItem.isArmor(item)){
						if(UtilItem.isGoldArmor(item)){
							r=UtilMath.r(100);
							if(r>=0&&r<=60){
								item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
							}else{
								item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
							}
						}else if(UtilItem.isChainmailArmor(item)){
							if(UtilItem.isChestplate(item)){
								r=UtilMath.r(100);
								if(r>=60&&r<=80){
									item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
								}
							}
							item.addEnchantment(Enchantment.PROTECTION_FIRE, 3);
						}else if(UtilItem.isIronArmor(item)){
							r=UtilMath.r(100);
							if(r>=60&&r<=80){
								item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
							}
							item.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 3);
						}
					}
				}
			}
		}
	}
	
	public static int emptySlot(Inventory inv){
		int slot=0;
		for(int i = 0 ; i<2000; i++){
			slot=UtilMath.r(inv.getSize());
			if(inv.getItem(slot)==null||inv.getItem(slot).getType()==Material.AIR){
				return slot;
			}
		}
		System.out.println("NOT FIND A EMPTY SLOT");
		return 0;
	}
	
	public static void add(HashMap<Chest,ArrayList<String>> template,HashMap<String,Integer> template_type,Chest g,String type){
		template.get(g).add(type);
		int a=template_type.get(type);
		a--;
		template_type.remove(type);
		if(a!=0)template_type.put(type, a);
		a=0;
	}
}
