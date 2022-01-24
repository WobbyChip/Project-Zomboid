-- Common
local function deepcopy(value)
	if type(value) == "table" then
		local result = {}
		for name, value in pairs(value) do
			result[name] = deepcopy(value)
		end
		return result
	else
		return value
	end
end

local function deepmerge(destination, source)
	for name, value in pairs(destination) do
		if type(value) == "table" then
			deepmerge(destination[name], source[name])
		else
			destination[name] = deepcopy(source[name])
		end
	end
	return
end

local function serializeSettings()
	local function stringify(value, prefix)
		local valueType = type(value)
		if		valueType == "table" then
			local result = (prefix.." = ("..prefix.." or {})\r\n")
			for name, value in pairs(value) do
				result = (result..stringify(value, (prefix.."[\""..name.."\"]")))
			end
			return result
		elseif	valueType == "string" then
			return (prefix.."=\""..value.."\"\r\n")
		else
			return (prefix.."="..tostring(value).."\r\n")
		end
	end


	local writer = getModFileWriter("HealthBars", "media/lua/client/HealthBars.Settings.lua", true, false)
	if writer ~= nil then
		writer:write("require \"HealthBars\"\r\n")
		writer:write(stringify(HealthBars["settings"], "HealthBars[\"settings\"]"))
		writer:close()
	end
	return
end


local DEFAULT_SETTINGS_BY_BAR_TYPES = {
		["endurance"] = {
				["visible"] = true,
				["x"] = 0,
				["y"] = 0,
				["movable"] = false,
				["width"] = 104,
				["height"] = 10,
				["resizable"] = false,
				["color"] = {r = (135 / 255), g = (206 / 255), b = (235 / 255), a = 0.5},
			},
		["water"] = {
				["visible"] = true,
				["x"] = 0,
				["y"] = 0,
				["movable"] = false,
				["width"] = 104,
				["height"] = 10,
				["resizable"] = false,
				["color"] = {r = ( 65 / 255), g = (105 / 255), b = (225 / 255), a = 0.5},
			},
		["food"] = {
				["visible"] = true,
				["x"] = 0,
				["y"] = 0,
				["movable"] = false,
				["width"] = 104,
				["height"] = 10,
				["resizable"] = false,
				["color"] = {r = (173 / 255), g = (255 / 255), b = ( 47 / 255), a = 0.5},
			},
		["health"] = {
				["visible"] = true,
				["x"] = 0,
				["y"] = 0,
				["movable"] = false,
				["width"] = 104,
				["height"] = 10,
				["resizable"] = false,
				["color"] = {r = (250 / 255), g = (128 / 255), b = (114 / 255), a = 0.5},
			},
		["coreTemperature"] = {
				["visible"] = true,
				["x"] = 0,
				["y"] = 0,
				["movable"] = false,
				["width"] = 104,
				["height"] = 10,
				["resizable"] = false,
			},
		["heatGeneration"] = {
				["visible"] = true,
				["x"] = 0,
				["y"] = 0,
				["movable"] = false,
				["width"] = 104,
				["height"] = 10,
				["resizable"] = false,
			},
	}
local DEFAULT_SETTINGS = {
		["version"] = 2020011000,
		["settingsByBarTypes"] = DEFAULT_SETTINGS_BY_BAR_TYPES,
		["settingsByBarTypesByPlayerNums"] = {},
	}
HealthBars = {
		["settings"] = deepcopy(DEFAULT_SETTINGS),
		["barByBarTypesByPlayerNums"] = {}
	}


