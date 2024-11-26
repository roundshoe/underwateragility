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

import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.ModifierlessKeybind;
import net.runelite.client.util.ColorUtil;

@ConfigGroup(UWAConfig.CONFIG_GROUP)
public interface UWAConfig extends Config
{
	String CONFIG_GROUP = "underwateragility";

	int DEFAULT_ALPHA = 40;

	// Sections

	@ConfigSection(
		name = "General",
		description = "General configuration.",
		position = 0
	)
	String SECTION_GENERAL = "generalSection";

	@ConfigSection(
		name = "Information Panel",
		description = "Information panel configuration.",
		position = 1
	)
	String SECTION_INFORMATION_PANEL = "informationPanelSection";

	@ConfigSection(
		name = "Color",
		description = "Color configuration.",
		position = 2
	)
	String SECTION_COLOR = "colorSection";

	// General

	@ConfigItem(
		name = "Oxygen Overlay",
		description = "Overlay oxygen on the player.",
		position = 0,
		keyName = "oxygenBar",
		section = SECTION_GENERAL
	)
	default OxygenBar oxygenBar()
	{
		return OxygenBar.OFF;
	}

	@ConfigItem(
		name = "Obstacle Overlay",
		description = "Overlay obstacle clickboxes.",
		position = 1,
		keyName = "obstaclesOverlay",
		section = SECTION_GENERAL
	)
	default OutlineStyle obstaclesOverlay()
	{
		return OutlineStyle.OFF;
	}

	@ConfigItem(
		name = "Current Overlay",
		description = "Outline currents.",
		position = 2,
		keyName = "currentOverlay",
		section = SECTION_GENERAL
	)
	default boolean currentOverlay()
	{
		return false;
	}

	@ConfigItem(
		name = "Bubble Overlay",
		description = "Overlay bubbles.",
		position = 3,
		keyName = "bubbleOverlay",
		section = SECTION_GENERAL
	)
	default OverlayStyle bubbleOverlay()
	{
		return OverlayStyle.OFF;
	}

	@ConfigItem(
		name = "Chest/Clam Overlay",
		description = "Outline active chest/clam.",
		position = 4,
		keyName = "chestClamOverlay",
		section = SECTION_GENERAL
	)
	default OverlayStyle chestClamOverlay()
	{
		return OverlayStyle.OFF;
	}

	@ConfigItem(
		name = "Hole Overlay",
		description = "Outline holes.",
		position = 5,
		keyName = "holeOverlay",
		section = SECTION_GENERAL
	)
	default OverlayStyle holeOverlay()
	{
		return OverlayStyle.OFF;
	}

	@ConfigItem(
		name = "Puffer Fish Overlay",
		description = "Overlay puffer fish.",
		position = 6,
		keyName = "pufferFishOverlay",
		section = SECTION_GENERAL
	)
	default OverlayStyle pufferFishOverlay()
	{
		return OverlayStyle.OFF;
	}

	String CONFIG_KEY_CHEST_CLAM_TIMER = "chestClamTimer";

	@ConfigItem(
		name = "Chest/Clam Timer",
		description = "Display an infobox timer when the chest/clam moves.",
		position = 7,
		keyName = CONFIG_KEY_CHEST_CLAM_TIMER,
		section = SECTION_GENERAL
	)
	default boolean chestClamTimer()
	{
		return false;
	}

	@ConfigItem(
		name = "Chest/Clam Line",
		description = "Draw a line between the player and the chest/clam.",
		position = 8,
		keyName = "chestClamLine",
		section = SECTION_GENERAL
	)
	default boolean chestClamLine()
	{
		return false;
	}

	@ConfigItem(
		name = "Hole Lines",
		description = "Draw lines between connecting holes.",
		position = 9,
		keyName = "holeLines",
		section = SECTION_GENERAL
	)
	default boolean holeLines()
	{
		return false;
	}

	@ConfigItem(
		name = "Lines Require Key Press",
		description = "Draw lines only while holding down configured key.",
		position = 10,
		keyName = "linesPressKey",
		section = SECTION_GENERAL
	)
	default boolean linesPressKey()
	{
		return false;
	}

