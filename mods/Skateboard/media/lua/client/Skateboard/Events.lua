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
    
	source:SetVariable("VehicleScriptName", vehicle:getScriptName())
end

Commands.Skateboard.PlayerEnter = function(args)
	local players = getOnlinePlayers();
	local vehicle = getVehicleById(args.vehicleId)

	for i = 0, players:size() - 1 do
		local player = players:get(i);

		if player:getOnlineID() == args.playerId then
			player:SetVariable("VehicleScriptName", vehicle:getScriptName());

			break
		end
	end
end

Commands.Skateboard.PlayerExit = function(args)
	local players = getOnlinePlayers();

	for i = 0, players:size() - 1 do
		local player = players:get(i);

		if player:getOnlineID() == args.playerId then
			player:SetVariable("VehicleScriptName", "");
			player:SetVariable("VehicleForward", "false")

			break
		end
	end
end

Commands.Skateboard.VehicleForward = function(args)
	local players = getOnlinePlayers();

	for i = 0, players:size() - 1 do
		local player = players:get(i);

		if player:getOnlineID() == args.playerId then
			player:SetVariable("VehicleForward", tostring(args.forward))

			break
		end
	end
end

local onServerCommand = function(module, command, args)
	if Commands[module] and Commands[module][command] then
		Commands[module][command](args)
	end
end

Events.OnServerCommand.Add(onServerCommand)
