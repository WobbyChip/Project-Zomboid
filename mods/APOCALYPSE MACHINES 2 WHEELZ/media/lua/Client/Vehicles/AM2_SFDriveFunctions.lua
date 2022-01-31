--AM2_SFDriveFunctions = {}

function AM2_SFDriveFunctions_turnBike(player, vehicle)
	local square = vehicle:getSquare()
	if isAdmin() then
		--vehicle:flipUpright()
	else
		--print("Test 3")
 		if luautils.walkAdj(player, square) then
			ISTimedActionQueue.add(AM2_ISTurnVehicle:new(player, vehicle))
		end
	end
end


function AM2_PullDevice(player, vehicle)
	local square = vehicle:getSquare()
	if isAdmin() then
		--vehicle:flipUpright()
	else
		--print("Test 3")
 		if luautils.walkAdj(player, square) then
			ISTimedActionQueue.add(AM2_ISPullDevice:new(player, vehicle))
		end
	end
end