	@ConfigItem(
		name = "Lines Key",
		description = "The key to press to draw lines.",
		position = 11,
		keyName = "linesKey",
		section = SECTION_GENERAL
	)
	default ModifierlessKeybind linesKey()
	{
		return new ModifierlessKeybind(KeyEvent.VK_SHIFT, InputEvent.SHIFT_DOWN_MASK);
	}

	@ConfigItem(
		name = "Replace Swim Animation",
		description = "Replaces the swim animation with walking.",
		position = 12,
		keyName = "replaceSwimAnimation",
		section = SECTION_GENERAL
	)
	default boolean replaceSwimAnimation()
	{
		return false;
	}

	String CONFIG_KEY_HIDE_OXYGEN_WIDGET = "hideOxygenWidget";

	@ConfigItem(
		name = "Hide Oxygen Widget",
		description = "Hide the oxygen widget.",
		position = 13,
		keyName = CONFIG_KEY_HIDE_OXYGEN_WIDGET,
		section = SECTION_GENERAL
	)
	default boolean hideOxygenWidget()
	{
		return false;
	}

	String CONFIG_KEY_HIDE_WATER_WIDGET = "hideWaterWidget";

	@ConfigItem(
		name = "Hide Water Widget",
		description = "Hide the water widget.",
		position = 14,
		keyName = CONFIG_KEY_HIDE_WATER_WIDGET,
		section = SECTION_GENERAL
	)
	default boolean hideWaterWidget()
	{
		return false;
	}

	String CONFIG_KEY_HIDE_SCENERY = "hideScenery";

	@ConfigItem(
		name = "Hide Scenery",
		description = "Hide scenery.",
		position = 15,
		keyName = CONFIG_KEY_HIDE_SCENERY,
		section = SECTION_GENERAL
	)
	default boolean hideScenery()
	{
		return false;
	}

	// Information Panel

	@ConfigItem(
		name = "Information Panel Overlay",
		description = "Display an information panel overlay.",
		position = 0,
		keyName = "informationPanelOverlay",
		section = SECTION_INFORMATION_PANEL
	)
	default boolean informationPanelOverlay()
	{
		return false;
	}

	@ConfigItem(
		name = "Ticks Until Switch",
		description = "Show ticks remaining until next chest/clam switch.",
		position = 1,
		keyName = "infoPanelTicks",
		section = SECTION_INFORMATION_PANEL
	)
	default boolean infoPanelTicks()
	{
		return true;
	}

	@ConfigItem(
		name = "Distance From Chest",
		description = "Show distance between player's tile and active chest/clam.",
		position = 2,
		keyName = "infoPanelDistance",
		section = SECTION_INFORMATION_PANEL
	)
	default boolean infoPanelDistance()
	{
		return true;
	}

	@ConfigItem(
		name = "Oxygen Ticks",
		description = "Show oxygen ticks remaining.",
		position = 3,
		keyName = "infoPanelOxygen",
		section = SECTION_INFORMATION_PANEL
	)
	default boolean infoPanelOxygen()
	{
		return true;
	}

	@ConfigItem(
		name = "Tears Per Hour",
		description = "Show number of successful thieves per hour." +
			"<br>Right-click panel to reset.",
		position = 4,
		keyName = "infoPanelTearsPerHour",
		section = SECTION_INFORMATION_PANEL
	)
	default boolean infoPanelTearsPerHour()
	{
		return true;
	}

	@ConfigItem(
		name = "Elapsed",
		description = "Show time elapsed." +
			"<br>Right-click panel to reset.",
		position = 5,
		keyName = "infoPanelElapsed",
		section = SECTION_INFORMATION_PANEL
	)
	default boolean infoPanelElapsed()
	{
		return true;
	}

	@ConfigItem(
		name = "Bubbles Nearby",
		description = "Show if there is a bubble nearby the active chest.",
		position = 6,
		keyName = "infoPanelBubble",
		section = SECTION_INFORMATION_PANEL
	)
	default boolean infoPanelBubble()
	{
		return true;
	}

	// Color

