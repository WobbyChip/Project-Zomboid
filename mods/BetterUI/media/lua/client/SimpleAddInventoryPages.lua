SimpleAddInventoryPages = {["containerButtonTextureColor"] = {r=0.5, g=0.5, b=0.5, a=1.0}}


Events.OnRefreshInventoryWindowContainers.Add(
		function(inventoryPage, state)
				if		inventoryPage.onCharacter
					and state == "buttonsAdded" then
					local specificPlayer = getSpecificPlayer(inventoryPage.player)
					local items = specificPlayer:getInventory():getItems()
					for index = 0, (items:size() - 1) do
						local item = items:get(index)
						if		item:getCategory() == "Container"
							and not specificPlayer:isEquipped(item)
							and item:getType() ~= "KeyRing" then
							local containerButton = inventoryPage:addContainerButton(item:getInventory(), item:getTex(), item:getName(), item:getName())
							if		item:getVisual()
								and item:getClothingItem() then
								local tint = item:getVisual():getTint(item:getClothingItem())
								containerButton:setTextureRGBA(
										(tint:getRedFloat() * 0.5),
										(tint:getGreenFloat() * 0.5),
										(tint:getBlueFloat() * 0.5),
										1.0
									)
							else
								local containerButtonTextureColor = SimpleAddInventoryPages["containerButtonTextureColor"]
								if	containerButtonTextureColor ~= nil then
									containerButton:setTextureRGBA(
											containerButtonTextureColor.r,
											containerButtonTextureColor.g,
											containerButtonTextureColor.b,
											containerButtonTextureColor.a
										)
								end
							end
							local containerButtonBorderColor = SimpleAddInventoryPages["containerButtonBorderColor"]
							if containerButtonBorderColor ~= nil then
								containerButton:setBorderRGBA(
										containerButtonBorderColor.r,
										containerButtonBorderColor.g,
										containerButtonBorderColor.b,
										containerButtonBorderColor.a
									)
							end
						end
					end
				end
				return
			end
	)
