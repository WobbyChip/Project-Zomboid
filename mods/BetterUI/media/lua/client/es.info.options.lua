esQolModOptions = esQolModOptions or {};
esQolModOptions.options = esQolModOptions.options or {};

local esQoLOptions = require("es.qoloptions.main");
local infoOptions = {};
local modOptionsInfo = {};

infoOptions.configOptions = {
    options_data = {
        esQoLHealthInfo = {
            getText("IGUI_ESQ_COMMON_UI_OFF"),
            getText("IGUI_ESQ_COMMON_UI_ON"),

            name = getText("IGUI_mo_esqHealthInfo"),
            tooltip = getText("IGUI_mo_esqHealthInfo_TT"),
            default = 2,
        },
        esQoLDrainableInfo = {
            getText("IGUI_ESQ_COMMON_UI_OFF"),
            getText("IGUI_ESQ_COMMON_UI_ON"),

            name = getText("IGUI_mo_esqDrainableInfo"),
            tooltip = getText("IGUI_mo_esqDrainableInfo_TT"),
            default = 2,
        },
        esQoLClothingInfo = {
            getText("IGUI_ESQ_COMMON_UI_OFF"),
            getText("IGUI_ESQ_COMMON_UI_ON") .." "..getText("IGUI_mo_esqInfo_TT_E"),
            getText("IGUI_ESQ_COMMON_UI_ON") .." "..getText("IGUI_mo_esqInfo_TT_S"),
            getText("IGUI_ESQ_COMMON_UI_ON") .." "..getText("IGUI_mo_esqInfo_TT_O"),

            name = getText("IGUI_mo_esqClothingInfo"),
            tooltip = getText("IGUI_mo_esqClothingInfo_TT"),
            default = 4,
        },
        esQoLBookInfo = {
            getText("IGUI_ESQ_COMMON_UI_OFF"),
            getText("IGUI_ESQ_COMMON_UI_ON"),

            name = getText("IGUI_mo_book"),
            tooltip = getText("IGUI_mo_book_TT"),
            default = 2,
        },
    },
    mod_id = "buiBase",
    mod_fullname = getText("IGUI_mo_esqInfoName"),
    mod_shortname = getText("IGUI_mo_esqInfoName"),
}

function infoOptions.getOption(infoOption)
    if (getPlayer() and esQoLOptions.isMP and not esQoLOptions.isAdmin(getPlayer())) then
        local settings = esQoLOptions.getSettings(getPlayer());
        if not settings then return false end;
        esQolModOptions.options = settings;
    end
    if (esQolModOptions == nil) then return false end;

    if (infoOption == "drainOn") then return esQolModOptions.options.esQoLDrainableInfo == 2 end;
    if (infoOption == "healthOn") then return esQolModOptions.options.esQoLHealthInfo == 2 end;
    if (infoOption == "meleeOn") then return esQolModOptions.options.esQoLWIMelee > 1 end;
    if (infoOption == "meleeCardinal") then
        if (esQolModOptions.options.esQoLWIMelee == 2) then return "E" end;
        if (esQolModOptions.options.esQoLWIMelee == 3) then return "S" end;
        if (esQolModOptions.options.esQoLWIMelee == 4) then return "O" end;
    end
    if (infoOption == "rangedOn") then return esQolModOptions.options.esQoLWIRanged > 1 end;
    if (infoOption == "rangedCardinal") then
        if (esQolModOptions.options.esQoLWIRanged == 2) then return "E" end;
        if (esQolModOptions.options.esQoLWIRanged == 3) then return "S" end;
        if (esQolModOptions.options.esQoLWIRanged == 4) then return "O" end;
    end
    if (infoOption == "partsOn") then return esQolModOptions.options.esQoLWIParts > 1 end;
    if (infoOption == "partsCardinal") then
        if (esQolModOptions.options.esQoLWIParts == 2) then return "E" end;
        if (esQolModOptions.options.esQoLWIParts == 3) then return "S" end;
        if (esQolModOptions.options.esQoLWIParts == 4) then return "O" end;
    end
    if (infoOption == "booksOn") then return esQolModOptions.options.esQoLBookInfo > 1 end;
    if (infoOption == "carSpaceOn") then return esQolModOptions.options.esQoLCarSpaceInfo > 1 end;
    if (infoOption == "clotheOn") then return esQolModOptions.options.esQoLClothingInfo > 1 end;
    if (infoOption == "clotheCardinal") then
        if (esQolModOptions.options.esQoLClothingInfo == 2) then return "E" end;
        if (esQolModOptions.options.esQoLClothingInfo == 3) then return "S" end;
        if (esQolModOptions.options.esQoLClothingInfo == 4) then return "O" end;
    end
end

local function refreshGlobal()
    for k, v in pairs(modOptionsInfo.options) do
        esQolModOptions.options[k] = v;
    end
end

if ModOptions and ModOptions.getInstance then
    for k, v in pairs(infoOptions.configOptions.options_data) do
        infoOptions.configOptions.options_data[k]["OnApplyInGame"] = refreshGlobal;
    end

    modOptionsInfo = ModOptions:getInstance(infoOptions.configOptions);
    ModOptions:loadFile();
    refreshGlobal();
end

return infoOptions;