package best_route_mod;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.interfaces.PostInitializeSubscriber;
import best_route_mod.patches.InputHelperPatch;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.NeowsLament;

import java.io.IOException;
import java.util.*;

@SpireInitializer
public class BestRouteMod implements PostInitializeSubscriber
{
	public static SpireConfig config;
    public BestRouteMod() {
        System.out.println("Best Route Mod initialized. Enjoy! -MMagicala");
    }

    public static void initialize() {
		BaseMod.subscribe(new BestRouteMod());
    }

    // Explicitly renders path from a node (when currMapNode isn't updated or for other reasons)
    public static void reRenderPath(MapRoomNode node, boolean neowsLamentCounterNotUpdated){
        ArrayList<MapRoomNode> nodes = new ArrayList<>();
        nodes.add(node);
        reRenderPath(nodes, neowsLamentCounterNotUpdated);
    }

    public static void reRenderPath(ArrayList<MapRoomNode> nodes){
        reRenderPath(nodes, false);
    }

    // Add an option for neow's lament counter since we might have to manually send the updated number
    public static void reRenderPath(ArrayList<MapRoomNode> nodes, boolean neowsLamentCounterNotUpdated){
        ColorPathManager.disableCurrentlyColoredPath();
        if(RoomClassManager.allRoomClassesInActive()){
            return;
        }
        int neowsLamentCounter = getNeowsLamentCounter();
        if(neowsLamentCounterNotUpdated && --neowsLamentCounter == 0){
        	neowsLamentCounter = -1;
		}
        MapPath bestPath = MapReader.getBestPathFrom(nodes, InputHelperPatch.isEmeraldKeyRequired, neowsLamentCounter, true);
        ColorPathManager.colorPath(bestPath, neowsLamentCounter != -1);
    }

    // Renders a path decisively from currMapNode or starting nodes
    public static void autoReRenderPath(){
        if(AbstractDungeon.firstRoomChosen) reRenderPath(AbstractDungeon.currMapNode, false);
        else reRenderPath(MapReader.getStartingNodes(), false);
    }

    public static int getNeowsLamentCounter(){
        return InputHelperPatch.isNeowsLamentFactored ? AbstractDungeon.player.getRelic(NeowsLament.ID).counter : -1;
    }

    HashMap<String, String> propertyLabelPairs = new HashMap<String, String>(){
		{
			put("neowsLamentFactoredAutomatically","Factor Neow's Lament automatically when receiving the relic");
			put("emeraldFactoredAutomatically", "Factor emerald automatically when loading into a game");
		}
	};
    // SpireConfig
	@Override
	public void receivePostInitialize()
	{
		// default properties
		Properties properties = new Properties();
		String[] propertyKeys = new String[propertyLabelPairs.keySet().size()];
		propertyLabelPairs.keySet().toArray(propertyKeys);
		for(int i = 0; i < propertyLabelPairs.size(); i++){
			properties.setProperty(propertyKeys[i], Boolean.toString(false));
		}

		try
		{
			config = new SpireConfig("BestRouteMod", "config", properties);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		// create mod settings panel
		int xPos = 350, yPos = 700;
		ModPanel settingsPanel = new ModPanel();

		// create buttons
		String[] labelValues = new String[propertyLabelPairs.values().size()];
		propertyLabelPairs.values().toArray(labelValues);
		for(int i = 0; i < 2; i++){
			int finalI = i;
			ModLabeledToggleButton toggleButton = new ModLabeledToggleButton(labelValues[i], xPos, yPos, Settings.CREAM_COLOR,
				FontHelper.charDescFont, config.getBool(propertyKeys[i]), settingsPanel, l -> {}
				, button -> {
					// On click event, update the key
					config.setBool(propertyKeys[finalI], button.enabled);
					try
					{
						config.save();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			);
			settingsPanel.addUIElement(toggleButton);
			yPos -= 50;
		}

		// Finally register mod
		BaseMod.registerModBadge(ImageMaster.loadImage("badge.jpg"), "Best Route Mod", "MMagicala", "Find the best route in the map!", settingsPanel);
	}
}