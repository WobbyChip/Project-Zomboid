function onCharacterDeath(character)
    if not instanceof(character, "IsoPlayer") then return end
    local isMe = (character:getOnlineID() == getPlayer():getOnlineID());

    --This is for MP, to clear other player corpse on client side
    --Since clients and server are not synced
    if isClient() and not isMe and SandboxVars.keepInventory then
        clearInventory(character);
    end

    --This will be only true in MP
    if isClient() then return end

    if SandboxVars.keepInventory then
        saveEquipItems(character);
    end

    savePlayer(character);

    if SandboxVars.keepInventory then
        clearInventory(character);
    end
end

Events.OnCharacterDeath.Add(onCharacterDeath);