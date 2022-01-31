--***********************************************************
--**                    THE INDIE STONE                    **
require "Vehicle/Vehicles"

local old_VehicleUtils_createPartInventoryItem =  VehicleUtils.createPartInventoryItem

function VehicleUtils.createPartInventoryItem(part)
	local vehicle = part:getVehicle()
	local sName =  vehicle:getScriptName()
	local iType = nil 
	if part:getItemType() and part:getItemType():get(0) then
		iType = part:getItemType():get(0)
	end
	if iType and iType:contains("Base.nil")  then
		--print("FALSE!")
		return false
	end
	if sName:contains("Burnt") then	
		local iType = nil 
		if part:getItemType() and part:getItemType():get(0) then
			iType = part:getItemType():get(0)
		end
		if iType and iType:contains("Gas")  and ZombRand(0,2)==0 then
			return false
		end
		if iType and iType:contains("Brake")  and ZombRand(0,2)==0 then
			return false
		end
		if iType and iType:contains("Suspension")  and ZombRand(0,2)==0 then
			return false
		end
		if iType and iType:contains("Muffler")  and ZombRand(0,2)==0 then
			return false
		end
		if iType and iType:contains("Radio")  and ZombRand(0,2)==0 then
			return false
		end
		if iType and iType:contains("Tire")  then
			return false
		end
		if iType and iType:contains("Light")  then
			return false
		end
		if iType and iType:contains("Wind") then
			--return false
		end
		if iType and iType:contains("Base.nil") then
			--print("FALSE!")
			--return false
		end	
	end
	local part2 = old_VehicleUtils_createPartInventoryItem(part)		
	--if sName:contains("Burnt") and part:getCondition() > 50 then part:setCondition(ZombRand(1,50)) end
	return part2
end



function Vehicles.Create.Battery(vehicle, part)

	local sName =  vehicle:getScriptName()
	if sName:contains("Burnt") and ZombRand(0,2) == 0 then
		return false
	end

	local item = VehicleUtils.createPartInventoryItem(part);
	if SandboxVars.VehicleEasyUse then
		item:setUsedDelta(1);
		return;
	end
	if vehicle:isGoodCar() then
		item:setUsedDelta(ZombRandFloat(0.8,1));
		return;
	end
	local tot = (getGameTime():getWorldAgeHours() / 5000);
	tot = tot + (((getSandboxOptions():getTimeSinceApo() - 1) * 30 * 24) / 4500);
	tot = ZombRandFloat(tot - 0.15, tot + 0.15);
	tot = 1 - tot;
	tot = math.min(tot, 1)
	item:setUsedDelta(math.max(0, tot));
	--local sName =  vehicle:getScriptName()
	if sName:contains("Burnt") then
		local roll = ZombRand(25)/100
		if item:getUsedDelta() > roll then
			item:setUsedDelta(roll)
		end
	end
	
end

local old_Vehicles_Create_Engine = Vehicles.Create.Engine

function Vehicles.Create.Engine(vehicle, part)
	old_Vehicles_Create_Engine(vehicle, part)
	
	local sName =  vehicle:getScriptName()
	if sName:contains("Burnt") then

		local roll = ZombRand(25)
		if part:getCondition() > roll then
			part:setCondition(roll)
		end
	end
end
