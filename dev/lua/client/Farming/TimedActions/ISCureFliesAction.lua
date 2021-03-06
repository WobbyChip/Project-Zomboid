--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISCureFliesAction = ISBaseTimedAction:derive("ISCureFliesAction");

function ISCureFliesAction:isValid()
	self.plant:updateFromIsoObject()
	return self.plant:getIsoObject() ~= nil
end

function ISCureFliesAction:waitToStart()
	self.character:faceThisObject(self.plant:getObject())
	return self.character:shouldBeTurning()
end

function ISCureFliesAction:update()
	self.character:faceThisObject(self.plant:getObject())
end

function ISCureFliesAction:start()
	self:setActionAnim(CharacterActionAnims.Pour)
	self:setOverrideHandModels(self.item, nil)
end

function ISCureFliesAction:stop()
    ISBaseTimedAction.stop(self);
end

function ISCureFliesAction:perform()
	-- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);

	local sq = self.plant:getSquare()
	local args = { x = sq:getX(), y = sq:getY(), z = sq:getZ(), uses = self.uses }
	CFarmingSystem.instance:sendCommand(self.character, 'cureFlies', args)

	-- Hack until server manages player inventory
	local level = self.plant.fliesLvl
	for i=1,self.uses do
		if level < 100 then
			if self.item then
				self.item:Use()
			else
				return;
			end
			level = level - 5
		end
	end
end

function ISCureFliesAction:new(character, item, uses, plant, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.item = item;
	o.uses = uses;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = time;
	if character:isTimedActionInstant() then
		o.maxTime = 1;
	end
    o.plant = plant;
	return o;
end
