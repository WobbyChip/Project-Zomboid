function onCreatePlayer(id, player)
    if player:getHoursSurvived() == 0 then
        setPlayerRespawn(player);
    end

    Respawn.bDead = false;
end

Events.OnCreatePlayer.Add(onCreatePlayer);