local self = {};

self.isInsideVehicle = function(source)
    local vehicle = source:getVehicle()

    if vehicle then
        return true
    end

    return false
end

self.bump = function(source, dumbType)
    local type = nil;

    source:setBumpFallType("");

    if dumbType == "Random" then
        local random = ZombRand(2);

        if random == 0 then
            dumbType = "FallBackwards";
        else
            dumbType = "FallForward";
        end
    end

    if dumbType == "FallBackwards" then
        type = "stagger";
        source:setBumpFallType("pushedFront");
    elseif dumbType == "FallForward" then
        local random = ZombRand(2);

        if random == 0 then
            type = "left"
        else
            type = "right"
        end
    end

    source:setBumpType(type);
	source:setBumpDone(false);
    source:setBumpFall(true);
	source:reportEvent("wasBumped");
end

self.startSoundRide = function(vehicle, soundName, volume)
    if vehicle:getCurrentSpeedKmHour() > 3 then
        local sound = vehicle:getEmitter():playSoundImpl(soundName, IsoObject.new());
        
        --vehicle:getEmitter():set3D(sound, true)
        vehicle:getEmitter():setVolume(sound, volume);
    end
end

self.endSoundRide = function(vehicle, soundName)

    if vehicle:getEmitter():isPlaying(soundName) then
        vehicle:getEmitter():stopSoundByName(soundName);
    end

end

self.currentFrontEndDurability = function(vehicle)
    local field = getClassField(vehicle, 62)

    return getClassFieldVal(vehicle, field)
end

self.bumpVehicle = function(source, dumbType, soundName)
    local vehicle = source:getVehicle();

    vehicle:exit(source);
    vehicle:setHotwired(false);
    vehicle:repair()

    self.endSoundRide(vehicle, soundName)

    source:SetVariable("VehicleScriptName", "")
    source:SetVariable("VehicleForward", "false")
    self.bump(source, dumbType)
end

return self