local function createContextMenu(bar, dx, dy, withColorMenu)
	local contextMenu = ISContextMenu.get(bar.playerNum, (bar.x + dx), (bar.y + dy), 1, 1)
	contextMenu:addOption("["..getText("ContextMenu_HealthBars_BarType_"..bar.barType).."]")
	contextMenu:addOption(
			getText("ContextMenu_HealthBars_Toggle_Movable"),
			bar,
			function(bar)
					bar.moveWithMouse = (not bar.moveWithMouse)
					bar.settings2["movable"] = bar.moveWithMouse
					return
				end
		)
	contextMenu:addOption(
			getText("ContextMenu_HealthBars_Toggle_Resizable"),
			bar,
			function(bar)
					bar.resizeWithMouse = (not bar.resizeWithMouse)
					bar.settings2["resizable"] = bar.resizeWithMouse
					return
				end
		)
	if withColorMenu == true then
		contextMenu:addOption(
				getText("ContextMenu_HealthBars_Change_Color"),
				bar,
				function(bar)
						local colorPicker = ISColorPicker:new(contextMenu.x, contextMenu.y)
						bar.colorPicker = colorPicker
						colorPicker:initialise()
						colorPicker:setInitialColor(ColorInfo.new(bar.color.r, bar.color.g, bar.color.b, bar.color.a))
						colorPicker.pickedTarget = bar
						colorPicker.pickedFunc = function(bar, color)
								bar.color = {r = color.r, g = color.g, b = color.b, a = bar.color.a}
								bar.settings2["color"] = bar.color
								return
							end
						local screenHeight = getCore():getScreenHeight()
						local colorPickerBottom = (colorPicker.y + colorPicker.height)
						if colorPickerBottom > screenHeight then
							colorPicker.y = (colorPicker.y - (colorPickerBottom - screenHeight))
						end
						local screenWidth = getCore():getScreenWidth()
						local colorPickerRight = (colorPicker.x + colorPicker.width)
						if colorPickerRight > screenWidth then
							colorPicker.x = (colorPicker.x - (colorPickerRight - screenWidth))
						end
						colorPicker:addToUIManager()
						return
					end
			)
	end
	contextMenu:addOption(
			getText("ContextMenu_HealthBars_Toggle_Visible"),
			bar,
			function(bar)
					bar:setVisible(not bar:isVisible())
					return
				end
		)
	local toggleBarsMenu = ISContextMenu:getNew(contextMenu)
	contextMenu:addSubMenu(contextMenu:addOption(getText("ContextMenu_HealthBars_Toggle_Visible_Other_Bars")), toggleBarsMenu)
	for barType, otherBar in pairs(HealthBars["barByBarTypesByPlayerNums"][bar.playerNum]) do
		if otherBar ~= bar then
			toggleBarsMenu:addOption(
					getText("ContextMenu_HealthBars_BarType_"..barType),
					nil,
					function()
							otherBar:setVisible(not otherBar:isVisible())
							return
						end
				)
		end
	end
	local option = contextMenu:addOption(getText("ContextMenu_HealthBars_Save_Settings"), nil, serializeSettings)
	option.notAvailable = getWorld():getGameMode() == "Multiplayer"
	if option.notAvailable then
		local toolTip = ISToolTip:new()
		toolTip:initialise()
		toolTip:setVisible(false)
		toolTip.description = getText("IGUI_HealthBars_Save_Settings")
		option.toolTip = toolTip
	end
end


-- StandardBar
local StandardBar = ISPanel:derive("StandardBar")

function StandardBar:setWidth(w, ...)
	local result = ISPanel.setWidth(self, w, ...)


	self.innerWidth = (self.width - self.borderSizes.l - self.borderSizes.r)


	return result
end

function StandardBar:setHeight(h, ...)
	local result = ISPanel.setHeight(self, h, ...)


	self.innerHeight = (self.height - self.borderSizes.t - self.borderSizes.b)


	return result
end

function StandardBar:render(...)
	local result = ISPanel.render(self, ...)


	self:drawRectStatic(
			self.borderSizes.l,
			self.borderSizes.t,
			math.floor((self.innerWidth * self:getValue()) + 0.5),
			self.innerHeight,
			self.color.a,
			self.color.r,
			self.color.g,
			self.color.b
		)

	if		self.moving
		 or self.resizing then
		self:drawRectStatic(
				self.borderSizes.l,
				self.borderSizes.t,
				self.innerWidth,
				self.innerHeight,
				0.5,
				0,
				0,
				0
			)
		self:drawText(
				("X: "..self.x..", Y: "..self.y..", W: "..self.width..", H: "..self.height),
				self.borderSizes.l,
				self.borderSizes.t,
				1,
				1,
				1,
				1,
				UIFont.Small
			)
	end


	return result