	@Alpha
	@ConfigItem(
		name = "Obstacle Outline",
		description = "Obstacle outline color.",
		position = 0,
		keyName = "obstaclesOutlineColor",
		section = SECTION_COLOR
	)
	default Color obstaclesOutlineColor()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
		name = "Obstacle Fill",
		description = "Obstacle fill color.",
		position = 1,
		keyName = "obstaclesFillColor",
		section = SECTION_COLOR
	)
	default Color obstaclesFillColor()
	{
		return ColorUtil.colorWithAlpha(Color.GREEN, DEFAULT_ALPHA);
	}

	@Alpha
	@ConfigItem(
		name = "Current Outline",
		description = "Current outline color.",
		position = 2,
		keyName = "currentOutlineColor",
		section = SECTION_COLOR
	)
	default Color currentOutlineColor()
	{
		return ColorUtil.colorWithAlpha(Color.RED, 0);
	}

	@Alpha
	@ConfigItem(
		name = "Current Fill",
		description = "Current fill color.",
		position = 3,
		keyName = "currentFillColor",
		section = SECTION_COLOR
	)
	default Color currentFillColor()
	{
		return ColorUtil.colorWithAlpha(Color.RED, DEFAULT_ALPHA);
	}

	@Alpha
	@ConfigItem(
		name = "Bubble Outline",
		description = "Bubbles outline color.",
		position = 4,
		keyName = "bubbleOutlineColor",
		section = SECTION_COLOR
	)
	default Color bubbleOutlineColor()
	{
		return Color.CYAN;
	}

	@Alpha
	@ConfigItem(
		name = "Bubble Fill",
		description = "Bubbles fill color.",
		position = 5,
		keyName = "bubbleFillColor",
		section = SECTION_COLOR
	)
	default Color bubbleFillColor()
	{
		return ColorUtil.colorWithAlpha(Color.CYAN, DEFAULT_ALPHA);
	}

	@Alpha
	@ConfigItem(
		name = "Chest/Clam Outline",
		description = "Chest/clam outline color.",
		position = 6,
		keyName = "chestClamOutlineColor",
		section = SECTION_COLOR
	)
	default Color chestClamOutlineColor()
	{
		return Color.MAGENTA;
	}

	@Alpha
	@ConfigItem(
		name = "Chest/Clam Fill",
		description = "Chest/clam fill color.",
		position = 7,
		keyName = "chestClamFillColor",
		section = SECTION_COLOR
	)
	default Color chestClamFillColor()
	{
		return ColorUtil.colorWithAlpha(Color.MAGENTA, DEFAULT_ALPHA);
	}

	@Alpha
	@ConfigItem(
		name = "Hole Outline",
		description = "Hole outline color.",
		position = 8,
		keyName = "holeOutlineColor",
		section = SECTION_COLOR
	)
	default Color holeOutlineColor()
	{
		return Color.YELLOW;
	}

	@Alpha
	@ConfigItem(
		name = "Hole Fill",
		description = "Hole fill color.",
		position = 9,
		keyName = "holeFillColor",
		section = SECTION_COLOR
	)
	default Color holeFillColor()
	{
		return ColorUtil.colorWithAlpha(Color.YELLOW, DEFAULT_ALPHA);
	}

	@Alpha
	@ConfigItem(
		name = "Puffer Fish Outline",
		description = "Puffer fish outline color.",
		position = 10,
		keyName = "pufferFishOutlineColor",
		section = SECTION_COLOR
	)
	default Color pufferFishOutlineColor()
	{
		return Color.BLUE;
	}

	@Alpha
	@ConfigItem(
		name = "Puffer Fish Fill",
		description = "Puffer fish fill color.",
		position = 11,
		keyName = "pufferFishFillColor",
		section = SECTION_COLOR
	)
	default Color pufferFishFillColor()
	{
		return ColorUtil.colorWithAlpha(Color.BLUE, DEFAULT_ALPHA);
	}

	// enum

	enum OxygenBar
	{
		OFF,
		PERCENT,
		TICKS
	}

	enum OverlayStyle
	{
		OFF,
		SCENE,
		MINIMAP,
		BOTH
	}

	enum OutlineStyle
	{
		OFF,
		CLICKBOX,
		TILE
	}
}
