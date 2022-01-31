-- pull the vehicle distributions into a local table
local distributionTable = VehicleDistributions[1]


VehicleDistributions.SWATPACKVEHICLESGearTrunk = {
    rolls = 4,
    items ={
	"RiotShieldSwat", 5,
	"Hat_SwatHelmet", 4,
	"Vest_BulletSwat", 2,
	"Trousers_Swat", 1,
	"Shoes_SwatBoots", 3,
	"Shoes_SwatBoots", 3,
	"SwatElbowPads", 6,
    	"SwatKneePads", 6,
    	"SwatNeck", 3,
    	"RiotShotgun", 2,
    	"Co2ShortRiotShotgun", 2,
    	"RubberShells", 3,
    	"RubberShellsBox", 3,
    }
}

VehicleDistributions.SWATPACKVEHICLESSeat = {
    rolls = 4,
    items ={
	 "Jacket_Swat", 3,
         "Hat_Balaclava_Swat", 5,
   	 "Hat_SwatGasMask", 4,
   	 "RubberShells", 3,
   	 "RubberShellsBox", 3,
    }
}

VehicleDistributions.SWATPACKVEHICLESGloveBox = {
    rolls = 2,
    items ={
	"Glasses_SwatGoggles", 5,
        "Gloves_SwatGloves", 4,
        "Hat_Balaclava_Swat", 4,
        "SWATPouch", 2,
    }
}

VehicleDistributions.RIOTPACKVEHICLESGearTrunk = {
    rolls = 6,
    items ={
		"RiotShieldPolice", 6,
		"Hat_PoliceRiotHelmet", 7,
		"RiotArmorSuit", 3,
		"Shoes_RiotBoots", 5,
		"Bag_PoliceBag", 4,
    }
}

VehicleDistributions.RIOTPACKVEHICLESSeat = {
    rolls = 3,
    items ={
		"RiotShotgun", 4,
		"RubberShellsBox", 5,
		"Co2ShortRiotShotgun", 3,
    }
}

VehicleDistributions.RIOTPACKVEHICLESGloveBox = {
    rolls = 6,
    items ={
        "RubberShells", 4,
	"Gloves_RiotGloves", 2,
    }
}


VehicleDistributions.BANKPACKVEHICLESGearTrunk = {
    rolls = 20,
    items ={
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
	"Money", 10,
    }
}

VehicleDistributions.BANKPACKVEHICLESSeat = {
    rolls = 2,
    items ={
	"Cigarettes", 10,
	"Cigarettes", 10,
    }
}

VehicleDistributions.BANKPACKVEHICLESGloveBox = {
    rolls = 2,
    items ={
	"Pistol2", 10,
    }
}


-- add a new SWAT distributions table
VehicleDistributions.SWATPACKVEHICLES = {
    TruckBed = VehicleDistributions.SWATPACKVEHICLESGearTrunk;
    SeatFrontRight = VehicleDistributions.SWATPACKVEHICLESSeat;
    SeatFrontLeft = VehicleDistributions.SWATPACKVEHICLESSeat;
    GloveBox = VehicleDistributions.SWATPACKVEHICLESGloveBox;
}

-- add a new RIOT distributions table
VehicleDistributions.RIOTPACKVEHICLES = {
    TruckBed = VehicleDistributions.RIOTPACKVEHICLESGearTrunk;
    SeatFrontRight = VehicleDistributions.RIOTPACKVEHICLESSeat;
    SeatFrontLeft = VehicleDistributions.RIOTPACKVEHICLESSeat;
    GloveBox = VehicleDistributions.RIOTPACKVEHICLESGloveBox;
}

-- add a new BANK distributions table
VehicleDistributions.BANKPACKVEHICLES = {
    TruckBed = VehicleDistributions.BANKPACKVEHICLESGearTrunk;
    SeatFrontRight = VehicleDistributions.BANKPACKVEHICLESSeat;
    SeatFrontLeft = VehicleDistributions.BANKPACKVEHICLESSeat;
    GloveBox = VehicleDistributions.BANKPACKVEHICLESGloveBox;
}

-- use the custom SWAT loot table for the police StepVanSwat and SwatTruck
distributionTable["StepVanSwat"] = { Normal = VehicleDistributions.SWATPACKVEHICLES; }
distributionTable["SwatTruck"] = { Normal = VehicleDistributions.SWATPACKVEHICLES; }

-- use the custom RIOT loot table for the police RiotTruck
distributionTable["RiotTruck"] = { Normal = VehicleDistributions.RIOTPACKVEHICLES; }

-- use the custom BANK loot table for the police Bank Transport
distributionTable["BankTruck"] = { Normal = VehicleDistributions.BANKPACKVEHICLES; }