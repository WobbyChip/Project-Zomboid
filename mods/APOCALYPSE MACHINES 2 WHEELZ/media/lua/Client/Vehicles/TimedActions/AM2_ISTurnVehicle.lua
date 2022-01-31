--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

AM2_ISTurnVehicle = ISBaseTimedAction:derive("AM2_ISTurnVehicle");

function AM2_ISTurnVehicle:isValid()
	--print("is valid")
	return true
end

function AM2_ISTurnVehicle:update()
	--print("update")
	-- self.character:setIgnoreMovementForDirection(false);
	self.character:faceThisObject(self.vehicle);
	--self.character:setIgnoreMovementForDirection(true);
	local roll = 19
	if ZombRand(roll) == 0 then
		self.vehicle:getEmitter():playSound(self.sound1)
		addSound(self.character, self.character:getX(), self.character:getY(), self.character:getZ(), 20, 10)
	end	
end

function AM2_ISTurnVehicle:start()

		self:setActionAnim("Loot");
		self.character:SetVariable("LootPosition", "Low");
		  -- self:setActionAnim("RemoveBarricade")      
          -- self:setAnimVariable("RemoveBarricade", "CrowbarMid")
   	
	self.vehicle:getEmitter():playSound(self.sound1)
    addSound(self.character, self.character:getX(), self.character:getY(), self.character:getZ(), 20, 10)

end

function AM2_ISTurnVehicle:stop()
	--print("stop")
	self.character:PlayAnim("Idle");
	self.character:setIgnoreMovementForDirection(false);
    ISBaseTimedAction.stop(self);
end

function AM2_ISTurnVehicle:perform()
	--print("perform")
    self.vehicle:getEmitter():playSound(self.sound2)
    addSound(self.character, self.character:getX(), self.character:getY(), self.character:getZ(), 20, 10)

	self.vehicle:flipUpright()
    -- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function AM2_ISTurnVehicle:new(player, vehicle)
	--print("new")
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = player;
	o.vehicle = vehicle;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	--o.mass = vehicle:getMass()
	--o.maxTime = 150 - (player:getPerkLevel(Perks.Strength) * 10)
	o.maxTime = vehicle:getMass() - (player:getPerkLevel(Perks.Strength) * 10)
	o.spriteFrame = 0
	o.sound1 = "ZombieThumpVehicle"
	o.sound2 = "BreakBarricadeMetal"
	return o;
end
