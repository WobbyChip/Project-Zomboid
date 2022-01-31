require "VehicleZoneDefinition"
--if VehicleZoneDistribution then 
VehicleZoneDistribution = VehicleZoneDistribution or {}
-- Parking Stall, common parking stall with random cars, the most used one (shop parking lots, houses etc.)
VehicleZoneDistribution.parkingstall.vehicles["Base.AM2_DirtDemon"] = {index = -1, spawnChance = 0.2}
VehicleZoneDistribution.parkingstall.vehicles["Base.AM2_DirtDemon"] = {index = -1, spawnChance = 0.05}
VehicleZoneDistribution.parkingstall.vehicles["Base.AM2_SkullKing"] = {index = -1, spawnChance = 0.25}
VehicleZoneDistribution.parkingstall.vehicles["Base.AM2_Fireball"] = {index = -1, spawnChance = 0.75}
VehicleZoneDistribution.parkingstall.vehicles["Base.AM2_Vesper"] = {index = -1, spawnChance = 0.75}
VehicleZoneDistribution.parkingstall.vehicles["Base.AM2_Vesper2"] = {index = -1, spawnChance = 1.0}
VehicleZoneDistribution.parkingstall.vehicles["Base.AM2_Vesper3"] = {index = -1, spawnChance = 0.25}
VehicleZoneDistribution.parkingstall.vehicles["Base.AM2_Commodore"] = {index = -1, spawnChance = 0.75}
VehicleZoneDistribution.parkingstall.vehicles["Base.AM2_Warhorse"] = {index = -1, spawnChance = 0.125}
VehicleZoneDistribution.parkingstall.vehicles["Base.AM2_Warhorse2"] = {index = -1, spawnChance = 0.06}
VehicleZoneDistribution.parkingstall.vehicles["Base.AM2_Warhorse3"] = {index = -1, spawnChance = 0.06}
VehicleZoneDistribution.parkingstall.vehicles["Base.AM2_Camel"] = {index = -1, spawnChance = 0.375}
VehicleZoneDistribution.parkingstall.vehicles["Base.AM2_Camel2"] = {index = -1, spawnChance = 0.375}
--VehicleZoneDistribution.parkingstall.vehicles["Base.AM2_Courier"] = {index = -1, spawnChance = 0.75}
VehicleZoneDistribution.parkingstall.vehicles["Base.AM2_Steelhorse"] = {index = -1, spawnChance = 0.125}
VehicleZoneDistribution.parkingstall.vehicles["Base.AM2_Steelhorse2"] = {index = -1, spawnChance = 0.125}
VehicleZoneDistribution.parkingstall.vehicles["Base.AM2_Kaiju"] = {index = -1, spawnChance = 0.75}


-- Trailer Parks, have a chance to spawn burnt cars, some on top of each others, it's like a pile of junk cars

VehicleZoneDistribution.trailerpark.vehicles["Base.AM2_DirtDemon"] = {index = -1, spawnChance = 7.5}
VehicleZoneDistribution.trailerpark.vehicles["Base.AM2_DirtDemon"] = {index = -1, spawnChance = 2.5}
VehicleZoneDistribution.trailerpark.vehicles["Base.AM2_SkullKing"] = {index = -1, spawnChance = 5}
VehicleZoneDistribution.trailerpark.vehicles["Base.AM2_Balrog"] = {index = -1, spawnChance = 5}
VehicleZoneDistribution.trailerpark.vehicles["Base.AM2_Balrog_Sidecar"] = {index = -1, spawnChance = 2.5}
VehicleZoneDistribution.trailerpark.vehicles["Base.AM2_Fireball"] = {index = -1, spawnChance = 10}
VehicleZoneDistribution.trailerpark.vehicles["Base.AM2_Commodore"] = {index = -1, spawnChance = 10}
VehicleZoneDistribution.trailerpark.vehicles["Base.AM2_Warhorse"] = {index = -1, spawnChance = 5}
VehicleZoneDistribution.trailerpark.vehicles["Base.AM2_Warhorse2"] = {index = -1, spawnChance = 2.5}
VehicleZoneDistribution.trailerpark.vehicles["Base.AM2_Warhorse3"] = {index = -1, spawnChance = 2.5}
VehicleZoneDistribution.trailerpark.vehicles["Base.AM2_Camel"] = {index = -1, spawnChance = 10}
VehicleZoneDistribution.trailerpark.vehicles["Base.AM2_Camel2"] = {index = -1, spawnChance = 5}
--VehicleZoneDistribution.trailerpark.vehicles["Base.AM2_Courier"] = {index = -1, spawnChance = 10}
VehicleZoneDistribution.trailerpark.vehicles["Base.AM2_Steelhorse"] = {index = -1, spawnChance = 5}
VehicleZoneDistribution.trailerpark.vehicles["Base.AM2_Steelhorse2"] = {index = -1, spawnChance = 5}
VehicleZoneDistribution.trailerpark.vehicles["Base.AM2_Wendigo"] = {index = -1, spawnChance = 10}
VehicleZoneDistribution.trailerpark.vehicles["Base.AM2_Yeti"] = {index = -1, spawnChance = 10}
VehicleZoneDistribution.trailerpark.vehicles["Base.AM2_Kaiju"] = {index = -1, spawnChance = 10}





