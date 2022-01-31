-- define weapons to be attached to zombies when creating them
-- random knives inside their neck, spear in their stomach, meatcleaver in their back...
-- this is used in IsoZombie.addRandomAttachedWeapon()

AttachedWeaponDefinitions = AttachedWeaponDefinitions or {};
-- Global chance of having an attached weapon, if we pass this we gonna add randomly one from the list

-- swat shield police's LHand
AttachedWeaponDefinitions.RiotShieldSwatShieldLelftHand = {
	chance = 90,
	outfit = {"Swat"},
	weaponLocation =  {"Shield in Lhand"},
	bloodLocations = nil,
	addHoles = false,
	daySurvived = 0,
	weapons = {
		"Base.RiotShieldSwat",
	},
}

AttachedWeaponDefinitions.RiotShieldPoliceShieldLelftHand = {
	chance = 90,
	outfit = {"AntiRiot"},
	weaponLocation =  {"Shield in Lhand"},
	bloodLocations = nil,
	addHoles = false,
	daySurvived = 0,
	weapons = {
		"Base.RiotShieldPolice",
	},
}