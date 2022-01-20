TheStar = TheStar or {}
TheStar.Options = TheStar.Options or {}

TheStar.Options.equippedItemOnly = false
TheStar.Options.showIcon = true
TheStar.Options.iconType = 1
TheStar.Options.equippedItemIconPosition = 2
TheStar.Options.showProgressBar = true
TheStar.Options.progressVertical = true
TheStar.Options.progressBarOpacity = 0.2
TheStar.Options.progressBarOpacityEquipped = 0.4
TheStar.Options.progressMulticolor = true
TheStar.Options.showOverheadNotification = true
TheStar.Options.overheadNotificationConditionLevel = 0.999
TheStar.Options.blinkOnConditionDrop = true
TheStar.Options.blinkCondition = 0.999
TheStar.Options.showAmmoCountHotbar = true
TheStar.Options.showAmmoCountEquippedItem = true
TheStar.Options.showAmmoCountInventory = true
TheStar.Options.showBatteryChargeHotbar = true
TheStar.Options.showBatteryChargeEquippedItem = true
TheStar.Options.showItemTooltipHotbar = true
TheStar.Options.showItemTooltipEquippedItem = true
TheStar.Options.showOverheadNotificationItemFall = true

local percentValues = { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0 }
local conditionValues = { 0.999, 0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1, 0.0 }