-- bad vehicles, moslty used in poor area, sometimes around pub etc.
VehicleZoneDistribution.bad.vehicles["Base.AM2_DirtDemon"] = {index = -1, spawnChance = 0.4}
VehicleZoneDistribution.bad.vehicles["Base.AM2_DirtDemon"] = {index = -1, spawnChance = 0.1}
VehicleZoneDistribution.bad.vehicles["Base.AM2_SkullKing"] = {index = -1, spawnChance = 0.5}
VehicleZoneDistribution.bad.vehicles["Base.AM2_Balrog"] = {index = -1, spawnChance = 0.5}
VehicleZoneDistribution.bad.vehicles["Base.AM2_Balrog_Sidecar"] = {index = -1, spawnChance = 0.25}
VehicleZoneDistribution.bad.vehicles["Base.AM2_Fireball"] = {index = -1, spawnChance = 2.5}
VehicleZoneDistribution.bad.vehicles["Base.AM2_Warhorse"] = {index = -1, spawnChance = 0.25}
VehicleZoneDistribution.bad.vehicles["Base.AM2_Warhorse2"] = {index = -1, spawnChance = 0.125}
VehicleZoneDistribution.bad.vehicles["Base.AM2_Warhorse3"] = {index = -1, spawnChance = 0.125}
VehicleZoneDistribution.bad.vehicles["Base.AM2_Camel"] = {index = -1, spawnChance = 1.25}
VehicleZoneDistribution.bad.vehicles["Base.AM2_Camel2"] = {index = -1, spawnChance = 1.25}
--VehicleZoneDistribution.bad.vehicles["Base.AM2_Courier"] = {index = -1, spawnChance = 2.5}
VehicleZoneDistribution.bad.vehicles["Base.AM2_Steelhorse"] = {index = -1, spawnChance = 0.25}
VehicleZoneDistribution.bad.vehicles["Base.AM2_Steelhorse2"] = {index = -1, spawnChance = 0.25}
-- VehicleZoneDistribution.bad.vehicles["Base.AM2_Balrog_Sidecar"] = {index = -1, spawnChance = 0.25}


-- medium vehicles, used in some of the good looking area, or in suburbs

VehicleZoneDistribution.medium.vehicles["Base.AM2_Vesper"] = {index = -1, spawnChance = 2.5}
VehicleZoneDistribution.medium.vehicles["Base.AM2_Vesper2"] = {index = -1, spawnChance = 5}
VehicleZoneDistribution.medium.vehicles["Base.AM2_Vesper3"] = {index = -1, spawnChance = 0.5}
VehicleZoneDistribution.medium.vehicles["Base.AM2_Commodore"] = {index = -1, spawnChance = 5}
VehicleZoneDistribution.medium.vehicles["Base.AM2_Fireball"] = {index = -1, spawnChance = 2.5}
VehicleZoneDistribution.medium.vehicles["Base.AM2_Camel"] = {index = -1, spawnChance = 2.5}
VehicleZoneDistribution.medium.vehicles["Base.AM2_Camel2"] = {index = -1, spawnChance = 0.5}
VehicleZoneDistribution.medium.vehicles["Base.AM2_Kaiju"] = {index = -1, spawnChance = 2.5}
--VehicleZoneDistribution.medium.vehicles["Base.AM2_Courier"] = {index = -1, spawnChance = 2.5}


