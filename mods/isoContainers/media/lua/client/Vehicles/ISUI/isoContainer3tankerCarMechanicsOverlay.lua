local function info()
--this chunk here is hell, i hate it so much, gas tank is a piece of...

ISCarMechanicsOverlay.CarList["Base.isoContainer3tanker"] = {imgPrefix = "isoContainer3tanker_", x=10,y=0};
--
ISCarMechanicsOverlay.PartList["TrunkDoor"] = {img="trunkdoor", vehicles = {"isoContainer3tanker_"}};
ISCarMechanicsOverlay.PartList["TrunkDoor"].vehicles = ISCarMechanicsOverlay.PartList["TrunkDoor"].vehicles or {};
ISCarMechanicsOverlay.PartList["TrunkDoor"].vehicles["isoContainer3tanker_"] = {x=0,y=0,x2=0,y2=0};
--
end


Events.OnInitWorld.Add(info);