function onPlayerUpdate(player)
    if player:getHealth() > 0 and player:getBodyDamage():getOverallBodyHealth() > 0 then return end
    if Respawn.bDead  then return end

    local isMe = (player:getOnlineID() == getPlayer():getOnlineID());
    if not isMe then return end
    Respawn.bDead = true;
    
    if SandboxVars.Respawn.keepInventory then
        saveEquipItems(player);
    end

    savePlayer(player);

    if SandboxVars.Respawn.keepInventory then
        clearInventory(player);
    end
end

Events.OnPlayerUpdate.Add(onPlayerUpdate);