-- -- good vehicles, used in good looking area, they're meant to spawn only good cars, so they're on every good looking house.

-- VehicleZoneDistribution.good.vehicles["Base.AM2_Fireball"] = {index = -1, spawnChance = 5}
-- VehicleZoneDistribution.good.vehicles["Base.AM2_Vesper"] = {index = -1, spawnChance = 5}
-- VehicleZoneDistribution.good.vehicles["Base.AM2_Vesper2"] = {index = -1, spawnChance = 5}
-- -- VehicleZoneDistribution.good.vehicles["Base.AM2_Vesper3"] = {index = -1, spawnChance = 5}
-- VehicleZoneDistribution.good.vehicles["Base.AM2_Commodore"] = {index = -1, spawnChance = 5}
-- VehicleZoneDistribution.good.vehicles["Base.AM2_Camel"] = {index = -1, spawnChance = 2.5}
-- VehicleZoneDistribution.good.vehicles["Base.AM2_Camel2"] = {index = -1, spawnChance = 2.5}

-- sports vehicles, sometimes on good looking area.
--VehicleZoneDistribution.sport.vehicles["Base.AM2_77transam"] = {index = -1, spawnChance = 1};
VehicleZoneDistribution.sport.vehicles["Base.AM2_DirtDemon"] = {index = -1, spawnChance = 2.5}
VehicleZoneDistribution.sport.vehicles["Base.AM2_DirtDemon"] = {index = -1, spawnChance = 2.5}
VehicleZoneDistribution.sport.vehicles["Base.AM2_SkullKing"] = {index = -1, spawnChance = 5}
VehicleZoneDistribution.sport.vehicles["Base.AM2_Balrog"] = {index = -1, spawnChance = 5}
VehicleZoneDistribution.sport.vehicles["Base.AM2_Balrog_Sidecar"] = {index = -1, spawnChance = 2.5}
VehicleZoneDistribution.sport.vehicles["Base.AM2_Fireball"] = {index = -1, spawnChance = 10}
VehicleZoneDistribution.sport.vehicles["Base.AM2_Vesper"] = {index = -1, spawnChance = 2.5}
VehicleZoneDistribution.sport.vehicles["Base.AM2_Vesper2"] = {index = -1, spawnChance = 5}
VehicleZoneDistribution.sport.vehicles["Base.AM2_Vesper3"] = {index = -1, spawnChance = 5}
VehicleZoneDistribution.sport.vehicles["Base.AM2_Commodore"] = {index = -1, spawnChance = 5}
VehicleZoneDistribution.sport.vehicles["Base.AM2_Warhorse"] = {index = -1, spawnChance = 2.5}
VehicleZoneDistribution.sport.vehicles["Base.AM2_Warhorse2"] = {index = -1, spawnChance = 1.25}
VehicleZoneDistribution.sport.vehicles["Base.AM2_Warhorse3"] = {index = -1, spawnChance = 1.25}
VehicleZoneDistribution.sport.vehicles["Base.AM2_Camel"] = {index = -1, spawnChance = 5}
VehicleZoneDistribution.sport.vehicles["Base.AM2_Camel2"] = {index = -1, spawnChance = 5}
--VehicleZoneDistribution.sport.vehicles["Base.AM2_Courier"] = {index = -1, spawnChance = 10}
VehicleZoneDistribution.sport.vehicles["Base.AM2_Steelhorse"] = {index = -1, spawnChance = 2.5}
VehicleZoneDistribution.sport.vehicles["Base.AM2_Steelhorse2"] = {index = -1, spawnChance = 2.5}
VehicleZoneDistribution.sport.vehicles["Base.AM2_Kaiju"] = {index = -1, spawnChance = 10}

VehicleZoneDistribution.sport.vehicles["Base.AM2_Wendigo"] = {index = -1, spawnChance = 10}
VehicleZoneDistribution.sport.vehicles["Base.AM2_Yeti"] = {index = -1, spawnChance = 10}


