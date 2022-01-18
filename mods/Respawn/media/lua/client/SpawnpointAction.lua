SpawnpointAction = ISBaseTimedAction:derive("SpawnpointAction")

function SpawnpointAction:isValid()
	return true
end

function SpawnpointAction:stop()
	ISBaseTimedAction.stop(self);
end

function SpawnpointAction:perform()
    if self.addRemove then
        setPlayerRespawn(self.character);
        self.character:Say("Respawn Point Set");
    else
        removePlayerRespawn(self.character);
        self.character:Say("Respawn Point Removed");
    end

	ISBaseTimedAction.perform(self);
end

function SpawnpointAction:new(character, object, addRemove, time)	
	local o = {};
	setmetatable(o, self);
	self.__index = self;
	o.character = character;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = time;
	o.object = object;
    o.addRemove = addRemove;
	return o
end