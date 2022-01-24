if not isClient() then return end -- Prevent load the file on SP
local Functions = require "Skateboard/Functions"
local Commands = {};
Commands.Skateboard = {};

Commands.Skateboard.BumpVehicle = function(args)
    local source = getPlayer();
    local vehicle = getVehicleById(args.vehicleId)
    
	Functions.bumpVehicle(source, vehicle, "SkateboardRide")
end

Commands.Skateboard.SetPlayerVehicleScriptName = function(args)
    local source = getPlayer();
    local vehicle = getVehicleById(args.vehicleId)
    
	source:SetVariable("vehicleScriptName", vehicle:getScriptName())
end

local onServerCommand = function(module, command, args)
	if Commands[module] and Commands[module][command] then
		Commands[module][command](args)
	end
end

Events.OnServerCommand.Add(onServerCommand)
