require "TimedActions/ISBaseTimedAction"

local TimeAction = ISBaseTimedAction:derive("TimeAction");

TimeAction.isValid = function(self)
    return true
end

TimeAction.update = function(self)

end

TimeAction.start = function(self)
    self:setActionAnim("Loot");
    self:setAnimVariable("LootPosition", "Low");
end

TimeAction.stop = function(self)
    ISBaseTimedAction.stop(self);
end

TimeAction.perform = function(self)
    if self.typeTimeAction == "grab" then
        local inventory = self.character:getInventory()
        local item = inventory:AddItem("Dislaik.Skateboard")

        item:setCondition(self.vehicle:getModData().skateboardCondition)
        
        if isClient() then
            sendClientCommand(self.character, "Skateboard", "RemoveVehicle", {
                vehicleId = self.vehicle:getId()
            })
        else
            self.vehicle:permanentlyRemove()
        end
        
    elseif self.typeTimeAction == "drop" then
        local square = getSquare(self.character:getX(), self.character:getY(), self.character:getZ())
        local inventory = self.character:getInventory()

        if self.character:isInARoom() then
            self.character:Say(getText("IGUI_PlayerText_NoSkateOnHouse"))
        else
            if isClient() then
                sendClientCommand(self.character, "Skateboard", "DropVehicle", {
                    itemCondition = self.item:getCondition()
                })
            else
                local vehicle = addVehicleDebug("Base.Skateboard", self.character:getDir(), nil, square);

                vehicle:getModData().skateboardCondition = self.item:getCondition()
            end

            inventory:Remove(self.item);
        end

    end

    ISBaseTimedAction.perform(self);
end

TimeAction.drop = function(self, character, item, time)
    local o = ISBaseTimedAction.new(self, character);
    o.typeTimeAction = "drop";
    o.item = item;
    o.stopOnWalk = true;
    o.stopOnRun = true;
    o.maxTime = time;
    o.fromHotbar = false;
    if o.character:isTimedActionInstant() then o.maxTime = 1; end
    return o;
end

TimeAction.grab = function(self, character, vehicle, time)
    local o = ISBaseTimedAction.new(self, character);
    o.typeTimeAction = "grab";
    o.vehicle = vehicle;
    o.stopOnWalk = true;
    o.stopOnRun = true;
    o.maxTime = time;
    o.fromHotbar = false;
    if o.character:isTimedActionInstant() then o.maxTime = 1; end
    return o;
end

return TimeAction