if VehicleZoneDistribution then

-- PaRkIngStalL --

VehicleZoneDistribution.parkingstall.vehicles["Base.isoContainer2"] = {index = -1, spawnChance = 1};
VehicleZoneDistribution.parkingstall.vehicles["Base.isoContainer3tanker"] = {index = -1, spawnChance = 1};

-- McCoy spawn --

VehicleZoneDistribution.mccoy.vehicles["Base.isoContainer2"] = {index = -1, spawnChance = 99};

-- Fosoil spawn --

VehicleZoneDistribution.fossoil.vehicles["Base.isoContainer3tanker"] = {index = -1, spawnChance = 75};

-- Postal spawn --

VehicleZoneDistribution.postal.vehicles["Base.isoContainer3tanker"] = {index = -1, spawnChance = 75};

-- TrailerPark spawn --

VehicleZoneDistribution.trailerpark.vehicles["Base.isoContainer2"] = {index = -1, spawnChance = 55};
VehicleZoneDistribution.trailerpark.vehicles["Base.isoContainer3tanker"] = {index = -1, spawnChance = 30};

-- Military spawn --

VehicleZoneDistribution.military = VehicleZoneDistribution.military or {}
VehicleZoneDistribution.military.vehicles = VehicleZoneDistribution.military.vehicles or {}
VehicleZoneDistribution.military.vehicles["Base.isoContainer2"] = {index = 1, spawnChance = 20};
VehicleZoneDistribution.military.vehicles["Base.isoContainer2"] = {index = 2, spawnChance = 10};
VehicleZoneDistribution.military.vehicles["Base.isoContainer3tanker"] = {index = 1, spawnChance = 20};
VehicleZoneDistribution.military.vehicles["Base.isoContainer3tanker"] = {index = 3, spawnChance = 10};

-- Farm spawn --

VehicleZoneDistribution.farm = VehicleZoneDistribution.farm or {}
VehicleZoneDistribution.farm.vehicles = VehicleZoneDistribution.farm.vehicles or {}
VehicleZoneDistribution.farm.vehicles["Base.isoContainer2"] = {index = -1, spawnChance = 40};
VehicleZoneDistribution.farm.vehicles["Base.isoContainer3tanker"] = {index = -1, spawnChance = 30};

end