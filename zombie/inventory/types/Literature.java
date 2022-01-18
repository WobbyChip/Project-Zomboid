// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory.types;

import zombie.util.io.BitHeaderRead;
import java.io.IOException;
import zombie.util.io.BitHeaderWrite;
import zombie.GameWindow;
import zombie.util.io.BitHeader;
import java.nio.ByteBuffer;
import zombie.characters.traits.TraitCollection;
import java.util.Iterator;
import zombie.characters.traits.TraitFactory;
import zombie.characters.professions.ProfessionFactory;
import java.util.Collection;
import zombie.core.Translator;
import zombie.ui.ObjectTooltip;
import zombie.scripting.objects.Item;
import zombie.inventory.ItemType;
import java.util.List;
import java.util.HashMap;
import zombie.inventory.InventoryItem;

public final class Literature extends InventoryItem
{
    public boolean bAlreadyRead;
    public String requireInHandOrInventory;
    public String useOnConsume;
    private int numberOfPages;
    private String bookName;
    private int LvlSkillTrained;
    private int NumLevelsTrained;
    private String SkillTrained;
    private int alreadyReadPages;
    private boolean canBeWrite;
    private HashMap<Integer, String> customPages;
    private String lockedBy;
    private int pageToWrite;
    private List<String> teachedRecipes;
    
    public Literature(final String s, final String bookName, final String s2, final String s3) {
        super(s, bookName, s2, s3);
        this.bAlreadyRead = false;
        this.requireInHandOrInventory = null;
        this.useOnConsume = null;
        this.numberOfPages = -1;
        this.bookName = "";
        this.LvlSkillTrained = -1;
        this.SkillTrained = "None";
        this.alreadyReadPages = 0;
        this.canBeWrite = false;
        this.customPages = null;
        this.lockedBy = null;
        this.teachedRecipes = null;
        this.setBookName(bookName);
        this.cat = ItemType.Literature;
        if (this.staticModel == null) {
            this.staticModel = "Book";
        }
    }
    
    public Literature(final String s, final String bookName, final String s2, final Item item) {
        super(s, bookName, s2, item);
        this.bAlreadyRead = false;
        this.requireInHandOrInventory = null;
        this.useOnConsume = null;
        this.numberOfPages = -1;
        this.bookName = "";
        this.LvlSkillTrained = -1;
        this.SkillTrained = "None";
        this.alreadyReadPages = 0;
        this.canBeWrite = false;
        this.customPages = null;
        this.lockedBy = null;
        this.teachedRecipes = null;
        this.setBookName(bookName);
        this.cat = ItemType.Literature;
        if (this.staticModel == null) {
            this.staticModel = "Book";
        }
    }
    
    @Override
    public boolean IsLiterature() {
        return true;
    }
    
    @Override
    public int getSaveType() {
        return Item.Type.Literature.ordinal();
    }
    
    @Override
    public String getCategory() {
        if (this.mainCategory != null) {
            return this.mainCategory;
        }
        return "Literature";
    }
    
    @Override
    public void update() {
        if (this.container != null) {}
    }
    
    @Override
    public boolean finishupdate() {
        return true;
    }
    
