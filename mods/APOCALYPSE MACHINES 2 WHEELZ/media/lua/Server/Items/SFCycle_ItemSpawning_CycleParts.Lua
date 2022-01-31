require 'Items/Distributions'
require "Items/ProceduralDistributions" 
require "Vehicles/VehicleDistributions"

table.insert(ProceduralDistributions.list.CrateMechanics.items, "filcher.CycleBattery")
table.insert(ProceduralDistributions.list.CrateMechanics.items, 2)
table.insert(ProceduralDistributions.list.CrateMechanics.items, "filcher.NormalCycleSuspension")
table.insert(ProceduralDistributions.list.CrateMechanics.items, 0.5)
table.insert(ProceduralDistributions.list.CrateMechanics.items, "filcher.OldCycleBrake")
table.insert(ProceduralDistributions.list.CrateMechanics.items, 3)
table.insert(ProceduralDistributions.list.CrateMechanics.items, "filcher.OldCycleMuffler")
table.insert(ProceduralDistributions.list.CrateMechanics.items, 4)

table.insert(ProceduralDistributions.list.CrateMechanics.items, "Base.NormalScooterSuspension")
table.insert(ProceduralDistributions.list.CrateMechanics.items, 0.5)
table.insert(ProceduralDistributions.list.CrateMechanics.items, "Base.OldScooterBrake")
table.insert(ProceduralDistributions.list.CrateMechanics.items, 3)
table.insert(ProceduralDistributions.list.CrateMechanics.items, "Base.OldScooterMuffler")
table.insert(ProceduralDistributions.list.CrateMechanics.items, 4)

if ProceduralDistributions.list.GarageMechanic then
	table.insert(ProceduralDistributions.list.GarageMechanic.items, "filcher.CycleBattery")
	table.insert(ProceduralDistributions.list.GarageMechanic.items, 0.25)
end

if ProceduralDistributions.list.GarageMechanics then
	table.insert(ProceduralDistributions.list.GarageMechanics.items, "filcher.CycleBattery")
	table.insert(ProceduralDistributions.list.GarageMechanics.items, 0.25)
end

table.insert(ProceduralDistributions.list.MechanicShelfBrakes.items, "filcher.NormalCycleBrake")
table.insert(ProceduralDistributions.list.MechanicShelfBrakes.items, 3)
table.insert(ProceduralDistributions.list.MechanicShelfBrakes.items, "filcher.ModernCycleBrake")
table.insert(ProceduralDistributions.list.MechanicShelfBrakes.items, 2)

table.insert(ProceduralDistributions.list.MechanicShelfBrakes.items, "Base.NormalScooterBrake")
table.insert(ProceduralDistributions.list.MechanicShelfBrakes.items, 3)
table.insert(ProceduralDistributions.list.MechanicShelfBrakes.items, "Base.ModernScooterBrake")
table.insert(ProceduralDistributions.list.MechanicShelfBrakes.items, 2)

table.insert(ProceduralDistributions.list.MechanicShelfElectric.items, "filcher.CycleBattery")
table.insert(ProceduralDistributions.list.MechanicShelfElectric.items, 3)

table.insert(ProceduralDistributions.list.MechanicShelfMufflers.items, "filcher.NormalCycleMuffler")
table.insert(ProceduralDistributions.list.MechanicShelfMufflers.items, 3)
table.insert(ProceduralDistributions.list.MechanicShelfMufflers.items, "filcher.ModernCycleMuffler")
table.insert(ProceduralDistributions.list.MechanicShelfMufflers.items, 2)

table.insert(ProceduralDistributions.list.MechanicShelfMufflers.items, "Base.NormalScooterMuffler")
table.insert(ProceduralDistributions.list.MechanicShelfMufflers.items, 3)
table.insert(ProceduralDistributions.list.MechanicShelfMufflers.items, "Base.ModernScooterMuffler")
table.insert(ProceduralDistributions.list.MechanicShelfMufflers.items, 2)

table.insert(ProceduralDistributions.list.MechanicShelfSuspension.items, "filcher.NormalCycleSuspension")
table.insert(ProceduralDistributions.list.MechanicShelfSuspension.items, 3)
table.insert(ProceduralDistributions.list.MechanicShelfSuspension.items, "filcher.ModernCycleSuspension")
table.insert(ProceduralDistributions.list.MechanicShelfSuspension.items, 2)

table.insert(ProceduralDistributions.list.MechanicShelfSuspension.items, "Base.NormalScooterSuspension")
table.insert(ProceduralDistributions.list.MechanicShelfSuspension.items, 3)
table.insert(ProceduralDistributions.list.MechanicShelfSuspension.items, "Base.ModernScooterSuspension")
table.insert(ProceduralDistributions.list.MechanicShelfSuspension.items, 2)

table.insert(ProceduralDistributions.list.MechanicShelfWheels.items, "filcher.NormalCycleTire")
table.insert(ProceduralDistributions.list.MechanicShelfWheels.items, 3)
table.insert(ProceduralDistributions.list.MechanicShelfWheels.items, "filcher.ModernCycleTire")
table.insert(ProceduralDistributions.list.MechanicShelfWheels.items, 2)

table.insert(ProceduralDistributions.list.MechanicShelfWheels.items, "Base.NormalScooterTire")
table.insert(ProceduralDistributions.list.MechanicShelfWheels.items, 3)
table.insert(ProceduralDistributions.list.MechanicShelfWheels.items, "Base.ModernScooterTire")
table.insert(ProceduralDistributions.list.MechanicShelfWheels.items, 2)

table.insert(ProceduralDistributions.list.MechanicShelfWheels.items, "Base.NormalATVTire")
table.insert(ProceduralDistributions.list.MechanicShelfWheels.items, 3)
table.insert(ProceduralDistributions.list.MechanicShelfWheels.items, "Base.ModernATVTire")
table.insert(ProceduralDistributions.list.MechanicShelfWheels.items, 2)