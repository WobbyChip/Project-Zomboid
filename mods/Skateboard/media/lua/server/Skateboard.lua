if not isServer() then return end -- Prevent load the file on SP
local Functions = require "Skateboard/Functions"

local onTick = function(tick)
	local players = getOnlinePlayers();

	for i = 0, players:size() - 1 do
		local player = players:get(i);

		if player then
			local vehicle = player:getVehicle();

			if vehicle and vehicle:getScriptName() == "Base.Skateboard" then
				local frontDurability = Functions.currentFrontEndDurability(vehicle)
				
				if vehicle:getModData().currentFrontEndDurability ~= frontDurability then
					vehicle:getModData().currentFrontEndDurability = frontDurability

                    sendServerCommand(player, "Skateboard", "BumpVehicle", {
                        vehicleId = vehicle:getId()
                    })
				end
			end
		end
	end
end

Events.OnTick.Add(onTick)