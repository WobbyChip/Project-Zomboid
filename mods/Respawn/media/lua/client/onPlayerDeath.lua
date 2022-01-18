function onPlayerDeath(player)
    savePlayer(player);

    if SandboxVars.keepInventory then
        clearInventory(player);
    end
end

Events.OnPlayerDeath.Add(onPlayerDeath);