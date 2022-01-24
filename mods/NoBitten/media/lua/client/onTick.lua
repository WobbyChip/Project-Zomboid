NoBittenTime = os.time() + SandboxVars.bittenUpdateInterval;

function onTick()
    local seconds = os.difftime(os.time(), NoBittenTime);
    if seconds < SandboxVars.bittenUpdateInterval then return end

    NoBittenTime = os.time() + SandboxVars.bittenUpdateInterval;
    local player = getPlayer();
    if player == nil then return end
    removeBitten(player, 0);
end

Events.OnTick.Add(onTick);