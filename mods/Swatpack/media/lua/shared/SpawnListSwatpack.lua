local getActivatedMods = getActivatedMods
local size = size
local get = get

local function isMod(mod_Name)
    local mods = getActivatedMods();
    for i=0, mods:size()-1, 1 do
        if mods:get(i) == mod_Name then
            return true;
        end
    end
    return false;
end

if VehicleZoneDistribution then -- check if the table exists for backwards compatibility

-- ****************************** --
--          SPECIAL VEHICLES      --
-- ****************************** --

-- police


VehicleZoneDistribution.police = {};
VehicleZoneDistribution.police.vehicles = {};
VehicleZoneDistribution.police.vehicles["Base.PickUpVanLightsPolice"] = {index = 0, spawnChance = 40};
VehicleZoneDistribution.police.vehicles["Base.CarLightsPolice"] = {index = 0, spawnChance = 60};
VehicleZoneDistribution.police.chanceToSpawnNormal = 40;
VehicleZoneDistribution.police.specialCar = true;
VehicleZoneDistribution.police.spawnRate = 20;

    if isMod("FRUsedCarsBETA") then
VehicleZoneDistribution.police = {};
VehicleZoneDistribution.police.vehicles = {};
VehicleZoneDistribution.police.vehicles["Base.91blazerpd"] = {index = -1, spawnChance = 20};
VehicleZoneDistribution.police.vehicles["Base.85vicsheriff"] = {index = -1, spawnChance = 20};
VehicleZoneDistribution.police.vehicles["Base.hmmwvpolice"] = {index = -1, spawnChance = 10};
VehicleZoneDistribution.police.vehicles["Base.hmmwvblackopps"] = {index = -1, spawnChance = 1};
VehicleZoneDistribution.police.vehicles["Base.blazerblackops"] = {index = -1, spawnChance = 1};
VehicleZoneDistribution.police.vehicles["Base.m35a2blackopps"] = {index = -1, spawnChance = 1};
VehicleZoneDistribution.police.vehicles["Base.m151blackopps"] = {index = -1, spawnChance = 1};
    end

    if isMod("Swatpack") then
	VehicleZoneDistribution.police = {};
	VehicleZoneDistribution.police.vehicles = {};
        VehicleZoneDistribution.police.vehicles["Base.StepVanSwat"] = {index = -1, spawnChance = 15};
        VehicleZoneDistribution.police.vehicles["Base.RiotTruck"] = {index = -1, spawnChance = 15}; 
        VehicleZoneDistribution.police.vehicles["Base.SwatTruck"] = {index = -1, spawnChance = 15}; 
        VehicleZoneDistribution.police.vehicles["Base.BankTruck"] = {index = -1, spawnChance = 15};
	VehicleZoneDistribution.junkyard = {};
	VehicleZoneDistribution.junkyard.vehicles = {}; 
	VehicleZoneDistribution.junkyard.vehicles["Base.StepVanSwat"] = {index = -1, spawnChance = 5};
	VehicleZoneDistribution.junkyard.vehicles["Base.RiotTruck"] = {index = -1, spawnChance = 5};
	VehicleZoneDistribution.junkyard.vehicles["Base.SwatTruck"] = {index = -1, spawnChance = 5};
	VehicleZoneDistribution.junkyard.vehicles["Base.BankTruck"] = {index = -1, spawnChance = 5};
    end

    if isMod("VileM113APC") then
	VehicleZoneDistribution.police = {};
	VehicleZoneDistribution.police.vehicles = {};
        VehicleZoneDistribution.police.vehicles["Base.m113a1police"] = {index = -1, spawnChance = 5};
        VehicleZoneDistribution.police.vehicles["Base.m113a1"] = {index = -1, spawnChance = 0};   
    end

    -- Police Zones
    -- Ranger Zones
    -- Farm Zones
end