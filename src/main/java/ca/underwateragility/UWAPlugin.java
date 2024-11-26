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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.GroundObject;
import net.runelite.api.HintArrowType;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.Renderable;
import net.runelite.api.TileObject;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
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
	tags = {"under", "water", "underwater", "agility", "fossil", "island"}
)
public class UWAPlugin extends Plugin implements KeyListener
{
	private static final Set<Integer> REGION_IDS = Set.of(15008, 15264);

	private static final Set<Integer> GAME_OBJECT_IDS_IGNORE = Set.of(
		16984, 30500, 30501, 30734, 30735, 30738, 30740, 30741, 30742, 30743,
		30744, 30759, 30760, 30761, 30762, 30783, 30784, 30785, 30787, 30947,
		30948, 30957, 30958, 30959, 30962, 30963, 30964, 30965, 30966, 30969,
		30970, 30971, 30972, 30987, 31433, 31434, 31435, 31843
	);
	private static final Set<Integer> GROUND_OBJECT_IDS_IGNORE = Collections.singleton(7517);
	private static final Set<Integer> NPC_IDS_IGNORE = Set.of(7757, 7758, 7782, 7783, 7784, 7796, 8667);

	private static final Set<WorldPoint> WORLD_POINTS_BUBBLE_CHESTS = Set.of(
		new WorldPoint(3742, 10242, 1), new WorldPoint(3744, 10242, 1),
		new WorldPoint(3751, 10267, 1), new WorldPoint(3752, 10269, 1),
		new WorldPoint(3753, 10266, 1), new WorldPoint(3760, 10259, 1),
		new WorldPoint(3766, 10264, 1), new WorldPoint(3777, 10279, 1),
		new WorldPoint(3779, 10274, 1), new WorldPoint(3782, 10254, 1),
		new WorldPoint(3782, 10256, 1), new WorldPoint(3785, 10266, 1),
		new WorldPoint(3787, 10254, 1), new WorldPoint(3791, 10255, 1),
		new WorldPoint(3804, 10265, 1), new WorldPoint(3811, 10261, 1),
		new WorldPoint(3813, 10249, 1), new WorldPoint(3814, 10270, 1),
		new WorldPoint(3815, 10285, 1), new WorldPoint(3816, 10267, 1),
		new WorldPoint(3817, 10284, 1), new WorldPoint(3818, 10270, 1),
		new WorldPoint(3821, 10248, 1), new WorldPoint(3833, 10243, 1));

	private static final int SCRIPT_ID_OXYGEN = 1997;
	private static final int WIDGET_GROUP_ID_WATER1 = 169;
	private static final int WIDGET_GROUP_ID_WATER2 = 170;
	private static final int WIDGET_GROUP_ID_OXYGEN = 609;
	private static final int WATER_WIDGET_DEFAULT_OPACITY = 140;
	private static final int TICK_COUNT_CHEST = 100;
	private static final int ANIMATION_ID_THIEVE_SUCCESS = 7709;
	private static final int ANIMATION_ID_THIEVE_FAIL = 7710;

	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

	@Getter(AccessLevel.PACKAGE)
	private final List<TileObject> bubbles = new ArrayList<>();
	@Getter(AccessLevel.PACKAGE)
	private final List<TileObject> chestClams = new ArrayList<>();
	@Getter(AccessLevel.PACKAGE)
	private final List<TileObject> obstacles = new ArrayList<>();
	@Getter(AccessLevel.PACKAGE)
	private final List<GameObject> currents = new ArrayList<>();
	@Getter(AccessLevel.PACKAGE)
	private final List<TileObject> holes = new ArrayList<>();
	@Getter(AccessLevel.PACKAGE)
	private final List<NPC> pufferFish = new ArrayList<>();
	@Getter(AccessLevel.PACKAGE)
	private final List<WorldPoint> chestClamLines = new ArrayList<>();

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
	private UWASceneOverlay uwaSceneOverlay;
	@Inject
	private UWAMinimapOverlay uwaMinimapOverlay;
	@Inject
	private UWAInformationOverlayPanel uwaInformationOverlayPanel;
	@Inject
	private GameEventManager gameEventManager;

	@Nullable
	@Getter(AccessLevel.PACKAGE)
	private WorldPoint targetWorldPoint;
	@Nullable
	private Timer timer;

	@Nullable
	@Getter(AccessLevel.PACKAGE)
	private Instant startTime;

	@Getter(AccessLevel.PACKAGE)
	private int thieveSuccessCount;
	@Getter(AccessLevel.PACKAGE)
	private int thieveFailCount;
	@Getter(AccessLevel.PACKAGE)
	private int distanceToTarget = -1;
	@Getter(AccessLevel.PACKAGE)
	private int ticksRemaining = -1;
	@Getter(AccessLevel.PACKAGE)
	private int oxygen;
	@Getter(AccessLevel.PACKAGE)
	private int oxygenTicks;

	@Getter(AccessLevel.PACKAGE)
	private boolean bubbleNearby;
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

		overlayManager.add(uwaSceneOverlay);
		overlayManager.add(uwaMinimapOverlay);
		overlayManager.add(uwaInformationOverlayPanel);

		hideOxygenWidget(config.hideOxygenWidget());
		hideWaterWidget(config.hideWaterWidget());

