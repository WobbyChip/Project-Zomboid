function onCharacterDeath(character)
    if not instanceof(character, "IsoPlayer") then return end
    local isMe = (character:getOnlineID() == getPlayer():getOnlineID());
    if not isClient() or isMe then return end

    --Fix corpse duplication glitch on MP
    character:setOnDeathDone(true);

    --This is for MP, to clear other player corpse on client side
    --Since clients and server are not synced
     if SandboxVars.Respawn.keepInventory then
        clearInventory(character);
    end
end

Events.OnCharacterDeath.Add(onCharacterDeath);