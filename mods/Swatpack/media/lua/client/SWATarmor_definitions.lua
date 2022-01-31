--Body Parts List --> Foot_L, Foot_R, ForeArm_L, ForeArm_R, Groin, Hand_L, Hand_R, Head, LowerLeg_L, LowerLeg_R, MAX, Neck, Torso_Lower, Torso_Upper, UpperArm_L, UpperArm_R, UpperLeg_L, UpperLeg_R 
--Available Res Types list --> ScratchRes|DeepWoundRes|BurnRes|BulletRes|FractureRes|GlassRes|BiteRes

if(ItemValueTable == nil) then ItemValueTable = {}; end
ItemValueTable["Armor.RiotShieldSwat"] = 10.00;
ItemValueTable["Armor.RiotShieldPolice"] = 8.00;
if(GlobalArmor == nil) then GlobalArmor = {} end

local WorkingArmor;
	
	
-- Shields
		
	WorkingArmor = "RiotShieldSwat";
	GlobalArmor[WorkingArmor] = {};
		GlobalArmor[WorkingArmor]["ScratchRes"] = {Hand_L = 100, ForeArm_L = 100, UpperArm_L = 100, Torso_Upper = 100, Torso_Lower = 100, Groin = 100, UpperLeg_L = 100, LowerLeg_L = 100};
		GlobalArmor[WorkingArmor]["DeepWoundRes"] = {Hand_L = 100, ForeArm_L = 100, UpperArm_L = 100, Torso_Upper = 100, Torso_Lower = 100, Groin = 100, UpperLeg_L = 100, LowerLeg_L = 100};	
		GlobalArmor[WorkingArmor]["BiteRes"] = {Hand_L = 100, ForeArm_L = 100, UpperArm_L = 100, Torso_Upper = 100, Torso_Lower = 100, Groin = 100, UpperLeg_L = 100, LowerLeg_L = 100};
		GlobalArmor[WorkingArmor]["BulletRes"] = {Hand_L = 100, ForeArm_L = 100, UpperArm_L = 100, Torso_Upper = 100, Torso_Lower = 100, Groin = 100, UpperLeg_L = 100, LowerLeg_L = 100};			
		GlobalArmor[WorkingArmor]["Durability"] = 80;
		GlobalArmor[WorkingArmor]["Location"] = "Shield";
	
		
	WorkingArmor = "RiotShieldPolice";
	GlobalArmor[WorkingArmor] = {};
		GlobalArmor[WorkingArmor]["ScratchRes"] = {Hand_L = 80, ForeArm_L = 80, UpperArm_L = 80, Torso_Upper = 80, Torso_Lower = 80, Groin = 80};
		GlobalArmor[WorkingArmor]["DeepWoundRes"] = {Hand_L = 80, ForeArm_L = 80, UpperArm_L = 80, Torso_Upper = 80, Torso_Lower = 80, Groin = 80};	
		GlobalArmor[WorkingArmor]["BiteRes"] = {Hand_L = 80, ForeArm_L = 80, UpperArm_L = 80, Torso_Upper = 80, Torso_Lower = 80, Groin = 80};			
		GlobalArmor[WorkingArmor]["Durability"] = 35;
		GlobalArmor[WorkingArmor]["Location"] = "Shield";
		

