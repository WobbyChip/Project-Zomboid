playerNum = -1;

function onPlayerDeath(player)
    playerNum = player:getPlayerNum();

    if ISPostDeathUI.instance[playerNum] then
        ISPostDeathUI.instance[playerNum].buttonQuit:setTitle("RESPAWN");
        ISPostDeathUI.instance[playerNum].buttonQuit:setOnClick(onRespawn);
    end
end

function onRespawn(target)
    if MainScreen.instance:isReallyVisible() then return end
    target:setVisible(false);

    setPlayerMouse(nil); --This spawns new player
    loadPlayer(getPlayer());

    if ISPostDeathUI.instance[playerNum] then
        ISPostDeathUI.instance[playerNum]:removeFromUIManager();
        ISPostDeathUI.instance[playerNum] = nil;
    end
end

Events.OnPlayerDeath.Add(onPlayerDeath);