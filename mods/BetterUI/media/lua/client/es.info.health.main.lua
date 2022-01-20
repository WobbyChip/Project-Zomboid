local esInfoOptions = require("es.info.options");
local esHealthInfo = {};

local baseISScrollingListBox_updateTooltip = ISHealthBodyPartListBox.updateTooltip;
function ISHealthBodyPartListBox:updateTooltip()
    if (esInfoOptions.getOption("healthOn")) then
        for b = 1, #self.items do
            if (self.items[b].item.bodyPart ~= nil) then
                self.items[b].tooltip = esHealthInfo.createTooltip(self.items[b].item.bodyPart);
            end
        end
    end

    baseISScrollingListBox_updateTooltip(self)
end

function esHealthInfo.createTooltip(item)
    local text = getText("IGUI_health_Wounded").. " <RGB:1,0,0> ";
    local line = " <LINE> ";
    local wounded = false;
    local doctorLevel = getPlayer():getPerkLevel(Perks.Doctor);

    if (item:getBleedingTime() > 0) then
        text = text .. line .. getText("IGUI_health_Bleeding");
        wounded = true;
    end
    if (item:getBiteTime() > 0) then
        text = text .. line .. getText("IGUI_health_Bitten");
        wounded = true;
    end
    if (item:getScratchTime() > 0) then
        text = text .. line .. getText("IGUI_health_Scratched");
        wounded = true;
    end
    if (item:getCutTime() > 0) then
        text = text .. line .. getText("IGUI_health_Cut");
        wounded = true;
    end
    if (item:getDeepWoundTime() > 0) then
        text = text .. line .. getText("IGUI_health_DeepWound");
        wounded = true;
    end
    if (item:getFractureTime() > 0) then
        text = text .. line .. getText("IGUI_health_Fracture");
        wounded = true;
    end
    if (item:isInfectedWound()) then
        if doctorLevel > 8 or (item:getWoundInfectionLevel() * 10 >= (2.5 - doctorLevel)) then
            text = text .. line .. getText("IGUI_health_Infected");
            wounded = true;
        end
    end
    if (item:getBurnTime() > 0) then
        text = text .. line .. getText("IGUI_health_Burned");
        wounded = true;
    end
    if (item:getStitchTime() > 0) then
        text = text .. line .. getText("IGUI_health_Stitched");
        wounded = true;
    end
    if (item:haveBullet()) then
        text = text .. line .. getText("IGUI_health_LodgedBullet");
        wounded = true;
    end
    if (item:haveGlass()) then
        text = text .. line .. getText("IGUI_health_LodgedGlassShards");
        wounded = true;
    end

    if (wounded) then
        return text;
    else
        return nil;
    end

end