    @Override
    public void DoTooltip(final ObjectTooltip objectTooltip, final ObjectTooltip.Layout layout) {
        if (this.getBoredomChange() != 0.0f) {
            final ObjectTooltip.LayoutItem addItem = layout.addItem();
            final int n = (int)this.getBoredomChange();
            addItem.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_literature_Boredom_Reduction")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem.setValueRight(n, false);
        }
        if (this.getStressChange() != 0.0f) {
            final ObjectTooltip.LayoutItem addItem2 = layout.addItem();
            final int n2 = (int)(this.getStressChange() * 100.0f);
            addItem2.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_literature_Stress_Reduction")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem2.setValueRight(n2, false);
        }
        if (this.getUnhappyChange() != 0.0f) {
            final ObjectTooltip.LayoutItem addItem3 = layout.addItem();
            final int n3 = (int)this.getUnhappyChange();
            addItem3.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_food_Unhappiness")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem3.setValueRight(n3, false);
        }
        if (this.getNumberOfPages() != -1) {
            final ObjectTooltip.LayoutItem addItem4 = layout.addItem();
            int n4 = this.getAlreadyReadPages();
            if (objectTooltip.getCharacter() != null) {
                n4 = objectTooltip.getCharacter().getAlreadyReadPages(this.getFullType());
            }
            addItem4.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_literature_Number_of_Pages")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem4.setValue(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, n4, this.getNumberOfPages()), 1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (this.getLvlSkillTrained() != -1) {
            final ObjectTooltip.LayoutItem addItem5 = layout.addItem();
            String s = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getLvlSkillTrained());
            if (this.getLvlSkillTrained() != this.getMaxLevelTrained()) {
                s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, this.getMaxLevelTrained());
            }
            addItem5.setLabel(Translator.getText("Tooltip_Literature_XpMultiplier", s), 1.0f, 1.0f, 0.8f, 1.0f);
        }
        if (this.getTeachedRecipes() != null) {
            final Iterator<String> iterator = this.getTeachedRecipes().iterator();
            while (iterator.hasNext()) {
                layout.addItem().setLabel(Translator.getText("Tooltip_Literature_TeachedRecipes", Translator.getRecipeName(iterator.next())), 1.0f, 1.0f, 0.8f, 1.0f);
            }
            if (objectTooltip.getCharacter() != null) {
                final ObjectTooltip.LayoutItem addItem6 = layout.addItem();
                String s2 = Translator.getText("Tooltip_literature_NotBeenRead");
                if (objectTooltip.getCharacter().getKnownRecipes().containsAll(this.getTeachedRecipes())) {
                    s2 = Translator.getText("Tooltip_literature_HasBeenRead");
                }
                addItem6.setLabel(s2, 1.0f, 1.0f, 0.8f, 1.0f);
                if (objectTooltip.getCharacter().getKnownRecipes().containsAll(this.getTeachedRecipes())) {
                    final ProfessionFactory.Profession profession = ProfessionFactory.getProfession(objectTooltip.getCharacter().getDescriptor().getProfession());
                    final TraitCollection traits = objectTooltip.getCharacter().getTraits();
                    int n5 = 0;
                    int n6 = 0;
                    for (int i = 0; i < this.getTeachedRecipes().size(); ++i) {
                        final String s3 = this.getTeachedRecipes().get(i);
                        if (profession != null && profession.getFreeRecipes().contains(s3)) {
                            ++n5;
                        }
                        for (int j = 0; j < traits.size(); ++j) {
                            final TraitFactory.Trait trait = TraitFactory.getTrait(traits.get(j));
                            if (trait != null && trait.getFreeRecipes().contains(s3)) {
                                ++n6;
                            }
                        }
                    }
                    if (n5 > 0 || n6 > 0) {
                        layout.addItem().setLabel(Translator.getText("Tooltip_literature_AlreadyKnown"), 0.0f, 1.0f, 0.8f, 1.0f);
                    }
                }
            }
        }
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        final BitHeaderWrite allocWrite = BitHeader.allocWrite(BitHeader.HeaderSize.Byte, byteBuffer);
        int n = 0;
        if (this.numberOfPages >= 127 && this.numberOfPages < 32767) {
            n = 1;
        }
        else if (this.numberOfPages >= 32767) {
            n = 2;
        }
        if (this.numberOfPages != -1) {
            allocWrite.addFlags(1);
            if (n == 1) {
                allocWrite.addFlags(2);
                byteBuffer.putShort((short)this.numberOfPages);
            }
            else if (n == 2) {
                allocWrite.addFlags(4);
                byteBuffer.putInt(this.numberOfPages);
            }
            else {
                byteBuffer.put((byte)this.numberOfPages);
            }
        }
        if (this.alreadyReadPages != 0) {
            allocWrite.addFlags(8);
            if (n == 1) {
                byteBuffer.putShort((short)this.alreadyReadPages);
            }
            else if (n == 2) {
                byteBuffer.putInt(this.alreadyReadPages);
            }
            else {
                byteBuffer.put((byte)this.alreadyReadPages);
            }
        }
        if (this.canBeWrite) {
            allocWrite.addFlags(16);
        }
        if (this.customPages != null && this.customPages.size() > 0) {
            allocWrite.addFlags(32);
            byteBuffer.putInt(this.customPages.size());
            final Iterator<String> iterator = this.customPages.values().iterator();
            while (iterator.hasNext()) {
                GameWindow.WriteString(byteBuffer, iterator.next());
            }
        }
        if (this.lockedBy != null) {
            allocWrite.addFlags(64);
            GameWindow.WriteString(byteBuffer, this.getLockedBy());
        }
        allocWrite.write();
        allocWrite.release();
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        super.load(byteBuffer, n);
        final BitHeaderRead allocRead = BitHeader.allocRead(BitHeader.HeaderSize.Byte, byteBuffer);
        if (!allocRead.equals(0)) {
            int n2 = 0;
            if (allocRead.hasFlags(1)) {
                if (allocRead.hasFlags(2)) {
                    n2 = 1;
                    this.numberOfPages = byteBuffer.getShort();
                }
                else if (allocRead.hasFlags(4)) {
                    n2 = 2;
                    this.numberOfPages = byteBuffer.getInt();
                }
                else {
                    this.numberOfPages = byteBuffer.get();
                }
            }
            if (allocRead.hasFlags(8)) {
                if (n2 == 1) {
                    this.alreadyReadPages = byteBuffer.getShort();
                }
                else if (n2 == 2) {
                    this.alreadyReadPages = byteBuffer.getInt();
                }
                else {
                    this.alreadyReadPages = byteBuffer.get();
                }
            }
            this.canBeWrite = allocRead.hasFlags(16);
            if (allocRead.hasFlags(32)) {
                final int int1 = byteBuffer.getInt();
                if (int1 > 0) {
                    this.customPages = new HashMap<Integer, String>();
                    for (int i = 0; i < int1; ++i) {
                        this.customPages.put(i + 1, GameWindow.ReadString(byteBuffer));
                    }
                }
            }
            if (allocRead.hasFlags(64)) {
                this.setLockedBy(GameWindow.ReadString(byteBuffer));
            }
        }
        allocRead.release();
    }
    
    @Override
    public float getBoredomChange() {
        if (!this.bAlreadyRead) {
            return this.boredomChange;
        }
        return 0.0f;
    }
    
    @Override
    public float getUnhappyChange() {
        if (!this.bAlreadyRead) {
            return this.unhappyChange;
        }
        return 0.0f;
    }
    
    @Override
    public float getStressChange() {
        if (!this.bAlreadyRead) {
            return this.stressChange;
        }
        return 0.0f;
    }
    
    public int getNumberOfPages() {
        return this.numberOfPages;
    }
    
    public void setNumberOfPages(final int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }
    
    public String getBookName() {
        return this.bookName;
    }
    
    public void setBookName(final String bookName) {
        this.bookName = bookName;
    }
    
    public int getLvlSkillTrained() {
        return this.LvlSkillTrained;
    }
    
    public void setLvlSkillTrained(final int lvlSkillTrained) {
        this.LvlSkillTrained = lvlSkillTrained;
    }
    
    public int getNumLevelsTrained() {
        return this.NumLevelsTrained;
    }
    
    public void setNumLevelsTrained(final int numLevelsTrained) {
        this.NumLevelsTrained = numLevelsTrained;
    }
    
    public int getMaxLevelTrained() {
        return this.getLvlSkillTrained() + this.getNumLevelsTrained() - 1;
    }
    
    public String getSkillTrained() {
        return this.SkillTrained;
    }
    
    public void setSkillTrained(final String skillTrained) {
        this.SkillTrained = skillTrained;
    }
    
    public int getAlreadyReadPages() {
        return this.alreadyReadPages;
    }
    
    public void setAlreadyReadPages(final int alreadyReadPages) {
        this.alreadyReadPages = alreadyReadPages;
    }
    
    public boolean canBeWrite() {
        return this.canBeWrite;
    }
    
    public void setCanBeWrite(final boolean canBeWrite) {
        this.canBeWrite = canBeWrite;
    }
    
    public HashMap<Integer, String> getCustomPages() {
        if (this.customPages == null) {
            (this.customPages = new HashMap<Integer, String>()).put(1, "");
        }
        return this.customPages;
    }
    
    public void setCustomPages(final HashMap<Integer, String> customPages) {
        this.customPages = customPages;
    }
    
    public void addPage(final Integer key, final String value) {
        if (this.customPages == null) {
            this.customPages = new HashMap<Integer, String>();
        }
        this.customPages.put(key, value);
    }
    
    public String seePage(final Integer key) {
        if (this.customPages == null) {
            (this.customPages = new HashMap<Integer, String>()).put(1, "");
        }
        return this.customPages.get(key);
    }
    
    public String getLockedBy() {
        return this.lockedBy;
    }
    
    public void setLockedBy(final String lockedBy) {
        this.lockedBy = lockedBy;
    }
    
    public int getPageToWrite() {
        return this.pageToWrite;
    }
    
    public void setPageToWrite(final int pageToWrite) {
        this.pageToWrite = pageToWrite;
    }
    
    public List<String> getTeachedRecipes() {
        return this.teachedRecipes;
    }
    
    public void setTeachedRecipes(final List<String> teachedRecipes) {
        this.teachedRecipes = teachedRecipes;
    }
}
