if not isServer() then return end -- Prevent load the file on SP

local Functions = require "Skateboard/Functions"
local Commands = {};
Commands.Skateboard = {};

Commands.Skateboard.DropVehicle = function(player, args)
	local playerSquare = getSquare(player:getX(), player:getY(), player:getZ())
	local playerDir = player:getDir()
	local vehicle = addVehicleDebug("Base.Skateboard", playerDir, nil, playerSquare);

	vehicle:getModData().skateboardCondition = args.itemCondition
end

Commands.Skateboard.RemoveVehicle = function(player, args)
	local vehicle = getVehicleById(args.vehicleId)

	vehicle:permanentlyRemove()
end

Commands.Skateboard.SetFrontDurability = function(player, args)
	local vehicle = getVehicleById(args.vehicleId)
	local frontDurability = Functions.currentFrontEndDurability(vehicle)

	vehicle:getModData().currentFrontEndDurability = frontDurability
end

local onClientCommand = function(module, command, player, args)
	if Commands[module] and Commands[module][command] then
		Commands[module][command](player, args)
	end
end

Events.OnClientCommand.Add(onClientCommand)