VehicleZoneDistribution.junkyard.vehicles["Base.AM2_DirtDemon"] = {index = -1, spawnChance = 0.75}
VehicleZoneDistribution.junkyard.vehicles["Base.AM2_DirtDemon"] = {index = -1, spawnChance = 0.25}
VehicleZoneDistribution.junkyard.vehicles["Base.AM2_SkullKing"] = {index = -1, spawnChance = 0.5}
VehicleZoneDistribution.junkyard.vehicles["Base.AM2_Balrog"] = {index = -1, spawnChance = 0.5}
VehicleZoneDistribution.junkyard.vehicles["Base.AM2_Balrog_Sidecar"] = {index = -1, spawnChance = 0.25}
VehicleZoneDistribution.junkyard.vehicles["Base.AM2_Fireball"] = {index = -1, spawnChance = 1}
VehicleZoneDistribution.junkyard.vehicles["Base.AM2_Vesper"] = {index = -1, spawnChance = 1}
VehicleZoneDistribution.junkyard.vehicles["Base.AM2_Vesper2"] = {index = -1, spawnChance = 1}
VehicleZoneDistribution.junkyard.vehicles["Base.AM2_Commodore"] = {index = -1, spawnChance = 2}
VehicleZoneDistribution.junkyard.vehicles["Base.AM2_Warhorse"] = {index = -1, spawnChance = 0.5}
VehicleZoneDistribution.junkyard.vehicles["Base.AM2_Warhorse2"] = {index = -1, spawnChance = 0.25}
VehicleZoneDistribution.junkyard.vehicles["Base.AM2_Warhorse3"] = {index = -1, spawnChance = 0.25}
VehicleZoneDistribution.junkyard.vehicles["Base.AM2_Camel"] = {index = -1, spawnChance = 1}
VehicleZoneDistribution.junkyard.vehicles["Base.AM2_Camel2"] = {index = -1, spawnChance = 1}
--VehicleZoneDistribution.junkyard.vehicles["Base.AM2_Courier"] = {index = -1, spawnChance = 2}
VehicleZoneDistribution.junkyard.vehicles["Base.AM2_Steelhorse"] = {index = -1, spawnChance = 0.5}
VehicleZoneDistribution.junkyard.vehicles["Base.AM2_Steelhorse2"] = {index = -1, spawnChance = 0.5}
-- VehicleZoneDistribution.junkyard.vehicles["Base.AM2_Balrog_Sidecar"] = {index = -1, spawnChance = 1}

VehicleZoneDistribution.junkyard.vehicles["Base.AM2_Wendigo"] = {index = -1, spawnChance = 1}
VehicleZoneDistribution.junkyard.vehicles["Base.AM2_Yeti"] = {index = -1, spawnChance = 1}
VehicleZoneDistribution.junkyard.vehicles["Base.AM2_Kaiju"] = {index = -1, spawnChance = 1}


-- traffic jam, mostly burnt car & damaged ones.
-- Used either for hard coded big traffic jam or smaller random ones.
VehicleZoneDistribution.trafficjamw.vehicles["Base.AM2_DirtDemon"] = {index = -1, spawnChance = 0.75}
VehicleZoneDistribution.trafficjamw.vehicles["Base.AM2_DirtDemon"] = {index = -1, spawnChance = 0.25}
VehicleZoneDistribution.trafficjamw.vehicles["Base.AM2_SkullKing"] = {index = -1, spawnChance = 0.25}
VehicleZoneDistribution.trafficjamw.vehicles["Base.AM2_Balrog"] = {index = -1, spawnChance = 0.125}
VehicleZoneDistribution.trafficjamw.vehicles["Base.AM2_Balrog_Sidecar"] = {index = -1, spawnChance = 0.05}
VehicleZoneDistribution.trafficjamw.vehicles["Base.AM2_Fireball"] = {index = -1, spawnChance = 1}
VehicleZoneDistribution.trafficjamw.vehicles["Base.AM2_Warhorse"] = {index = -1, spawnChance = 0.5}
VehicleZoneDistribution.trafficjamw.vehicles["Base.AM2_Warhorse2"] = {index = -1, spawnChance = 0.25}
VehicleZoneDistribution.trafficjamw.vehicles["Base.AM2_Warhorse3"] = {index = -1, spawnChance = 0.25}
VehicleZoneDistribution.trafficjamw.vehicles["Base.AM2_Camel"] = {index = -1, spawnChance = 0.5}
VehicleZoneDistribution.trafficjamw.vehicles["Base.AM2_Camel2"] = {index = -1, spawnChance = 0.5}
--VehicleZoneDistribution.trafficjamw.vehicles["Base.AM2_Courier"] = {index = -1, spawnChance = 1}
VehicleZoneDistribution.trafficjamw.vehicles["Base.AM2_Steelhorse"] = {index = -1, spawnChance = 0.5}
VehicleZoneDistribution.trafficjamw.vehicles["Base.AM2_Steelhorse2"] = {index = -1, spawnChance = 0.5}
VehicleZoneDistribution.trafficjamw.vehicles["Base.AM2_Kaiju"] = {index = -1, spawnChance = 1}
-- VehicleZoneDistribution.trafficjamw.vehicles["Base.AM2_Vesper"] = {index = -1, spawnChance = 0.25}
-- VehicleZoneDistribution.trafficjamw.vehicles["Base.AM2_Vesper2"] = {index = -1, spawnChance = 0.5}
-- VehicleZoneDistribution.trafficjamw.vehicles["Base.AM2_Balrog_Sidecar"] = {index = -1, spawnChance = 0.125}

