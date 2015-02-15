package amerifrance.guideapi.gui;

import amerifrance.guideapi.buttons.ButtonNext;
import amerifrance.guideapi.buttons.ButtonPrev;
import amerifrance.guideapi.objects.Book;
import amerifrance.guideapi.objects.abstraction.AbstractCategory;
import amerifrance.guideapi.wrappers.CategoryWrapper;
import com.google.common.collect.HashMultimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class GuiHome extends GuiBase {

    public ResourceLocation outlineTexture;
    public ResourceLocation pageTexture;
    public Book book;
    public HashMultimap<Integer, CategoryWrapper> categoryWrappers;
    public ButtonNext buttonNext;
    public ButtonPrev buttonPrev;
    private int categoryPage;

    public GuiHome(Book book, EntityPlayer player, ItemStack bookStack) {
        super(player, bookStack);
        this.book = book;
        this.pageTexture = book.pageTexture();
        this.outlineTexture = book.outlineTexture();
        this.categoryPage = 0;
        this.categoryWrappers = this.categoryWrappers.create();
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        this.categoryWrappers.clear();

        guiLeft = (this.width - this.xSize) / 2;
        guiTop = (this.height - this.ySize) / 2;

        this.buttonList.add(buttonNext = new ButtonNext(0, guiLeft + 4 * xSize / 6, guiTop + 5 * ySize / 6, this));
        this.buttonList.add(buttonPrev = new ButtonPrev(1, guiLeft + xSize / 5, guiTop + 5 * ySize / 6, this));

        int cX = guiLeft;
        int cY = guiTop + 15;
        boolean drawOnLeft = true;
        int i = 0;
        int pageNumber = 0;

        for (AbstractCategory category : book.categories()) {
            if (drawOnLeft) {
                categoryWrappers.put(pageNumber, new CategoryWrapper(book, category, cX, cY, 15, 15, player, this.fontRendererObj, this.itemRender, drawOnLeft, bookStack));
                cX = guiLeft + 180;
                drawOnLeft = false;
            } else {
                categoryWrappers.put(pageNumber, new CategoryWrapper(book, category, cX, cY, 15, 15, player, this.fontRendererObj, this.itemRender, drawOnLeft, bookStack));
                cY += 25;
                cX = guiLeft;
                drawOnLeft = true;
            }
            i++;

            if (i >= 12) {
                i = 0;
                cX = guiLeft;
                cY = guiTop + 15;
                pageNumber++;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float renderPartialTicks) {
        for (CategoryWrapper wrapper : this.categoryWrappers.get(categoryPage)) {
            if (wrapper.canPlayerSee()) wrapper.draw(mouseX, mouseY, this);
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(pageTexture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        Minecraft.getMinecraft().getTextureManager().bindTexture(outlineTexture);
        drawTexturedModalRectWithColor(guiLeft, guiTop, 0, 0, xSize, ySize, book.color());
        drawSplitString(book.getLocalizedWelcomeMessage(), guiLeft + 37, guiTop + 12, 4 * xSize / 6, 0);

        for (CategoryWrapper wrapper : this.categoryWrappers.get(categoryPage)) {
            if (wrapper.canPlayerSee()) wrapper.drawExtras(mouseX, mouseY, this);
        }

        drawCenteredString(fontRendererObj, String.valueOf(categoryPage + 1) + "/" + String.valueOf(categoryWrappers.asMap().size()), guiLeft + xSize / 2, guiTop + 5 * ySize / 6, 0);
        super.drawScreen(mouseX, mouseY, renderPartialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int typeofClick) {
        super.mouseClicked(mouseX, mouseY, typeofClick);

        for (CategoryWrapper wrapper : this.categoryWrappers.get(categoryPage)) {
            if (wrapper.isMouseOnWrapper(mouseX, mouseY) && wrapper.canPlayerSee()) {
                if (typeofClick == 0) wrapper.category.onLeftClicked(book, mouseX, mouseY, player, bookStack);
                else if (typeofClick == 1) wrapper.category.onRightClicked(book, mouseX, mouseY, player, bookStack);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
        if ((keyCode == Keyboard.KEY_UP || keyCode == Keyboard.KEY_RIGHT) && categoryPage + 1 < categoryWrappers.asMap().size()) {
            this.categoryPage++;
        }
        if ((keyCode == Keyboard.KEY_DOWN || keyCode == Keyboard.KEY_LEFT) && categoryPage > 0) {
            this.categoryPage--;
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 0 && categoryPage + 1 < categoryWrappers.asMap().size()) {
            this.categoryPage++;
        } else if (button.id == 1 && categoryPage > 0) {
            this.categoryPage--;
        }
    }
}
