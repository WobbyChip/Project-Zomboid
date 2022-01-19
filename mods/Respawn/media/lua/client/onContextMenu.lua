function onFillWorldObjectContextMenu(playerId, context, worldobjects, test)
	for _, object in ipairs(worldobjects) do
        if SandboxVars.allowSpawnpoint and instanceof(object, "IsoTelevision") then
            context:addOption("Set Respawn", object, onSetRespawn, playerId, true);
            context:addOption("Remove Respawn", object, onSetRespawn, playerId, false);
        end;
	end
end

function onSetRespawn(object, playerId, addRemove)
    local player = getSpecificPlayer(playerId);

    if not object:getSquare() or not luautils.walkAdj(player, object:getSquare()) then
		return
	end

    ISTimedActionQueue.add(SpawnpointAction:new(player, addRemove, SandboxVars.spawnpointTimer));
end

Events.OnFillWorldObjectContextMenu.Add(onFillWorldObjectContextMenu)