-- VehicleZoneDistribution.trafficjame.vehicles["Base.AM2_DirtDemon"] = {index = -1, spawnChance = 1}
-- VehicleZoneDistribution.trafficjame.vehicles["Base.AM2_SkullKing"] = {index = -1, spawnChance = 0.25}
-- VehicleZoneDistribution.trafficjame.vehicles["Base.AM2_Balrog"] = {index = -1, spawnChance = 0.125}
-- VehicleZoneDistribution.trafficjame.vehicles["Base.AM2_Fireball"] = {index = -1, spawnChance = 1}
-- -- VehicleZoneDistribution.trafficjame.vehicles["Base.AM2_Balrog_Sidecar"] = {index = -1, spawnChance = 0.125}

-- VehicleZoneDistribution.trafficjamn.vehicles["Base.AM2_DirtDemon"] = {index = -1, spawnChance = 1}
-- VehicleZoneDistribution.trafficjamn.vehicles["Base.AM2_SkullKing"] = {index = -1, spawnChance = 0.25}
-- VehicleZoneDistribution.trafficjamn.vehicles["Base.AM2_Balrog"] = {index = -1, spawnChance = 0.125}
-- VehicleZoneDistribution.trafficjamn.vehicles["Base.AM2_Fireball"] = {index = -1, spawnChance = 1}
-- -- VehicleZoneDistribution.trafficjamn.vehicles["Base.AM2_Balrog_Sidecar"] = {index = -1, spawnChance = 0.125}

-- VehicleZoneDistribution.trafficjams.vehicles["Base.AM2_DirtDemon"] = {index = -1, spawnChance = 1}
-- VehicleZoneDistribution.trafficjams.vehicles["Base.AM2_SkullKing"] = {index = -1, spawnChance = 0.25}
-- VehicleZoneDistribution.trafficjams.vehicles["Base.AM2_Balrog"] = {index = -1, spawnChance = 0.125}
-- VehicleZoneDistribution.trafficjams.vehicles["Base.AM2_Fireball"] = {index = -1, spawnChance = 1}
-- -- VehicleZoneDistribution.trafficjams.vehicles["Base.AM2_Balrog_Sidecar"] = {index = -1, spawnChance = 0.125}

VehicleZoneDistribution.ranger.vehicles["Base.AM2_Wendigo"] = {index = -1, spawnChance = 10}
VehicleZoneDistribution.ranger.vehicles["Base.AM2_Yeti"] = {index = -1, spawnChance = 10}

VehicleZoneDistribution.mccoy.vehicles["Base.AM2_Fireball"] = {index = -1, spawnChance = 25}
VehicleZoneDistribution.mccoy.vehicles["Base.AM2_Camel"] = {index = -1, spawnChance = 12.5}
VehicleZoneDistribution.mccoy.vehicles["Base.AM2_Camel2"] = {index = -1, spawnChance = 12.5}
VehicleZoneDistribution.mccoy.vehicles["Base.AM2_Vesper3"] = {index = -1, spawnChance = 10}
VehicleZoneDistribution.mccoy.vehicles["Base.AM2_Wendigo"] = {index = -1, spawnChance = 10}
VehicleZoneDistribution.mccoy.vehicles["Base.AM2_Yeti"] = {index = -1, spawnChance = 10}