end

function StandardBar:onMouseUp(x, y, ...)
	local moved = (self.moveWithMouse and self.moving)
	local resized = (self.resizeWithMouse and self.resizing)


	local result = ISPanel.onMouseUp(self, x, y, ...)


	self.resizing = false
	if		moved
		or  resized then
		self.settings2["x"] = self.x
		self.settings2["y"] = self.y
		self.settings2["width"] = self.width
		self.settings2["height"] = self.height
	end


	return result
end

function StandardBar:onMouseUpOutside(x, y, ...)
	local result = ISPanel.onMouseUpOutside(self, x, y, ...)


	self.resizing = false


	return result
end

function StandardBar:onMouseDown(x, y, ...)
	local result = ISPanel.onMouseDown(self, x, y, ...)


	if self.resizeWithMouse then
		self.resizingBorders = {
				["l"] = (x >= 0
					and  x <= self.borderSizes.l),
				["t"] = (y >= 0
					and  y <= self.borderSizes.t),
				["r"] = x <= self.width
					and x >= (self.width - self.borderSizes.r),
				["b"] = y <= self.height
					and y >= (self.height - self.borderSizes.b),
			}
		self.resizing = (self.resizingBorders["l"] == true
					or   self.resizingBorders["t"] == true
					or   self.resizingBorders["r"] == true
					or   self.resizingBorders["b"] == true)
	end
	if self.moveWithMouse then
		self.moving = (not self.resizeWithMouse
				or     not self.resizing)
	end


	return result
end

function StandardBar:applySizeDelta(dx, dy)
	if		self.resizingBorders["l"] == true then
		self:setX(self.x + dx)
	end
	if		self.resizingBorders["l"] == true then
		self:setWidth(math.max(self.minimumWidth, (self.width - dx)))
	elseif	self.resizingBorders["r"] == true then
		self:setWidth(math.max(self.minimumWidth, (self.width + dx)))
	end
	if		self.resizingBorders["t"] == true then
		self:setY(self.y + dy)
	end
	if		self.resizingBorders["t"] == true then
		self:setHeight(math.max(self.minimumHeight, (self.height - dy)))
	elseif	self.resizingBorders["b"] == true then
		self:setHeight(math.max(self.minimumHeight, (self.height + dy)))
	end
	return
end

function StandardBar:onMouseMoveOutside(dx, dy, ...)
	local result = ISPanel.onMouseMoveOutside(self, dx, dy, ...)


	if self.resizing then
		self:applySizeDelta(dx, dy)
	end


	return result
end

function StandardBar:onMouseMove(dx, dy, ...)
	local result = ISPanel.onMouseMove(self, dx, dy, ...)


	if self.resizing then
		self:applySizeDelta(dx, dy)
	end


	return result
end

function StandardBar:onMouseDoubleClick(x, y, ...)
	return
end

function StandardBar:onRightMouseDown(x, y, ...)
	local result = ISPanel.onRightMouseDown(self, x, y, ...)


	self.rightMouseDown = true


	return result
end

function StandardBar:onRightMouseUp(dx, dy, ...)
	local result = ISPanel.onRightMouseUp(self, dx, dy, ...)


	if		self.rightMouseDown == true then
		createContextMenu(self, dx, dy, true)
	end
	self.rightMouseDown = false


	return result
end

function StandardBar:onRightMouseUpOutside(x, y, ...)
	local result = ISPanel.onRightMouseUpOutside(self, x, y, ...)


	self.rightMouseDown = false


	return result
end

function StandardBar:setVisible(visible)
	self.settings2["visible"] = (visible == true)
	return ISPanel.setVisible(self, visible)
end

function StandardBar:isVisible()
	return (self.settings2["visible"] == true)
end

