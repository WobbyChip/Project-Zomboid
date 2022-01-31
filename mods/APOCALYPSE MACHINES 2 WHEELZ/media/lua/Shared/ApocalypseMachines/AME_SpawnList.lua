require "VehicleZoneDefinition"
--if VehicleZoneDistribution then 
VehicleZoneDistribution = VehicleZoneDistribution or {}
-- Parking Stall, common parking stall with random cars, the most used one (shop parking lots, houses etc.)
VehicleZoneDistribution.parkingstall.vehicles["Base.AME_Eagle"] = {index = -1, spawnChance = 1.0}
VehicleZoneDistribution.parkingstall.vehicles["Base.AME_Patriot"] = {index = -1, spawnChance = 0.5}


-- Trailer Parks, have a chance to spawn burnt cars, some on top of each others, it's like a pile of junk cars
VehicleZoneDistribution.trailerpark.vehicles["Base.AME_Eagle"] = {index = -1, spawnChance = 10}
VehicleZoneDistribution.trailerpark.vehicles["Base.AME_Patriot"] = {index = -1, spawnChance = 10}

-- bad vehicles, moslty used in poor area, sometimes around pub etc.
VehicleZoneDistribution.bad.vehicles["Base.AME_Eagle"] = {index = -1, spawnChance = 2.5}
VehicleZoneDistribution.bad.vehicles["Base.AME_Patriot"] = {index = -1, spawnChance = 1}

-- medium vehicles, used in some of the good looking area, or in suburbs
--VehicleZoneDistribution.medium.vehicles["Base.AME_Eagle"] = {index = -1, spawnChance = 5}


-- -- good vehicles, used in good looking area, they're meant to spawn only good cars, so they're on every good looking house.
-- VehicleZoneDistribution.good.vehicles["Base.AME_Eagle"] = {index = -1, spawnChance = 10}


VehicleZoneDistribution.junkyard.vehicles["Base.AME_Eagle"] = {index = -1, spawnChance = 0.25}


VehicleZoneDistribution.ambulance.vehicles["Base.AME_Patriot"] = {index = -1, spawnChance = 20}