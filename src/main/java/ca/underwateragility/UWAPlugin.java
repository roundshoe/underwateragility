/*
 * Copyright (c) 2024, roundshoe <https://github.com/roundshoe>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ca.underwateragility;

import com.google.inject.Provides;
import java.awt.event.KeyEvent;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.GroundObject;
import net.runelite.api.HintArrowType;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.Renderable;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.ui.overlay.infobox.Timer;
import net.runelite.client.util.GameEventManager;

@Singleton
@PluginDescriptor(
	name = "Underwater Agility",
	description = "A plugin for Underwater Agility.",
	tags = {"under", "water", "underwater", "agility", "fossil", "island"},
	enabledByDefault = false
)
public class UWAPlugin extends Plugin implements KeyListener
{
	private static final Set<Integer> REGION_IDS = Set.of(15008, 15264);

	private static final Set<Integer> GAME_OBJECT_IDS_IGNORE = Set.of(
		16984, 30740, 30744, 30759, 30760, 30761, 30783, 30784, 30785, 30787, 30948,
		30957, 30959, 30962, 30963, 30964, 30965, 30966, 30969, 30970, 30971, 30972
	);
	private static final Set<Integer> GROUND_OBJECT_IDS_IGNORE = Set.of(7517);
	private static final Set<Integer> NPC_IDS_IGNORE = Set.of(7757, 7758, 7782, 7783, 7784, 7796, 8667);

	private static final int SCRIPT_ID_OXYGEN = 1997;
	private static final int WIDGET_GROUP_ID_WATER1 = 169;
	private static final int WIDGET_GROUP_ID_WATER2 = 170;
	private static final int WIDGET_GROUP_ID_OXYGEN = 609;
	private static final int WATER_WIDGET_DEFAULT_OPACITY = 140;

	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

	@Getter(AccessLevel.PACKAGE)
	private final List<GameObject> bubbles = new ArrayList<>();
	@Getter(AccessLevel.PACKAGE)
	private final List<GameObject> openChestClams = new ArrayList<>();
	@Getter(AccessLevel.PACKAGE)
	private final List<GameObject> obstacles = new ArrayList<>();
	@Getter(AccessLevel.PACKAGE)
	private final List<GameObject> currents = new ArrayList<>();
	@Getter(AccessLevel.PACKAGE)
	private final List<GameObject> holes = new ArrayList<>();
	@Getter(AccessLevel.PACKAGE)
	private final List<NPC> pufferFish = new ArrayList<>();

	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private UWAConfig config;
	@Inject
	private Hooks hooks;
	@Inject
	private InfoBoxManager infoBoxManager;
	@Inject
	private ItemManager itemManager;
	@Inject
	private KeyManager keyManager;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private UWAOverlay uwaOverlay;
	@Inject
	private UWALinesOverlay uwaLinesOverlay;
	@Inject
	private GameEventManager gameEventManager;

	@Nullable
	private WorldPoint hintArrowPoint;
	@Nullable
	private Timer timer;

	@Getter(AccessLevel.PACKAGE)
	private int oxygen;
	@Getter(AccessLevel.PACKAGE)
	private int oxygenTicks;

	@Getter(AccessLevel.PACKAGE)
	private boolean keyPressed;
	private boolean enabled;

	@Provides
	UWAConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(UWAConfig.class);
	}

	@Override
	public void startUp()
	{
		clientThread.invokeLater(() -> {
			if (client.getGameState() == GameState.LOGGED_IN && inRegion())
			{
				init();
			}
		});
	}

	private void init()
	{
		enabled = true;
		hooks.registerRenderableDrawListener(drawListener);
		keyManager.registerKeyListener(this);
		overlayManager.add(uwaOverlay);
		overlayManager.add(uwaLinesOverlay);
		updateOxygen();
		hideOxygenWidget(config.hideOxygenWidget());
		hideWaterWidget(config.hideWaterWidget());
		updateHintArrowPoint();
		gameEventManager.simulateGameEvents(this);
	}

	@Override
	public void shutDown()
	{
		enabled = false;
		hooks.unregisterRenderableDrawListener(drawListener);
		keyManager.unregisterKeyListener(this);
		overlayManager.remove(uwaOverlay);
		overlayManager.remove(uwaLinesOverlay);
		keyPressed = false;
		clearGameObjects();
		pufferFish.clear();
		removeTimer();
		oxygen = 0;
		oxygenTicks = 0;
		hideOxygenWidget(false);
		hideWaterWidget(false);
		setGameStateLoading();
	}

	private void clearGameObjects()
	{
		bubbles.clear();
		openChestClams.clear();
		obstacles.clear();
		currents.clear();
		holes.clear();
	}

	@Subscribe
	public void onConfigChanged(final ConfigChanged event)
	{
		if (!enabled || !event.getGroup().equals(UWAConfig.CONFIG_GROUP))
		{
			return;
		}

		final String key = event.getKey();

		switch (key)
		{
			case UWAConfig.CONFIG_KEY_HIDE_SCENERY:
				setGameStateLoading();
				break;
			case UWAConfig.CONFIG_KEY_HIDE_WATER_WIDGET:
				clientThread.invokeLater(() -> {
					if (client.getGameState() == GameState.LOGGED_IN)
					{
						hideWaterWidget(config.hideWaterWidget());
					}
				});
				break;
			case UWAConfig.CONFIG_KEY_HIDE_OXYGEN_WIDGET:
				clientThread.invokeLater(() -> {
					if (client.getGameState() == GameState.LOGGED_IN)
					{
						hideOxygenWidget(config.hideOxygenWidget());
					}
				});
				break;
			case UWAConfig.CONFIG_KEY_CHEST_CLAM_TIMER:
				removeTimer();
				break;
			default:
				break;
		}
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event)
	{
		final GameState gameState = event.getGameState();

		switch (gameState)
		{
			case LOGGED_IN:
				if (inRegion())
				{
					if (!enabled)
					{
						init();
					}
				}
				else
				{
					if (enabled)
					{
						shutDown();
					}
				}
				break;
			case LOADING:
				if (enabled)
				{
					clearGameObjects();
				}
				break;
			case LOGIN_SCREEN:
			case HOPPING:
				shutDown();
				break;
		}
	}

	@Subscribe
	public void onGameTick(final GameTick event)
	{
		if (!enabled)
		{
			return;
		}

		if (oxygen < 1000 && oxygenTicks > 0)
		{
			--oxygenTicks;
		}

		updateHintArrowPoint();
	}

	@Subscribe
	public void onNpcSpawned(final NpcSpawned event)
	{
		if (!enabled)
		{
			return;
		}

		final NPC npc = event.getNpc();

		if (npc.getId() == NpcID.PUFFER_FISH_8667)
		{
			pufferFish.add(npc);
		}
	}

	@Subscribe
	public void onNpcDespawned(final NpcDespawned event)
	{
		if (!enabled)
		{
			return;
		}

		final NPC npc = event.getNpc();

		if (npc.getId() == NpcID.PUFFER_FISH_8667)
		{
			pufferFish.remove(npc);
		}
	}

	@Subscribe
	public void onGameObjectSpawned(final GameObjectSpawned event)
	{
		if (!enabled)
		{
			return;
		}

		final GameObject gameObject = event.getGameObject();
		final int id = gameObject.getId();

		switch (id)
		{
			case ObjectID.CLAM_30969:
			case ObjectID.CHEST_30971:
				openChestClams.add(gameObject);
				break;
			case ObjectID.TUNNEL_30959:
			case ObjectID.OBSTACLE_30962:
			case ObjectID.OBSTACLE_30964:
				obstacles.add(gameObject);
				break;
			case ObjectID.CURRENT:
				currents.add(gameObject);
				break;
			case ObjectID.HOLE_30966:
				holes.add(gameObject);
				break;
			case NullObjectID.NULL_30957:
				bubbles.add(gameObject);
				break;
			default:
				break;
		}

		if (config.hideScenery() && !GAME_OBJECT_IDS_IGNORE.contains(id))
		{
			client.getTopLevelWorldView().getScene().removeGameObject(gameObject);
		}
	}

	@Subscribe
	public void onGameObjectDespawned(final GameObjectDespawned event)
	{
		if (!enabled)
		{
			return;
		}

		final GameObject gameObject = event.getGameObject();
		final int id = gameObject.getId();

		switch (id)
		{
			case ObjectID.CLAM_30969:
			case ObjectID.CHEST_30971:
				openChestClams.remove(gameObject);
				break;
			case ObjectID.TUNNEL_30959:
			case ObjectID.OBSTACLE_30962:
			case ObjectID.OBSTACLE_30964:
				obstacles.remove(gameObject);
				break;
			case ObjectID.CURRENT:
				currents.remove(gameObject);
				break;
			case ObjectID.HOLE_30966:
				holes.remove(gameObject);
				break;
			case NullObjectID.NULL_30957:
				bubbles.remove(gameObject);
				break;
			default:
				break;
		}
	}

	@Subscribe
	public void onGroundObjectSpawned(final GroundObjectSpawned event)
	{
		if (!enabled || !config.hideScenery())
		{
			return;
		}

		final GroundObject groundObject = event.getGroundObject();
		final int id = groundObject.getId();

		if (!GROUND_OBJECT_IDS_IGNORE.contains(id))
		{
			event.getTile().setGroundObject(null);
		}
	}

	@Subscribe
	public void onScriptPreFired(final ScriptPreFired event)
	{
		if (!enabled || event.getScriptId() != SCRIPT_ID_OXYGEN)
		{
			return;
		}

		updateOxygen();
	}

	@Subscribe
	public void onWidgetLoaded(final WidgetLoaded event)
	{
		if (!enabled)
		{
			return;
		}

		final int groupId = event.getGroupId();

		switch (groupId)
		{
			case WIDGET_GROUP_ID_WATER1:
			case WIDGET_GROUP_ID_WATER2:
				hideWaterWidget(config.hideWaterWidget());
				break;
			case WIDGET_GROUP_ID_OXYGEN:
				hideOxygenWidget(config.hideOxygenWidget());
				break;
			default:
				break;
		}
	}

	private boolean inRegion()
	{
		return Arrays.stream(client.getTopLevelWorldView().getMapRegions()).anyMatch(REGION_IDS::contains);
	}

	private void setGameStateLoading()
	{
		clientThread.invokeLater(() -> {
			if (client.getGameState() == GameState.LOGGED_IN)
			{
				client.setGameState(GameState.LOADING);
			}
		});
	}

	private void updateOxygen()
	{
		oxygen = client.getVarbitValue(Varbits.OXYGEN_LEVEL);
		oxygenTicks = (oxygen * 3) / 10;
	}

	private void hideWaterWidget(final boolean hidden)
	{
		Widget widget = client.getWidget(WIDGET_GROUP_ID_WATER1, 0);

		if (widget != null)
		{
			widget.setOpacity(hidden ? 255 : WATER_WIDGET_DEFAULT_OPACITY);
		}

		widget = client.getWidget(WIDGET_GROUP_ID_WATER2, 0);

		if (widget != null)
		{
			widget.setOpacity(hidden ? 255 : WATER_WIDGET_DEFAULT_OPACITY);
		}
	}

	private void hideOxygenWidget(final boolean hidden)
	{
		for (int i = 3; i <= 6; ++i)
		{
			final Widget widget = client.getWidget(WIDGET_GROUP_ID_OXYGEN, i);

			if (widget != null)
			{
				widget.setOpacity(hidden ? 255 : 0);
			}
		}
	}

	private void updateHintArrowPoint()
	{
		if (!config.chestClamTimer() || client.getHintArrowType() != HintArrowType.COORDINATE)
		{
			return;
		}

		final WorldPoint worldPoint = client.getHintArrowPoint();

		if (worldPoint == null)
		{
			removeTimer();
			return;
		}

		if (worldPoint.equals(hintArrowPoint))
		{
			return;
		}

		removeTimer();
		timer = new Timer(60, ChronoUnit.SECONDS, itemManager.getImage(21656), this);
		infoBoxManager.addInfoBox(timer);
		hintArrowPoint = worldPoint;
	}

	private void removeTimer()
	{
		infoBoxManager.removeInfoBox(timer);
		timer = null;
		hintArrowPoint = null;
	}

	private boolean shouldDraw(final Renderable renderable, final boolean drawingUI)
	{
		if (config.hideScenery())
		{
			if (renderable instanceof NPC)
			{
				final NPC npc = (NPC) renderable;
				final int id = npc.getId();

				return NPC_IDS_IGNORE.contains(id);
			}
		}

		return true;
	}

	@Override
	public void keyTyped(final KeyEvent e)
	{
	}

	@Override
	public void keyPressed(final KeyEvent e)
	{
		if (enabled && config.holeLinesKey().matches(e))
		{
			keyPressed = true;
		}
	}

	@Override
	public void keyReleased(final KeyEvent e)
	{
		if (enabled && config.holeLinesKey().matches(e))
		{
			keyPressed = false;
		}
	}
}