function StandardBar:new(playerNum, barType, getValue)
	local settings = HealthBars["settings"]["settingsByBarTypesByPlayerNums"][tostring(playerNum)][barType]

	local instance = ISPanel:new(settings["x"], settings["y"], settings["width"], settings["height"])
	setmetatable(instance, self)
	self.__index = self

	instance.playerNum = playerNum
	instance.barType = barType
	instance.settings2 = settings
	instance.moveWithMouse = (settings["movable"] == true)
	instance.resizeWithMouse = (settings["resizable"] == true)
	instance.borderSizes = {l = 2, t = 2, r = 2, b = 2}
	instance.innerWidth = (instance.width - instance.borderSizes.l - instance.borderSizes.r)
	instance.innerHeight = (instance.height - instance.borderSizes.t - instance.borderSizes.b)
	instance.color = settings["color"]
	instance.minimumWidth = (1 + instance.borderSizes.l + instance.borderSizes.r)
	instance.minimumHeight = (1 + instance.borderSizes.t + instance.borderSizes.b)
	instance.getValue = getValue

	instance:setVisible(settings["visible"] == true)

	HealthBars["barByBarTypesByPlayerNums"][playerNum][barType] = instance
	return instance
end


-- TemperatureBar
local TemperatureBar = ISGradientBar:derive("TemperatureBar")

function TemperatureBar:prerender(...)
	local result = ISGradientBar.prerender(self, ...)


	self:setValue(self:getValue())


	return result
end

function TemperatureBar:render(...)
	local result = ISGradientBar.render(self, ...)


	if		self.moving
		 or self.resizing then
		self:drawRectStatic(
				self.borderSizes.l,
				self.borderSizes.t,
				(self.width - self.borderSizes.l - self.borderSizes.r),
				(self.height - self.borderSizes.t - self.borderSizes.b),
				0.5,
				0,
				0,
				0
			)
		self:drawText(
				("X: "..self.x..", Y: "..self.y..", W: "..self.width..", H: "..self.height),
				self.borderSizes.l,
				self.borderSizes.t,
				1,
				1,
				1,
				1,
				UIFont.Small
			)
	end


	return result
end

function TemperatureBar:onMouseUp(x, y, ...)
	local moved = (self.moveWithMouse and self.moving)
	local resized = (self.resizeWithMouse and self.resizing)


	local result = ISGradientBar.onMouseUp(self, x, y, ...)


	self.resizing = false
	if		moved
		or  resized then
		self.settings2["x"] = self.x
		self.settings2["y"] = self.y
		self.settings2["width"] = self.width
		self.settings2["height"] = self.height
	end


	return result
end

function TemperatureBar:onMouseUpOutside(x, y, ...)
	local result = ISGradientBar.onMouseUpOutside(self, x, y, ...)


	self.resizing = false


	return result
end

function TemperatureBar:onMouseDown(x, y, ...)
	local result = ISGradientBar.onMouseDown(self, x, y, ...)


	if self.resizeWithMouse then
		self.resizingBorders = {
				["l"] = (x >= 0
					and  x <= self.borderSizes.l),
				["t"] = (y >= 0
					and  y <= self.borderSizes.t),
				["r"] = x <= self.width
					and x >= (self.width - self.borderSizes.r),
				["b"] = y <= self.height
					and y >= (self.height - self.borderSizes.b),
			}
		self.resizing = (self.resizingBorders["l"] == true
					or   self.resizingBorders["t"] == true
					or   self.resizingBorders["r"] == true
					or   self.resizingBorders["b"] == true)
	end
	if self.moveWithMouse then
		self.moving = (not self.resizeWithMouse
				or     not self.resizing)
	end


	return result
end

function TemperatureBar:applySizeDelta(dx, dy)
	if		self.resizingBorders["l"] == true then
		self:setX(self.x + dx)
	end
	if		self.resizingBorders["l"] == true then
		self:setWidth(math.max(self.minimumWidth, (self.width - dx)))
	elseif	self.resizingBorders["r"] == true then
		self:setWidth(math.max(self.minimumWidth, (self.width + dx)))
	end
	if		self.resizingBorders["t"] == true then
		self:setY(self.y + dy)
	end
	if		self.resizingBorders["t"] == true then
		self:setHeight(math.max(self.minimumHeight, (self.height - dy)))
	elseif	self.resizingBorders["b"] == true then
		self:setHeight(math.max(self.minimumHeight, (self.height + dy)))
	end

	self:adjustRadius()
	return
