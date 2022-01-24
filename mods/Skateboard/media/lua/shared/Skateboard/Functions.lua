local self = {};

self.isInsideVehicle = function(source)
    local vehicle = source:getVehicle()

    if vehicle then
        return true
    end

    return false
end

self.bump = function(source)
    local random = ZombRand(2);
    local bumptype = nil

    if random == 0 then
        bumptype = "left"
    else
        bumptype = "right"
    end

    source:SetVariable("bumptype", bumptype)
    source:SetVariable("bumpfall", "true")
end

self.startSoundRide = function(source, vehicle, soundName, volume)
    local sound = vehicle:getEmitter():playSound(soundName)
    
    vehicle:getEmitter():set3D(sound, true)
    vehicle:getEmitter():setVolume(sound, volume)
end

self.currentFrontEndDurability = function(vehicle)
    local field = getClassField(vehicle, 62)

    return getClassFieldVal(vehicle, field)
end

self.bumpVehicle = function(source, vehicle, soundName)
    vehicle:exit(source);

    if vehicle:getEmitter():isPlaying(soundName) then
        vehicle:getEmitter():stopSoundByName(soundName);
    end

    vehicle:setHotwired(false);
    vehicle:repair()
    source:SetVariable("vehicleScriptName", "")
    source:SetVariable("vehicleForward", "false")
    self.bump(source)
end

return self