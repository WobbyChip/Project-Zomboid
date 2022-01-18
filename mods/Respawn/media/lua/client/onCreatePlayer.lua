function onCreatePlayer(id, player)
    if player:getHoursSurvived() == 0 then
        setPlayerRespawn(player);
    end
end

Events.OnCreatePlayer.Add(onCreatePlayer);