end

function TemperatureBar:onMouseMoveOutside(dx, dy, ...)
	local result = ISGradientBar.onMouseMoveOutside(self, dx, dy, ...)


	if self.resizing then
		self:applySizeDelta(dx, dy)
	end


	return result
end

function TemperatureBar:onMouseMove(dx, dy, ...)
	local result = ISGradientBar.onMouseMove(self, dx, dy, ...)


	if self.resizing then
		self:applySizeDelta(dx, dy)
	end


	return result
end

function TemperatureBar:onMouseDoubleClick(x, y, ...)
	return
end

function TemperatureBar:onRightMouseDown(x, y, ...)
	local result = ISGradientBar.onRightMouseDown(self, x, y, ...)


	self.rightMouseDown = true


	return result
end

function TemperatureBar:onRightMouseUp(dx, dy, ...)
	local result = ISGradientBar.onRightMouseUp(self, dx, dy, ...)


	if		self.rightMouseDown == true then
		createContextMenu(self, dx, dy)
	end
	self.rightMouseDown = false


	return result
end

function TemperatureBar:onRightMouseUpOutside(x, y, ...)
	local result = ISGradientBar.onRightMouseUpOutside(self, x, y, ...)


	self.rightMouseDown = false


	return result
end

function TemperatureBar:adjustRadius()
	self.settings.radius = math.max(3, math.floor((25 * (self.width / 255)) + 0.5))
	return
end

function TemperatureBar:setVisible(visible)
	self.settings2["visible"] = (visible == true)
	return ISGradientBar.setVisible(self, visible)
end

function TemperatureBar:isVisible()
	return (self.settings2["visible"] == true)
end

function TemperatureBar:new(playerNum, barType, getValue)
	local settings = HealthBars["settings"]["settingsByBarTypesByPlayerNums"][tostring(playerNum)][barType]

	local instance = ISGradientBar:new(settings["x"], settings["y"], settings["width"], settings["height"])
	setmetatable(instance, self)
	self.__index = self

	instance.settings.gradientTex = getTexture("media/ui/BodyInsulation/heatbar_horz")
	instance.settings.doKnob = false

	instance.playerNum = playerNum
	instance.barType = barType
	instance.settings2 = settings
	instance.moveWithMouse = (settings["movable"] == true)
	instance.resizeWithMouse = (settings["resizable"] == true)
	instance.borderSizes = {l = 2, t = 2, r = 2, b = 2}
	instance.minimumWidth = (1 + instance.borderSizes.l + instance.borderSizes.r)
	instance.minimumHeight = (1 + instance.borderSizes.t + instance.borderSizes.b)
	instance.getValue = getValue

	instance:adjustRadius()
	instance:setVisible(settings["visible"] == true)

	HealthBars["barByBarTypesByPlayerNums"][playerNum][barType] = instance
	return instance
end

Events.OnGameBoot.Add(
		function()
				local healthBarsSettings = HealthBars["settings"]
				local settingsVersion = healthBarsSettings["version"]
				if		settingsVersion == 2020011000 then
					local settingsByBarTypesByPlayerNums = healthBarsSettings["settingsByBarTypesByPlayerNums"]
					if		healthBarsSettings["initialized"] == true then
						settingsByBarTypesByPlayerNums["0"] = healthBarsSettings["settingsByBarTypes"]
					end
					
					local defaultSettingsByBarTypesByPlayerNums = DEFAULT_SETTINGS["settingsByBarTypesByPlayerNums"]
					for playerNum, settingsByBarTypes in pairs(settingsByBarTypesByPlayerNums) do
						defaultSettingsByBarTypesByPlayerNums[playerNum] = DEFAULT_SETTINGS_BY_BAR_TYPES
					end
				end


				healthBarsSettings = deepcopy(DEFAULT_SETTINGS)
				deepmerge(healthBarsSettings, HealthBars["settings"])
				HealthBars["settings"] = healthBarsSettings


				if		settingsVersion == 2020011000 then
					healthBarsSettings["initialized"] = nil
					healthBarsSettings["settingsByBarTypes"] = nil
				end
				return
			end
	)
