function onCharacterDeath(character)
    if not isServer() then return end

    if instanceof(character, "IsoPlayer") then
        local package = {};
        package.playerId = character:getPlayerNum();

        sendServerCommand(character, "Respawn", "onCharacterDeath", package);
        clearInventory(character);
    end
end

Events.OnCharacterDeath.Add(onCharacterDeath);