if ModOptions and ModOptions.getInstance then
    local function onModOptionsApply(optionValues)
        TheStar.Options.equippedItemOnly = optionValues.settings.options.equippedItemOnly
        TheStar.Options.showIcon = optionValues.settings.options.showIcon
        TheStar.Options.iconType = optionValues.settings.options.iconType
        TheStar.Options.equippedItemIconPosition = optionValues.settings.options.equippedItemIconPosition
        TheStar.Options.showProgressBar = optionValues.settings.options.showProgressBar
        TheStar.Options.progressVertical = optionValues.settings.options.progressOrientation == 2
        TheStar.Options.progressBarOpacity = percentValues[optionValues.settings.options.progressBarOpacity]
        TheStar.Options.progressBarOpacityEquipped = percentValues[optionValues.settings.options.progressBarOpacityEquipped]
        TheStar.Options.progressMulticolor = optionValues.settings.options.progressMulticolor
        TheStar.Options.updateNotificationCondition(optionValues.settings.options.overheadNotificationCondition)
        TheStar.Options.updateBlinkCondition(optionValues.settings.options.blinkCondition)
        TheStar.Options.updateAmmoCount(optionValues.settings.options.showAmmoCount)
        TheStar.Options.showAmmoCountInventory = optionValues.settings.options.showAmmoCountInventory
        TheStar.Options.updateBatteryCharge(optionValues.settings.options.showBatteryCharge)
        TheStar.Options.updateItemTooltip(optionValues.settings.options.showItemTooltip)
        TheStar.Options.showOverheadNotificationItemFall = optionValues.settings.options.showOverheadNotificationItemFall
    end

    local SETTINGS = {
        options_data = {
            equippedItemOnly = {
                name = "UI_TheStar_EquippedItemOnly",
                default = false,
                OnApplyMainMenu = onModOptionsApply,
                OnApplyInGame = onModOptionsApply,
            },
            showIcon = {
                name = "UI_TheStar_Icon",
                tooltip = "UI_TheStar_Icon_Tooltip",
                default = true,
                OnApplyMainMenu = onModOptionsApply,
                OnApplyInGame = onModOptionsApply,
            },
            iconType = {
                getText("UI_TheStar_Icon_Type_Star"), getText("UI_TheStar_Icon_Type_Bubble"),

                name = "UI_TheStar_Icon_Type",
                default = 1,
                OnApplyMainMenu = onModOptionsApply,
                OnApplyInGame = onModOptionsApply,
            },
            equippedItemIconPosition = {
                getText("UI_TheStar_EquippedItemIconPosition_TopLeft"),
                getText("UI_TheStar_EquippedItemIconPosition_TopRight"),

                name = "UI_TheStar_EquippedItemIconPosition",
                default = 2,
                OnApplyMainMenu = onModOptionsApply,
                OnApplyInGame = onModOptionsApply,
            },
            showProgressBar = {
                name = "UI_TheStar_ProgressBar",
                tooltip = "UI_TheStar_ProgressBar_Tooltip",
                default = true,
                OnApplyMainMenu = onModOptionsApply,
                OnApplyInGame = onModOptionsApply,
            },
            progressOrientation = {
                getText("UI_TheStar_ProgressBar_Horizontal"), getText("UI_TheStar_ProgressBar_Vertical"),

                name = "UI_TheStar_ProgressBar_Orientation",
                default = 2,
                OnApplyMainMenu = onModOptionsApply,
                OnApplyInGame = onModOptionsApply,
            },
            progressBarOpacity = {
                "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%",

                name = "UI_TheStar_ProgressBar_Opacity",
                tooltip = "UI_TheStar_ProgressBar_Opacity_Tooltip",
                default = 2,
                OnApplyMainMenu = onModOptionsApply,
                OnApplyInGame = onModOptionsApply,
            },
            progressBarOpacityEquipped = {
                "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%",

                name = "UI_TheStar_ProgressBar_OpacityEquipped",
                tooltip = "UI_TheStar_ProgressBar_Opacity_Tooltip",
                default = 4,
                OnApplyMainMenu = onModOptionsApply,
                OnApplyInGame = onModOptionsApply,
            },
            progressMulticolor = {
                name = "UI_TheStar_ProgressBar_Multicolor",
                tooltip = "UI_TheStar_ProgressBar_Multicolor_Tooltip",
                default = true,
                OnApplyMainMenu = onModOptionsApply,
                OnApplyInGame = onModOptionsApply,
            },
            overheadNotificationCondition = {
                getText("UI_TheStar_Dropdown_Always"),
                "90%", "80%", "70%", "60%", "50%", "40%", "30%", "20%", "10%",
                getText("UI_TheStar_Dropdown_Off"),

                name = "UI_TheStar_OverheadNotification",
                tooltip = "UI_TheStar_OverheadNotification_Tooltip",
                default = 1,
                OnApplyMainMenu = onModOptionsApply,
                OnApplyInGame = onModOptionsApply,
            },
            blinkCondition = {
                getText("UI_TheStar_Dropdown_Always"),
                "90%", "80%", "70%", "60%", "50%", "40%", "30%", "20%", "10%",
                getText("UI_TheStar_Dropdown_Off"),

                name = "UI_TheStar_BlinkConditionPercent",
                tooltip = "UI_TheStar_BlinkConditionPercent_Tooltip",
                default = 1,
                OnApplyMainMenu = onModOptionsApply,
                OnApplyInGame = onModOptionsApply,
            },
            showAmmoCount = {
                getText("UI_TheStar_Dropdown_Off"), getText("UI_TheStar_Dropdown_HotbarEquippedItem"),
                getText("UI_TheStar_Dropdown_HotbarOnly"), getText("UI_TheStar_Dropdown_EquippedItemOnly"),

                name = "UI_TheStar_Ammo",
                default = 2,
                OnApplyMainMenu = onModOptionsApply,
                OnApplyInGame = onModOptionsApply,
            },
            showAmmoCountInventory = {
                name = "UI_TheStar_AmmoInventory",
                tooltip = "UI_TheStar_AmmoInventory_Tooltip",
                default = true,
                OnApplyMainMenu = onModOptionsApply,
                OnApplyInGame = onModOptionsApply,
            },
            showBatteryCharge = {
                getText("UI_TheStar_Dropdown_Off"), getText("UI_TheStar_Dropdown_HotbarEquippedItem"),
                getText("UI_TheStar_Dropdown_HotbarOnly"), getText("UI_TheStar_Dropdown_EquippedItemOnly"),

                name = "UI_TheStar_BatteryCharge",
                default = 2,
                OnApplyMainMenu = onModOptionsApply,
                OnApplyInGame = onModOptionsApply,
            },
            showItemTooltip = {
                getText("UI_TheStar_Dropdown_Off"), getText("UI_TheStar_Dropdown_HotbarEquippedItem"),
                getText("UI_TheStar_Dropdown_HotbarOnly"), getText("UI_TheStar_Dropdown_EquippedItemOnly"),

                name = "UI_TheStar_ItemTooltip",
                tooltip = "UI_TheStar_ItemTooltip_Tooltip",
                default = 2,
                OnApplyMainMenu = onModOptionsApply,
                OnApplyInGame = onModOptionsApply,
            },
            showOverheadNotificationItemFall = {
                name = "UI_TheStar_OverheadNotificationItemFall",
                tooltip = "UI_TheStar_OverheadNotificationItemFall_Tooltip",
                default = true,
                OnApplyMainMenu = onModOptionsApply,
                OnApplyInGame = onModOptionsApply,
            },
        },

        mod_id = 'TheStar',
        mod_shortname = 'Weapon Condition Indicator',
        mod_fullname = 'Weapon Condition Indicator',
    }

    -- Was added in Build 41.64
    if getCore():getGameVersion():getMinor() < 64 then
        SETTINGS.options_data.showOverheadNotificationItemFall = nil
    end

    local optionsInstance = ModOptions:getInstance(SETTINGS)
    ModOptions:loadFile()

    local optionNotificationCondition = optionsInstance:getData("overheadNotificationCondition")
    function optionNotificationCondition:OnApply(newValue)
        TheStar.Options.updateNotificationCondition(newValue)
        TheStar.Notifier.Condition.init()
    end
    local optionNotificationItemFall = optionsInstance:getData("showOverheadNotificationItemFall")
    if optionNotificationItemFall then
        function optionNotificationItemFall:OnApply(newValue)
            TheStar.Options.showOverheadNotificationItemFall = newValue
            TheStar.Notifier.ItemFall.init()
        end
    end
    local optionShowAmmoCount = optionsInstance:getData("showAmmoCount")
    function optionShowAmmoCount:OnApply(newValue)
        TheStar.Options.updateAmmoCount(newValue)
        TheStar.HandMainExtension.Ammo.init()
    end
    local optionShowBatteryCharge = optionsInstance:getData("showBatteryCharge")
    function optionShowBatteryCharge:OnApply(newValue)
        TheStar.Options.updateBatteryCharge(newValue)
        TheStar.HandMainExtension.Battery.init()
        TheStar.HandSecondaryExtension.init()
    end

    Events.OnPreMapLoad.Add(function() onModOptionsApply({ settings = SETTINGS }) end)
