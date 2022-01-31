require "Vehicle/Vehicles"

function Vehicles.ContainerAccess.AMI_Seat(vehicle, part, chr)
	if not part:getInventoryItem() then return false; end
	local seat = part:getContainerSeatNumber()
	-- Can't put stuff on an occupied seat.
	if seat ~= -1 and vehicle:getCharacter(seat) then
		return false
	end
	if chr:getVehicle() == vehicle then
		-- Can the seated player reach the other seat?
		return vehicle:canSwitchSeat(vehicle:getSeat(chr), seat) and
				not vehicle:getCharacter(seat)
	elseif chr:getVehicle() then
		-- Can't reach seat from inside a different vehicle.
		return false
	else
		if not vehicle:isInArea(part:getArea(), chr) then return false end
		-- local doorPart = vehicle:getPassengerDoor(seat)
		-- if doorPart and doorPart:getDoor() and not doorPart:getDoor():isOpen() then
			-- return false
		-- end
		-- -- Rear seats in a 2-door vehicle for example.
		-- if not doorPart and vehicle:getPartById("DoorFrontLeft") then return false end
		-- -- Door is uninstalled/open, or an exterior seat (motorcycle?)
		return true
	end
end


function Vehicles.ContainerAccess.AMI_SeatNope(vehicle, part, chr)
	return false
end