VehicleZoneDistribution.police.vehicles["Base.AM2_Warhorse2"] = {index = 0, spawnChance = 5}
VehicleZoneDistribution.police.vehicles["Base.AM2_Warhorse3"] = {index = 0, spawnChance = 5}



VehicleZoneDistribution.farm = VehicleZoneDistribution.farm or {}
VehicleZoneDistribution.farm.vehicles = VehicleZoneDistribution.farm.vehicles or {}
VehicleZoneDistribution.farm.vehicles["Base.AM2_DirtDemon"] = {index = -1, spawnChance = 5}
VehicleZoneDistribution.farm.vehicles["Base.AM2_DirtDemon"] = {index = -1, spawnChance = 1}
VehicleZoneDistribution.farm.vehicles["Base.AM2_Fireball"] = {index = -1, spawnChance = 5}
VehicleZoneDistribution.farm.vehicles["Base.AM2_Vesper"] = {index = -1, spawnChance = 1}
VehicleZoneDistribution.farm.vehicles["Base.AM2_Vesper2"] = {index = -1, spawnChance = 2}
VehicleZoneDistribution.farm.vehicles["Base.AM2_Warhorse"] = {index = -1, spawnChance = 1}
VehicleZoneDistribution.farm.vehicles["Base.AM2_Warhorse2"] = {index = -1, spawnChance = 0.5}
VehicleZoneDistribution.farm.vehicles["Base.AM2_Warhorse3"] = {index = -1, spawnChance = 0.5}
VehicleZoneDistribution.farm.vehicles["Base.AM2_Commodore"] = {index = -1, spawnChance = 5}
VehicleZoneDistribution.farm.vehicles["Base.AM2_Camel"] = {index = -1, spawnChance = 3}
VehicleZoneDistribution.farm.vehicles["Base.AM2_Camel2"] = {index = -1, spawnChance = 2}
VehicleZoneDistribution.farm.vehicles["Base.AM2_Vesper_3"] = {index = -1, spawnChance = 5}
VehicleZoneDistribution.farm.vehicles["Base.AM2_Steelhorse"] = {index = -1, spawnChance = 1}
VehicleZoneDistribution.farm.vehicles["Base.AM2_Steelhorse2"] = {index = -1, spawnChance = 1}
VehicleZoneDistribution.farm.vehicles["Base.AM2_Wendigo"] = {index = -1, spawnChance = 5}
VehicleZoneDistribution.farm.vehicles["Base.AM2_Yeti"] = {index = -1, spawnChance = 5}
VehicleZoneDistribution.farm.vehicles["Base.AM2_Kaiju"] = {index = -1, spawnChance = 1}
VehicleZoneDistribution.farm.baseVehicleQuality = 0.8;
VehicleZoneDistribution.farm.chanceToPartDamage = 20;
VehicleZoneDistribution.farm.chanceToSpawnSpecial = 0;
VehicleZoneDistribution.farm.spawnRate = 25;


VehicleZoneDistribution.military = VehicleZoneDistribution.military or {}
VehicleZoneDistribution.military.vehicles = VehicleZoneDistribution.military.vehicles or {}
VehicleZoneDistribution.military.vehicles["Base.AM2_Warhorse2"] = {index = -1, spawnChance = 10}
VehicleZoneDistribution.military.vehicles["Base.AM2_Warhorse3"] = {index = -1, spawnChance = 10}
VehicleZoneDistribution.military.vehicles["Base.AM2_Vesper3"] = {index = -1, spawnChance = 10}
VehicleZoneDistribution.military.vehicles["Base.AM2_Steelhorse2"] = {index = -1, spawnChance = 5}
VehicleZoneDistribution.military.baseVehicleQuality = 1;
VehicleZoneDistribution.military.chanceToSpawnSpecial = 0;
VehicleZoneDistribution.military.spawnRate = 25;

--end