		gameEventManager.simulateGameEvents(this);
	}

	@Override
	public void shutDown()
	{
		enabled = false;

		hooks.unregisterRenderableDrawListener(drawListener);
		keyManager.unregisterKeyListener(this);

		overlayManager.remove(uwaSceneOverlay);
		overlayManager.remove(uwaMinimapOverlay);
		overlayManager.remove(uwaInformationOverlayPanel);

		clearObjects();
		pufferFish.clear();
		chestClamLines.clear();

		targetWorldPoint = null;

		bubbleNearby = false;
		keyPressed = false;

		resetTears();

		distanceToTarget = -1;
		ticksRemaining = -1;
		oxygen = 0;
		oxygenTicks = 0;

		removeTimer();

		hideOxygenWidget(false);
		hideWaterWidget(false);

		setGameStateLoading();
	}

	private void clearObjects()
	{
		bubbles.clear();
		chestClams.clear();
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
				hideWaterWidget(config.hideWaterWidget());
				break;
			case UWAConfig.CONFIG_KEY_HIDE_OXYGEN_WIDGET:
				hideOxygenWidget(config.hideOxygenWidget());
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
					clearObjects();
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

		if (ticksRemaining >= 0)
		{
			--ticksRemaining;
		}

		updateTargetWorldPoint();
		updateDistanceToTarget();
		updateChestClamLines();
		updatePlayerAnimations();
	}

	@Subscribe
	public void onAnimationChanged(final AnimationChanged event)
	{
		if (!enabled)
		{
			return;
		}

		final Actor actor = event.getActor();

		if (actor != client.getLocalPlayer())
		{
			return;
		}

		final int id = actor.getAnimation();

		if (id == ANIMATION_ID_THIEVE_SUCCESS)
		{
			if (thieveSuccessCount++ == 0)
			{
				startTime = Instant.now();
			}
		}
		else if (id == ANIMATION_ID_THIEVE_FAIL)
		{
			++thieveFailCount;
		}
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
				chestClams.add(gameObject);
				break;
			case ObjectID.TUNNEL_30959:
			case ObjectID.OBSTACLE_30962:
			case ObjectID.OBSTACLE_30964:
				obstacles.add(gameObject);
				break;
			case ObjectID.CURRENT:
			case ObjectID.PLANT_30779:
			case ObjectID.PLANT_30780:
			case ObjectID.PLANT_30781:
			case ObjectID.PLANT_30782:
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
				chestClams.remove(gameObject);
				break;
			case ObjectID.TUNNEL_30959:
			case ObjectID.OBSTACLE_30962:
			case ObjectID.OBSTACLE_30964:
				obstacles.remove(gameObject);
				break;
			case ObjectID.CURRENT:
			case ObjectID.PLANT_30779:
			case ObjectID.PLANT_30780:
			case ObjectID.PLANT_30781:
			case ObjectID.PLANT_30782:
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

	private void updateTargetWorldPoint()
	{
		if (client.getHintArrowType() != HintArrowType.COORDINATE)
		{
			targetWorldPoint = null;
			return;
		}

		final WorldPoint worldPoint = client.getHintArrowPoint();

		if (worldPoint == null)
		{
			targetWorldPoint = null;
			return;
		}

		if (worldPoint.equals(targetWorldPoint))
		{
			return;
		}

		targetWorldPoint = worldPoint;

		updateTimer();
		updateTicksRemaining();
		updateBubbleAdjacent(worldPoint);
	}

	private void updateDistanceToTarget()
	{
		if (targetWorldPoint != null)
		{
			distanceToTarget = client.getLocalPlayer().getWorldLocation().distanceTo2D(targetWorldPoint);
		}
	}

	private void removeTimer()
	{
		infoBoxManager.removeInfoBox(timer);
		timer = null;
	}

	private void updateTimer()
	{
		removeTimer();

		if (config.chestClamTimer())
		{
			timer = new Timer((long) (TICK_COUNT_CHEST * 0.6), ChronoUnit.SECONDS, itemManager.getImage(ItemID.MERMAIDS_TEAR, 3, false), this);
			timer.setTooltip("Underwater Agility");
			infoBoxManager.addInfoBox(timer);
		}
	}

	private void updateTicksRemaining()
	{
		ticksRemaining = TICK_COUNT_CHEST;
	}

	private void updateBubbleAdjacent(final WorldPoint target)
	{
		for (final var wp : WORLD_POINTS_BUBBLE_CHESTS)
		{
			if (wp.getX() == target.getX() && wp.getY() == target.getY())
			{
				bubbleNearby = true;
				return;
			}
		}

		bubbleNearby = false;
	}

	private void updateChestClamLines()
	{
		chestClamLines.clear();

		if (targetWorldPoint == null)
		{
			return;
		}

		chestClamLines.add(client.getLocalPlayer().getWorldLocation());
		chestClamLines.add(new WorldPoint(targetWorldPoint.getX(), targetWorldPoint.getY(), 1));
	}

	private void updatePlayerAnimations()
	{
		if (!config.replaceSwimAnimation())
		{
			return;
		}

		final var player = client.getLocalPlayer();

		if (player.getIdlePoseAnimation() != 808)
		{
			player.setIdlePoseAnimation(808);
			player.setIdleRotateLeft(823);
			player.setIdleRotateRight(823);
			player.setWalkAnimation(819);
			player.setWalkRotateLeft(821);
			player.setWalkRotateRight(822);
			player.setWalkRotate180(820);
			player.setRunAnimation(824);
		}
	}

	void resetTears()
	{
		thieveSuccessCount = 0;
		thieveFailCount = 0;
		startTime = null;
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
		if (enabled && config.linesKey().matches(e))
		{
			keyPressed = true;
		}
	}

	@Override
	public void keyReleased(final KeyEvent e)
	{
		if (enabled && config.linesKey().matches(e))
		{
			keyPressed = false;
		}
	}
}
