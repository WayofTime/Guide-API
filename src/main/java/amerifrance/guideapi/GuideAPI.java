package amerifrance.guideapi;

import amerifrance.guideapi.items.ItemsRegistry;
import amerifrance.guideapi.proxies.CommonProxy;
import amerifrance.guideapi.util.EventHandler;
import amerifrance.guideapi.util.serialization.BookCreator;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;

@Mod(modid = ModInformation.ID, name = ModInformation.NAME, version = ModInformation.VERSION, dependencies = ModInformation.DEPEND)
public class GuideAPI {

    public static CreativeTabs tabGuide = new CreativeTabs(ModInformation.ID + ".creativeTab") {
        @Override
        public ItemStack getIconItemStack() {
            return new ItemStack(Items.book);
        }

        @Override
        public Item getTabIconItem() {
            return Items.book;
        }
    };

    @Mod.Instance
    public static GuideAPI instance;
    @SidedProxy(clientSide = ModInformation.CLIENTPROXY, serverSide = ModInformation.COMMONPROXY)
    public static CommonProxy proxy;

    private static File configDir;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
        configDir = new File(event.getModConfigurationDirectory() + "/" + ModInformation.NAME);
        configDir.mkdirs();
        ConfigHandler.init(new File(configDir.getPath() + "/Guide-API.cfg"));
        ItemsRegistry.registerItems();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

        TestBook.testBook();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        BookCreator.registerCustomSerializers(gsonBuilder);
        BookCreator.registerJsonBooks(gsonBuilder);
    }

    public static File getConfigDir() {
        return configDir;
    }
}
