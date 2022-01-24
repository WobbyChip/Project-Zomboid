function onServerCommand(module, command, packet, args)
    if module ~= "Respawn" then return end

    if command == "onCharacterDeath" then
        if SandboxVars.keepInventory then
            saveEquipItems(getPlayer());
        end

        savePlayer(getPlayer());

        if SandboxVars.keepInventory then
            clearInventory(getPlayer());
        end
    end
end

Events.OnServerCommand.Add(onServerCommand);
