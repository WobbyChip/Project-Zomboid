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

Commands.Skateboard.PlayerEnter = function(source, args)
	local players = getOnlinePlayers();
	local sourceId = source:getOnlineID();
	local vehicle = getVehicleById(args.vehicleId)
	local frontDurability = Functions.currentFrontEndDurability(vehicle)

	vehicle:getModData().currentFrontEndDurability = frontDurability

	for i = 0, players:size() - 1 do
		local player = players:get(i);

		if player ~= source then
			sendServerCommand(player, "Skateboard", "PlayerEnter", {
				playerId = sourceId,
				vehicleId = args.vehicleId
			})
		end
	end
end

Commands.Skateboard.PlayerExit = function(source, args)
	local players = getOnlinePlayers();
	local sourceId = source:getOnlineID();

	for i = 0, players:size() - 1 do
		local player = players:get(i);

		if player ~= source then
			sendServerCommand(player, "Skateboard", "PlayerExit", {
				playerId = sourceId
			})
		end
	end
end

Commands.Skateboard.VehicleForward = function(source, args)
	local players = getOnlinePlayers();
	local sourceId = source:getOnlineID();

	for i = 0, players:size() - 1 do
		local player = players:get(i);

		if player ~= source then
			sendServerCommand(player, "Skateboard", "VehicleForward", {
				playerId = sourceId,
				forward = args.forward
			})
		end
	end
end

local onClientCommand = function(module, command, player, args)
	if Commands[module] and Commands[module][command] then
		Commands[module][command](player, args)
	end
end

Events.OnClientCommand.Add(onClientCommand)
