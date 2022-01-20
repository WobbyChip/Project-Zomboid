TheStar = TheStar or {}
TheStar.Utils = TheStar.Utils or {}

function TheStar.Utils.round(number, decimalPlaces)
    local multiplier = 10 ^ (decimalPlaces or 0)
    return math.floor(number * multiplier + 0.5) / multiplier
end

function TheStar.Utils.getHandMainOverlayTexture(number)
    local textureOrientation = TheStar.Options.progressVertical and "" or "Horizontal_"
    return getTexture("media/ui/HandMain_Overlay_" .. textureOrientation .. number .. ".png")
end

function TheStar.Utils.getHandMainOverlayReversedTexture(number)
    local textureOrientation = TheStar.Options.progressVertical and "" or "Horizontal_"
    return getTexture("media/ui/HandMain_Overlay_Reversed_" .. textureOrientation .. number .. ".png")
end

function TheStar.Utils.getIconTexture(number)
    local icon = TheStar.Options.iconType == 2 and "media/ui/QualityBubble_" or "media/ui/QualityStar_"
    return getTexture(icon .. number .. ".png")
end

function TheStar.Utils.getProgressColor(conditionLevel, isWaterSource)
    -- Used by the blinker
    if not TheStar.Options.showProgressBar then
        return TheStar.Config.COLOR_RED
    end

    local progressColor = TheStar.Config.COLOR_GREEN
    if isWaterSource then
        -- Change the progress bar's color for a water container
        progressColor = TheStar.Config.COLOR_BLUE
    else
        -- Change the progress bar's color depending on an item's condition
        if TheStar.Options.progressMulticolor then
            if conditionLevel <= 0.6 and conditionLevel > 0.3 then
                progressColor = TheStar.Config.COLOR_YELLOW
            elseif conditionLevel <= 0.3 then
                progressColor = TheStar.Config.COLOR_RED
            end
        end
    end
    return progressColor
end

function TheStar.Utils.isHandWeapon(item)
    return instanceof(item, "HandWeapon")
end

function TheStar.Utils.isRangedWeapon(item)
    return TheStar.Utils.isHandWeapon(item) and item:isRanged()
end

function TheStar.Utils.isWaterSource(item)
    return item:isWaterSource()
end

function TheStar.Utils.isLightSource(item)
    return instanceof(item, "DrainableComboItem") and item:canEmitLight()
end