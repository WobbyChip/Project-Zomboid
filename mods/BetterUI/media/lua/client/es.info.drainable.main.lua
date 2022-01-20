local esInfoOptions = require("es.info.options");
local esCommon = require("esq.common.01");

local baseISInventoryPane = ISInventoryPane.drawItemDetails;
function ISInventoryPane:drawItemDetails(item, y, xoff, yoff, red)

    if (esInfoOptions.getOption("drainOn") and item ~= nil and instanceof(item, "Drainable")) then
        local maxUses = esCommon.numbers.round(1 / item:getUseDelta());
        local usesLeft = item:getDrainableUsesInt();

        local hdrHgt = self.headerHgt
        local top = hdrHgt + y * self.itemHgt + yoff
        local fgBar = {r=0.0, g=0.6, b=0.0, a=0.7}
        local fgText = {r=0.6, g=0.8, b=0.5, a=0.6}
        local text = getText("IGUI_invpanel_Remaining") .. ": "..usesLeft.."/"..maxUses;
        self:drawTextAndProgressBar(text, item:getUsedDelta(), xoff, top, fgText, fgBar)
    else
        baseISInventoryPane(self,item, y, xoff, yoff, red);
    end

end
