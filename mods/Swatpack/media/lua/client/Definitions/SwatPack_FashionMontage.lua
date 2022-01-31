-- INSTRUCTIONS - NAMING THE FILE --
-- Rename this file, ideally your mod's ID followed by "_FashionMontage.lua"
-- Make sure this filename starts with an english letter --

-- INSTRUCTIONS - ADDING YOUR ITEMS --
-- Add each of your items to the list that corresponds to its BodyLocation
-- If it does not have a unique DisplayName it will not appear in the dropdown


-- This ensures the player won't get any error messages if they aren't using Fashion Montage
if getActivatedMods():contains("FashionMontage") then
  require "Definitions/_OGSN_FashionMontage"
  require "Definitions/_OGSN_FashionMontageVanillaClothes"
else
  return
end

-- pointless statement is pointless
ClothingSelectionDefinitions = ClothingSelectionDefinitions

local clothing = {
  -- these lists are named after the BodyLocation of the item
  -- If your item's BodyLocation = Hat then put it in "Hat"
  Hat = {
    "Base.Hat_SwatHelmet",
    "Base.Hat_PoliceRiotHelmet",
    "Base.Hat_SWATRiotHelmet",
    "Base.Hat_Antibombhelmet",
  },
  TankTop = {}, 
  Tshirt = {},      
  Shirt = {},       
  Socks = {},       
  Pants = {"Base.Trousers_Swat",}, 
  Skirt = {},
  Dress = {},
  Shoes = {"Base.Shoes_RiotBoots","Base.Shoes_SwatBoots",}, 
  Eyes = {"Base.Glasses_SwatGoggles",},
  LeftEye = {},
  RightEye = {},
  BeltExtra = {},
  AmmoStrap = {},
  Mask = {"Base.Hat_Balaclava_Swat",},
  MaskEyes = {"Base.Hat_SwatGasMask",},
  Underwear = {"Base.AntibombSuitP2",},
  FullHat = {"Base.Hat_SWATRiotHelmet","Base.Hat_SWATRiotHelmet2",},
  Torso1Legs1 = {},
  Neck = {"Base.SwatNeck",},
  Hands = {"Base.Gloves_SwatGloves","Base.Gloves_RiotGloves",},
  Legs1 = {},
  Sweater = {"Base.SwatKneePads",},
  Jacket = {"Base.Jacket_Swat",},
  FullSuit = {"Base.AntibombSuit","Base.RiotArmorSuit",},
  FullSuitHead = {},
  FullTop = {},
  BathRobe = {},
  TorsoExtra = {"Base.Vest_BulletSwat",},
  Tail = {"Base.SWATPad",},
  Back = {"Base.Bag_BigSwatBag","Base.Bag_PoliceBag",},
  Scarf = {"Base.SwatElbowPads",},
  FannyPackFront = {},
  Necklace = {},
  Necklace_Long = {},
  Nose = {},
  LeftWrist = {},
  RightWrist = {},
  Right_RingFinger = {},
  Left_RingFinger = {},
  Right_MiddleFinger = {},
  Left_MiddleFinger = {},
  Ears = {},
  EarTop = {},
  MaskFull = {},
  Belt421 = {"Base.SWATPouch",},

  -- If your mod adds new bodylocations, you can include its items here
  -- as long as you also include the new bodyLocation's name
  -- in the next array (see below)
}

local bodyLocations = {"Belt421"

  -- if your mod adds brand new bodyLocations, list each of them here as strings
  -- for example:
  -- "KneePads",
  -- "ThirdArm",
  -- "SidewaysBaseballCap",
}

if #bodyLocations > 0 then
  _OGSN_FashionMontage.addBodyLocations(bodyLocations);
end
_OGSN_FashionMontage.addClothingItems(clothing);