Events.OnCreatePlayer.Add(
		function(playerNum, playerObj)
				local settingsByBarTypesByPlayerNums = HealthBars["settings"]["settingsByBarTypesByPlayerNums"]
				local settingsByBarTypes = settingsByBarTypesByPlayerNums[tostring(playerNum)]
				if settingsByBarTypes == nil then
					settingsByBarTypes = deepcopy(DEFAULT_SETTINGS_BY_BAR_TYPES)
					settingsByBarTypesByPlayerNums[tostring(playerNum)] = settingsByBarTypes


					local numActivePlayers = getNumActivePlayers()
					local screenWidth = (getCore():getScreenWidth() / numActivePlayers)
					local screenXCenter = ((screenWidth * playerNum) + (screenWidth / 2))
					local playerHotbarTop = (getPlayerHotbar(tostring(playerNum)).y - 12)

					local healthBarSettings = settingsByBarTypes["health"]
					healthBarSettings["x"] = (screenXCenter - (healthBarSettings["width"] / 2))
					healthBarSettings["y"] = (playerHotbarTop - 1 - healthBarSettings["height"])

					local enduranceBarSettings = settingsByBarTypes["endurance"]
					enduranceBarSettings["x"] = healthBarSettings["x"]
					enduranceBarSettings["y"] = (healthBarSettings["y"] - 1 - enduranceBarSettings["height"])

					local waterBarSettings = settingsByBarTypes["water"]
					waterBarSettings["x"] = (healthBarSettings["x"] - 1 - waterBarSettings["width"])
					waterBarSettings["y"] = healthBarSettings["y"]

					local foodBarSettings = settingsByBarTypes["food"]
					foodBarSettings["x"] = waterBarSettings["x"]
					foodBarSettings["y"] = enduranceBarSettings["y"]

					local heatGenerationBarSettings = settingsByBarTypes["heatGeneration"]
					heatGenerationBarSettings["x"] = (healthBarSettings["x"] + 1 + heatGenerationBarSettings["width"])
					heatGenerationBarSettings["y"] = healthBarSettings["y"]

					local coreTemperatureBarSettings = settingsByBarTypes["coreTemperature"]
					coreTemperatureBarSettings["x"] = heatGenerationBarSettings["x"]
					coreTemperatureBarSettings["y"] = enduranceBarSettings["y"]
				end


				HealthBars["barByBarTypesByPlayerNums"][playerNum] = {}


				local healthBar = StandardBar:new(
						playerNum,
						"health",
						function()
								return (playerObj:getBodyDamage():getHealth() / 100)
							end
					)
				healthBar:initialise()
				healthBar:addToUIManager()

				local enduranceBar = StandardBar:new(
						playerNum,
						"endurance",
						function()
								return (playerObj:getStats():getEndurance() ^ 2)
							end
					)
				enduranceBar:initialise()
				enduranceBar:addToUIManager()

				local waterBar = StandardBar:new(
						playerNum,
						"water",
						function()
								return ((1 - playerObj:getStats():getThirst()) ^ 2)
							end
					)
				waterBar:initialise()
				waterBar:addToUIManager()

				local foodBar = StandardBar:new(
						playerNum,
						"food",
						function()
								return ((1 - playerObj:getStats():getHunger()) ^ 2)
							end
					)
				foodBar:initialise()
				foodBar:addToUIManager()

				local heatGenerationBar = TemperatureBar:new(
						playerNum,
						"heatGeneration",
						function()
								return playerObj:getBodyDamage():getThermoregulator():getHeatGenerationUI()
							end
					)
				heatGenerationBar:initialise()
				heatGenerationBar:addToUIManager()

				local coreTemperatureBar = TemperatureBar:new(
						playerNum,
						"coreTemperature",
						function()
								return playerObj:getBodyDamage():getThermoregulator():getCoreTemperatureUI()
							end
					)
				coreTemperatureBar:initialise()
				coreTemperatureBar:addToUIManager()
				return
			end
	)
