function onCharacterDeath(character)
    if not isServer() then return end

    --Clear corpse on server side and tell client to do same
    --Since onCharacterDeath event for clients are delayed
    if instanceof(character, "IsoPlayer") then
        sendServerCommand(character, "Respawn", "onCharacterDeath", nil);
        clearInventory(character);
    end
end

Events.OnCharacterDeath.Add(onCharacterDeath);