end

function TheStar.Options.updateNotificationCondition(optionValue)
    TheStar.Options.showOverheadNotification = optionValue ~= 11
    TheStar.Options.overheadNotificationConditionLevel = conditionValues[optionValue]
end

function TheStar.Options.updateBlinkCondition(optionValue)
    TheStar.Options.blinkOnConditionDrop = optionValue ~= 11
    TheStar.Options.blinkCondition = conditionValues[optionValue]
end

function TheStar.Options.updateAmmoCount(optionValue)
    TheStar.Options.showAmmoCountHotbar = optionValue == 2 or optionValue == 3
    TheStar.Options.showAmmoCountEquippedItem = optionValue == 2 or optionValue == 4
end

function TheStar.Options.updateBatteryCharge(optionValue)
    TheStar.Options.showBatteryChargeHotbar = optionValue == 2 or optionValue == 3
    TheStar.Options.showBatteryChargeEquippedItem = optionValue == 2 or optionValue == 4
end

function TheStar.Options.updateItemTooltip(optionValue)
    TheStar.Options.showItemTooltipHotbar = optionValue == 2 or optionValue == 3
    TheStar.Options.showItemTooltipEquippedItem = optionValue == 2 or optionValue == 4
end

-- obsolete options
-- blinkOnConditionDrop
-- showOverheadNotification